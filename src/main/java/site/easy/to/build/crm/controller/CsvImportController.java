package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.service.CsvImportService;

@Controller
@RequestMapping("/csv-import")
public class CsvImportController {

    @Autowired
    private CsvImportService csvImportService;

    // Page d'affichage du formulaire d'importation (optionnel)
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showImportPage(Model model) {
        return "csv-import"; // Nom de la vue Thymeleaf (à créer)
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String importUsers(@RequestParam("file") MultipartFile file, 
                            Model model, 
                            RedirectAttributes redirectAttributes) {
        try {
            csvImportService.importUsersFromCsv(file);
            redirectAttributes.addFlashAttribute("mess", "Utilisateurs importés avec succès !");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'importation des utilisateurs : " + e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/customers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String importCustomers(@RequestParam("file") MultipartFile file, 
                                 Model model, 
                                 RedirectAttributes redirectAttributes) {
        try {
            csvImportService.importCustomersFromCsv(file);
            redirectAttributes.addFlashAttribute("mess", "Clients importés avec succès !");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'importation des clients : " + e.getMessage());
            e.printStackTrace();
            return "redirect:/";
        }
    }
}