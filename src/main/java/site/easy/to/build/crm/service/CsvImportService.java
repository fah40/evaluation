package site.easy.to.build.crm.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Role;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.RoleRepository;
import site.easy.to.build.crm.repository.UserRepository;

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
    public void importCustomersFromCsv(MultipartFile file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
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

                // Si vous voulez lier à un user_id existant
                if (record.isSet("user_id")) {
                    int userId = Integer.parseInt(record.get("user_id"));
                    User user = userRepository.findById(userId);
                    if (user != null) {
                        customer.setUser(user);
                    } else {
                        throw new Exception("User with ID " + userId + " not found");
                    }
                }

                customerRepository.save(customer);
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'importation des clients : " + e.getMessage(), e);
        }
    }
}