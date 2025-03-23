package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.entity.TypeConfig;
import site.easy.to.build.crm.service.config.TypeConfigService;
import site.easy.to.build.crm.util.AuthenticationUtils;
import site.easy.to.build.crm.util.AuthorizationUtil;

import java.util.List;

@Controller
@RequestMapping("/type-config")
public class TypeConfigController {

    private final TypeConfigService typeConfigService;
    private final AuthenticationUtils authenticationUtils;

    @Autowired
    public TypeConfigController(TypeConfigService typeConfigService, AuthenticationUtils authenticationUtils) {
        this.typeConfigService = typeConfigService;
        this.authenticationUtils = authenticationUtils;
    }

    // Afficher le formulaire de création
    @GetMapping("/create")
    public String showCreateForm(Model model, Authentication authentication) {
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "error/access-denied";
        }
        model.addAttribute("typeConfig", new TypeConfig());
        return "config/create-type-config";
    }

    // Créer un TypeConfig
    @PostMapping("/create")
    public String createTypeConfig(@ModelAttribute("typeConfig") @Validated TypeConfig typeConfig, BindingResult bindingResult,
                                   Model model, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "error/access-denied";
        }

        if (bindingResult.hasErrors()) {
            return "config/create-type-config";
        }

        try {
            typeConfigService.save(typeConfig);
            redirectAttributes.addFlashAttribute("mess", "TypeConfig created successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/type-config/create";
        }

        return "redirect:/type-config/list";
    }

    // Afficher la liste des TypeConfig
    @GetMapping("/list")
    public String listTypeConfig(Model model, Authentication authentication) {
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "error/access-denied";
        }

        List<TypeConfig> typeConfigs = typeConfigService.findAll();
        model.addAttribute("typeConfigs", typeConfigs);
        return "config/list-type-config";
    }

    // Afficher le formulaire de mise à jour
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model, Authentication authentication) {
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "error/access-denied";
        }

        TypeConfig typeConfig = typeConfigService.findById(id);
        if (typeConfig == null) {
            return "error/not-found";
        }

        model.addAttribute("typeConfig", typeConfig);
        return "config/update-type-config";
    }

    // Mettre à jour un TypeConfig
    @PostMapping("/update")
    public String updateTypeConfig(@ModelAttribute("typeConfig") @Validated TypeConfig typeConfig, BindingResult bindingResult,
                                   Model model, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "error/access-denied";
        }

        if (bindingResult.hasErrors()) {
            return "config/update-type-config";
        }

        TypeConfig existingTypeConfig = typeConfigService.findById(typeConfig.getId());
        if (existingTypeConfig == null) {
            return "error/not-found";
        }

        try {
            typeConfigService.save(typeConfig);
            redirectAttributes.addFlashAttribute("mess", "TypeConfig updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/type-config/update/" + typeConfig.getId();
        }

        return "redirect:/type-config/list";
    }
}