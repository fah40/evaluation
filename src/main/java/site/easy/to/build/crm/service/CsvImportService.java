package site.easy.to.build.crm.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerLoginInfo;
import site.easy.to.build.crm.entity.OAuthUser;
import site.easy.to.build.crm.entity.Role;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.google.service.acess.GoogleAccessService;
import site.easy.to.build.crm.google.service.gmail.GoogleGmailApiService;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.RoleRepository;
import site.easy.to.build.crm.repository.UserRepository;
import site.easy.to.build.crm.service.customer.CustomerLoginInfoService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;
import site.easy.to.build.crm.util.EmailTokenUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvImportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final CustomerService customerService;
    private final CustomerLoginInfoService customerLoginInfoService;
    private final GoogleGmailApiService googleGmailApiService;
    private final Environment environment;

    @Autowired
    public CsvImportService(RoleRepository roleRepository, CustomerService customerService, CustomerLoginInfoService customerLoginInfoService, GoogleGmailApiService googleGmailApiService, Environment environment) {
        this.roleRepository = roleRepository;
        this.customerService = customerService;
        this.customerLoginInfoService = customerLoginInfoService;
        this.googleGmailApiService = googleGmailApiService;
        this.environment = environment;
    }

    @Transactional
    public void importUsersFromCsv(MultipartFile file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                User user = new User();

                // Champs obligatoires avec validation
                user.setEmail(record.get("email"));
                user.setUsername(record.get("username"));
                user.setPassword(record.get("password")); // À hasher en production
                user.setStatus(record.get("status"));

                // Champs optionnels
                if (record.isSet("hire_date")) {
                    user.setHireDate(LocalDate.parse(record.get("hire_date")));
                }
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                // Correction ici : utiliser setPasswordSet au lieu de setIsPasswordSet
                user.setPasswordSet(record.isSet("is_password_set") && 
                                   Boolean.parseBoolean(record.get("is_password_set")));

                // Gestion des rôles
                if (record.isSet("roles")) {
                    String rolesStr = record.get("roles");
                    List<String> roleNames = Arrays.asList(rolesStr.split(","));
                    List<Role> roles = new ArrayList<>();
                    for (String roleName : roleNames) {
                        Role role = roleRepository.findByName(roleName.trim());
                        if (role != null) {
                            roles.add(role);
                        } else {
                            throw new Exception("Role not found: " + roleName);
                        }
                    }
                    user.setRoles(roles);
                } else {
                    // Ajouter un rôle par défaut si aucun n'est spécifié
                    Role defaultRole = roleRepository.findByName("ROLE_USER");
                    if (defaultRole != null) {
                        user.setRoles(new ArrayList<>(List.of(defaultRole)));
                    } else {
                        throw new Exception("Default role 'ROLE_USER' not found");
                    }
                }

                // Sauvegarde dans la base de données
                userRepository.save(user);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'importation des utilisateurs : " + e.getMessage(), e);
        }
    }

    @Transactional
    public String importCustomersFromCsv(MultipartFile file,Authentication authentication,
                                                          AuthenticationUtils authenticationUtils,
                                                          UserService userService,
                                                          RedirectAttributes redirectAttributes) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            // Vérification de l'utilisateur connecté
            int userId = authenticationUtils.getLoggedInUserId(authentication);
            User user = userService.findById(userId);
            if (user.isInactiveUser()) {
                redirectAttributes.addFlashAttribute("error", "Votre compte est inactif.");
                return "redirect:/";
            }

            // Détection si l'utilisateur est un utilisateur Google (OAuth)
            boolean isGoogleUser = !(authentication instanceof UsernamePasswordAuthenticationToken);
            boolean hasGoogleGmailAccess = false;
            OAuthUser oAuthUser = null;
            if (isGoogleUser) {
                oAuthUser = authenticationUtils.getOAuthUserFromAuthentication(authentication);
                if (oAuthUser.getGrantedScopes().contains(GoogleAccessService.SCOPE_GMAIL)) {
                    hasGoogleGmailAccess = true;
                }
            }

            for (CSVRecord record : csvParser) {
                saveSingleCustomer(record, user, hasGoogleGmailAccess, oAuthUser);
                // customerRepository.save(customer);
            }
            return "redirect:/";
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'importation des clients : " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void saveSingleCustomer(CSVRecord record, User user, boolean hasGoogleGmailAccess, OAuthUser oAuthUser) throws Exception {
        Customer customer = new Customer();
        customer.setName(record.get("name"));
        customer.setPhone(record.get("phone"));
        customer.setAddress(record.get("address"));
        customer.setCity(record.get("city"));
        customer.setState(record.get("state"));
        customer.setCountry(record.get("country"));
        customer.setEmail(record.get("email"));
        customer.setDescription(record.get("description"));
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUser(user);

        // Créer CustomerLoginInfo
        CustomerLoginInfo customerLoginInfo = new CustomerLoginInfo();
        customerLoginInfo.setEmail(customer.getEmail());
        String token = EmailTokenUtils.generateToken();
        customerLoginInfo.setToken(token);
        customerLoginInfo.setPasswordSet(false);

        // Sauvegarder CustomerLoginInfo
        CustomerLoginInfo savedCustomerLoginInfo = customerLoginInfoService.save(customerLoginInfo);
        customer.setCustomerLoginInfo(savedCustomerLoginInfo);

        // Sauvegarder le client
        Customer createdCustomer = customerService.save(customer);
        savedCustomerLoginInfo.setCustomer(createdCustomer);
        customerLoginInfoService.save(savedCustomerLoginInfo);

        // Envoi d'email si applicable
        if (hasGoogleGmailAccess && oAuthUser != null && googleGmailApiService != null) {
            String baseUrl = environment.getProperty("app.base-url");
            String url = baseUrl + "set-password?token=" + customerLoginInfo.getToken();
            EmailTokenUtils.sendRegistrationEmail(
                savedCustomerLoginInfo.getEmail(), 
                customer.getName(), 
                url, 
                oAuthUser, 
                googleGmailApiService
            );
        }
    }
}