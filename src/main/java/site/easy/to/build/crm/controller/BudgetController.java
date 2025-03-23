package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerService;

import javax.validation.Valid;

import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final CustomerService customerService;

    @Autowired
    public BudgetController(BudgetService budgetService, CustomerService customerService) {
        this.budgetService = budgetService;
        this.customerService = customerService;
    }

    // Afficher le formulaire de création
    @GetMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public String showBudgetCreationForm(Model model) {
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("budget", new Budget());
        return "budget/budget-create";
    }

    // Sauvegarder un budget
    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public String saveBudget(
            @Valid @ModelAttribute("budget") Budget budget,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Customer> customers = customerService.findAll();
            model.addAttribute("customers", customers);
            return "budget/budget-create";
        }
        try {
            budgetService.save(budget);
            redirectAttributes.addFlashAttribute("mess", "Budget créé avec succès !");
            return "redirect:/budget/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création du budget : " + e.getMessage());
            return "redirect:/budget/create";
        }
    }

    // Afficher la liste des budgets avec filtre par client
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public String showBudgetList(
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {
        List<Budget> budgets;

        if (customerId != null && customerId > 0) {
            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                Date sqlStartDate = Date.valueOf(startDate);
                Date sqlEndDate = Date.valueOf(endDate);
                budgets = budgetService.findByCustomerIdAndDateRange(customerId, sqlStartDate, sqlEndDate);
            } else {
                budgets = budgetService.findByCustomerId(customerId);
            }
        } else if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            Date sqlStartDate = Date.valueOf(startDate);
            Date sqlEndDate = Date.valueOf(endDate);
            budgets = budgetService.findByDateRange(sqlStartDate, sqlEndDate);
        } else {
            budgets = budgetService.findAll();
        }

        List<Customer> customers = customerService.findAll();
        model.addAttribute("budgets", budgets);
        model.addAttribute("customers", customers);
        model.addAttribute("selectedCustomerId", customerId);
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);
        return "budget/budget-list";
    }

    // Afficher le formulaire de modification
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public String showBudgetEditForm(@PathVariable("id") int id, Model model) {
        Budget budget = budgetService.findById(id);
        if (budget == null) {
            return "redirect:/budget/list"; // Redirige si le budget n'existe pas
        }
        List<Customer> customers = customerService.findAll();
        model.addAttribute("budget", budget);
        model.addAttribute("customers", customers);
        return "budget/budget-edit";
    }

    // Mettre à jour un budget
    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public String updateBudget(
            @Valid @ModelAttribute("budget") Budget budget,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Customer> customers = customerService.findAll();
            model.addAttribute("customers", customers);
            return "budget/budget-edit";
        }
        try {
            budgetService.save(budget); // Sauvegarde les modifications
            redirectAttributes.addFlashAttribute("mess", "Budget mis à jour avec succès !");
            return "redirect:/budget/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour du budget : " + e.getMessage());
            return "redirect:/budget/edit/" + budget.getId();
        }
    }
}