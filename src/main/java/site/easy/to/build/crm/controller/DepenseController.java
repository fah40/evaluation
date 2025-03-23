package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Depense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.depense.DepenseService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;

import javax.validation.Valid;

import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/depense")
public class DepenseController {

    private final DepenseService depenseService;
    private final LeadService leadService;
    private final TicketService ticketService;
    private final CustomerService customerService;

    @Autowired
    public DepenseController(DepenseService depenseService, LeadService leadService, 
                           TicketService ticketService, CustomerService customerService) {
        this.depenseService = depenseService;
        this.leadService = leadService;
        this.ticketService = ticketService;
        this.customerService = customerService;
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public String showDepenseCreationForm(Model model) {
        List<Lead> leads = leadService.findAll();
        List<Ticket> tickets = ticketService.findAll();
        model.addAttribute("leads", leads);
        model.addAttribute("tickets", tickets);
        model.addAttribute("depense", new Depense());
        return "depense/depense-create";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public String saveDepense(
            @Valid @ModelAttribute("depense") Depense depense,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Lead> leads = leadService.findAll();
            List<Ticket> tickets = ticketService.findAll();
            model.addAttribute("leads", leads);
            model.addAttribute("tickets", tickets);
            return "depense/depense-create";
        }
        try {
            depenseService.save(depense);
            redirectAttributes.addFlashAttribute("mess", "Dépense créée avec succès !");
            return "redirect:/depense/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création de la dépense : " + e.getMessage());
            return "redirect:/depense/create";
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public String showDepenseList(
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {
        List<Depense> depenses;
        
        if (customerId != null && customerId > 0) {
            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                Date sqlStartDate = Date.valueOf(startDate);
                Date sqlEndDate = Date.valueOf(endDate);
                depenses = depenseService.findByDateRange(sqlStartDate, sqlEndDate)
                    .stream()
                    .filter(d -> (d.getLead() != null && d.getLead().getCustomer().getCustomerId() == customerId) ||
                                (d.getTicket() != null && d.getTicket().getCustomer().getCustomerId() == customerId))
                    .toList();
            } else {
                depenses = depenseService.findAll()
                    .stream()
                    .filter(d -> (d.getLead() != null && d.getLead().getCustomer().getCustomerId() == customerId) ||
                                (d.getTicket() != null && d.getTicket().getCustomer().getCustomerId() == customerId))
                    .toList();
            }
        } else if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            Date sqlStartDate = Date.valueOf(startDate);
            Date sqlEndDate = Date.valueOf(endDate);
            depenses = depenseService.findByDateRange(sqlStartDate, sqlEndDate);
        } else {
            depenses = depenseService.findAll();
        }

        List<Customer> customers = customerService.findAll();
        model.addAttribute("depenses", depenses);
        model.addAttribute("customers", customers);
        model.addAttribute("selectedCustomerId", customerId);
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);
        return "depense/depense-list";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public String showDepenseEditForm(@PathVariable("id") int id, Model model) {
        Depense depense = depenseService.findById(id);
        if (depense == null) {
            return "redirect:/depense/list";
        }
        List<Lead> leads = leadService.findAll();
        List<Ticket> tickets = ticketService.findAll();
        model.addAttribute("depense", depense);
        model.addAttribute("leads", leads);
        model.addAttribute("tickets", tickets);
        return "depense/depense-edit";
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public String updateDepense(
            @Valid @ModelAttribute("depense") Depense depense,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Lead> leads = leadService.findAll();
            List<Ticket> tickets = ticketService.findAll();
            model.addAttribute("leads", leads);
            model.addAttribute("tickets", tickets);
            return "depense/depense-edit";
        }
        try {
            depenseService.save(depense);
            redirectAttributes.addFlashAttribute("mess", "Dépense mise à jour avec succès !");
            return "redirect:/depense/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de la dépense : " + e.getMessage());
            return "redirect:/depense/edit/" + depense.getId();
        }
    }
}