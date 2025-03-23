package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.entity.Configuration;
import site.easy.to.build.crm.entity.TypeConfig;
import site.easy.to.build.crm.service.config.ConfigurationService;
import site.easy.to.build.crm.service.config.TypeConfigService;
import site.easy.to.build.crm.util.AuthorizationUtil;

import java.util.List;

@Controller
@RequestMapping("/config")
public class ConfigurationController {

    private final ConfigurationService configurationService;
    private final TypeConfigService typeConfigService;

    @Autowired
    public ConfigurationController(ConfigurationService configurationService, TypeConfigService typeConfigService) {
        this.configurationService = configurationService;
        this.typeConfigService = typeConfigService;
    }

    // Afficher le formulaire de création
    @GetMapping("/create")
    public String showCreateForm(Model model, Authentication authentication) {
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "error/access-denied";
        }
        List<TypeConfig> typeConfigs = typeConfigService.findAll();
        model.addAttribute("configuration", new Configuration());
        model.addAttribute("typeConfigs", typeConfigs);
        return "config/create-configuration";
    }

    // Créer une Configuration
    @PostMapping("/create")
    public String createConfiguration(@ModelAttribute("configuration") @Validated Configuration configuration,
                                    @RequestParam("typeConfigId") int typeConfigId, BindingResult bindingResult,
                                    Model model, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "error/access-denied";
        }

        TypeConfig typeConfig = typeConfigService.findById(typeConfigId);
        if (typeConfig == null) {
            redirectAttributes.addAttribute("error", "Invalid TypeConfig ID");
            return "redirect:/config/create";
        }
        configuration.setTypeConfig(typeConfig);

        // Vérifier si value est un entier valide
        String value = configuration.getValue();
        if (value != null && !value.trim().isEmpty()) {
            try {
                Integer.parseInt(value); // Tente de parser value en Integer
            } catch (NumberFormatException e) {
                redirectAttributes.addAttribute("error", "Value must be a valid integer");
                List<TypeConfig> typeConfigs = typeConfigService.findAll();
                model.addAttribute("typeConfigs", typeConfigs);
                model.addAttribute("configuration", configuration); // Préserver les données saisies
                return "config/create-configuration"; // Retourne au formulaire avec erreur
            }
        } else {
            redirectAttributes.addAttribute("error", "Value cannot be empty");
            return "redirect:/config/create";
        }

        if (bindingResult.hasErrors()) {
            List<TypeConfig> typeConfigs = typeConfigService.findAll();
            model.addAttribute("typeConfigs", typeConfigs);
            return "config/create-configuration";
        }

        try {
            configurationService.save(configuration);
            redirectAttributes.addFlashAttribute("mess", "Configuration created successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/config/create";
        }

        return "redirect:/config/list";
    }

    // Afficher la liste des Configurations
    @GetMapping("/list")
    public String listConfigurations(Model model, Authentication authentication) {
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "error/access-denied";
        }

        List<Configuration> configurations = configurationService.findAll();
        model.addAttribute("configurations", configurations);
        return "config/list-configuration";
    }

    // Afficher le formulaire de mise à jour
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model, Authentication authentication) {
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "error/access-denied";
        }

        Configuration configuration = configurationService.findById(id);
        if (configuration == null) {
            return "error/not-found";
        }

        List<TypeConfig> typeConfigs = typeConfigService.findAll();
        model.addAttribute("configuration", configuration);
        model.addAttribute("typeConfigs", typeConfigs);
        return "config/update-configuration";
    }

    // Mettre à jour une Configuration
    @PostMapping("/update")
    public String updateConfiguration(@ModelAttribute("configuration") @Validated Configuration configuration,
                                      @RequestParam("typeConfigId") int typeConfigId, BindingResult bindingResult,
                                      Model model, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "error/access-denied";
        }

        TypeConfig typeConfig = typeConfigService.findById(typeConfigId);
        if (typeConfig == null) {
            redirectAttributes.addAttribute("error", "Invalid TypeConfig ID");
            return "redirect:/config/update/" + configuration.getId();
        }
        configuration.setTypeConfig(typeConfig);

        if (bindingResult.hasErrors()) {
            List<TypeConfig> typeConfigs = typeConfigService.findAll();
            model.addAttribute("typeConfigs", typeConfigs);
            return "config/update-configuration";
        }

        Configuration existingConfiguration = configurationService.findById(configuration.getId());
        if (existingConfiguration == null) {
            return "error/not-found";
        }


        // Vérifier si value est un entier valide
        String value = configuration.getValue();
        if (value != null && !value.trim().isEmpty()) {
            try {
                Integer.parseInt(value); // Tente de parser value en Integer
            } catch (NumberFormatException e) {
                redirectAttributes.addAttribute("error", "Value must be a valid integer");
                List<TypeConfig> typeConfigs = typeConfigService.findAll();
                model.addAttribute("typeConfigs", typeConfigs);
                model.addAttribute("configuration", configuration); // Préserver les données saisies
                return "config/create-configuration"; // Retourne au formulaire avec erreur
            }
        } else {
            redirectAttributes.addAttribute("error", "Value cannot be empty");
            return "redirect:/config/create";
        }

        try {
            configurationService.save(configuration);
            redirectAttributes.addFlashAttribute("mess", "Configuration updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/config/update/" + configuration.getId();
        }

        return "redirect:/config/list";
    }
}