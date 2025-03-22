package site.easy.to.build.crm.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.service.DatabaseResetService;

@Controller
@RequestMapping("/reset") // Note : "/api/reset" est plus pour une API REST
public class DatabaseResetController {

    private final DatabaseResetService databaseResetService;

    public DatabaseResetController(DatabaseResetService databaseResetService) {
        this.databaseResetService = databaseResetService;
    }

    @GetMapping("/tables")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String resetTables(Model model, RedirectAttributes redirectAttributes) {
        try {
            // Réinitialiser les tables (supposons que resetAllTables existe)
            databaseResetService.resetTables();
            redirectAttributes.addFlashAttribute("mess", "Tables réinitialisées avec succès !"); // Message de succès
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la réinitialisation");
            return "redirect:/";
        }
    }
}