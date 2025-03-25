package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Configuration;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Depense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.TypeConfig;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.repository.ConfigurationRepository;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.DepenseRepository;
import site.easy.to.build.crm.repository.LeadRepository;
import site.easy.to.build.crm.repository.TicketRepository;
import site.easy.to.build.crm.repository.TypeConfigRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5000")
public class ApiController {

    private final CustomerRepository customerRepository;
    private final BudgetRepository budgetRepository;
    private final DepenseRepository depenseRepository;
    private final TicketRepository ticketRepository;
    private final LeadRepository leadRepository;
    private final ConfigurationRepository configurationRepository;
    private final TypeConfigRepository typeConfigRepository;
    @Autowired
    public ApiController(CustomerRepository customerRepository, BudgetRepository budgetRepository, 
                         DepenseRepository depenseRepository, TicketRepository ticketRepository,
                         LeadRepository leadRepository, ConfigurationRepository configurationRepository,
                         TypeConfigRepository typeConfigRepository) {
        this.customerRepository = customerRepository;
        this.budgetRepository = budgetRepository;
        this.depenseRepository = depenseRepository;
        this.ticketRepository = ticketRepository;
        this.leadRepository = leadRepository;
        this.configurationRepository = configurationRepository;
        this.typeConfigRepository = typeConfigRepository;
    }

    // Liste des clients
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.ok(customers);
    }

    // Liste des dépenses
    @GetMapping("/depenses")
    public ResponseEntity<List<Depense>> getAllDepenses() {
        List<Depense> depenses = depenseRepository.findAll();
        return ResponseEntity.ok(depenses);
    }

    // Liste des budgets
    @GetMapping("/budgets")
    public ResponseEntity<List<Budget>> getAllBudgets() {
        List<Budget> budgets = budgetRepository.findAll();
        return ResponseEntity.ok(budgets);
    }

    
    @GetMapping("/tickets/{customerId}")
    public ResponseEntity<List<Ticket>> getAllTicketsByCustomerId(@PathVariable("customerId") int customerId) {
        List<Ticket> tickets = ticketRepository.findByCustomerCustomerId(customerId);
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retourne 204 si aucun ticket n'est trouvé
        }
        return ResponseEntity.ok(tickets); // Retourne 200 avec la liste des tickets
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return ResponseEntity.ok(tickets);
    }
    
    @PutMapping("/depenses/{depenseId}")
    public ResponseEntity<Depense> updateDepenseValue(@PathVariable int depenseId, @RequestBody double newValue) {
        Depense depense = depenseRepository.findById(depenseId)
            .orElseThrow(() -> new RuntimeException("Depense not found"));
        depense.setValue(newValue);
        LocalDate localDate = LocalDate.now();

        // Convertissez LocalDate en java.sql.Date
        Date sqlDate = Date.valueOf(localDate);
        depense.setDate(sqlDate);
        Depense updatedDepense = depenseRepository.save(depense);
        return ResponseEntity.ok(updatedDepense);
    }

     // Supprimer un Ticket par ID
    @DeleteMapping("/tickets/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable int ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            return ResponseEntity.notFound().build();
        }
        // Supprimer d'abord la Depense associée, si elle existe
        Depense depense = depenseRepository.findByTicketTicketId(ticketId);
        if (depense != null) {
            depenseRepository.delete(depense);
        }
        ticketRepository.deleteById(ticketId);
        return ResponseEntity.noContent().build();
    }

    // Supprimer un Lead par ID
    @DeleteMapping("/leads/{leadId}")
    public ResponseEntity<Void> deleteLead(@PathVariable int leadId) {
        if (!leadRepository.existsById(leadId)) {
            return ResponseEntity.notFound().build();
        }
        // Supprimer d'abord la Depense associée, si elle existe
        Depense depense = depenseRepository.findByLeadLeadId(leadId);
        if (depense != null) {
            depenseRepository.delete(depense);
        }
        leadRepository.deleteById(leadId);
        return ResponseEntity.noContent().build();
    }



    // Tous les leads
    @GetMapping("/leads")
    public ResponseEntity<List<Lead>> getAllLeads() {
        List<Lead> leads = leadRepository.findAll();
        return ResponseEntity.ok(leads);
    }

    // Leads par customerId
    @GetMapping("/leads/{customerId}")
    public ResponseEntity<List<Lead>> getLeadsByCustomerId(@PathVariable int customerId) {
        List<Lead> leads = leadRepository.findByCustomerCustomerId(customerId);
        if (leads.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(leads);
    }




    // Toutes les configurations
    @GetMapping("/configurations")
    public ResponseEntity<List<Configuration>> getAllConfigurations() {
        List<Configuration> configurations = configurationRepository.findAll();
        return ResponseEntity.ok(configurations);
    }

    // Configurations par typeConfigId
    @GetMapping("/configurations/{typeConfigId}")
    public ResponseEntity<List<Configuration>> getConfigurationsByTypeConfigId(@PathVariable int typeConfigId) {
        List<Configuration> configurations = configurationRepository.findByTypeConfigId(typeConfigId);
        if (configurations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(configurations);
    }

    // Modifier la valeur d'une configuration
    @PutMapping("/configurations/{id}")
    public ResponseEntity<Configuration> updateConfigurationValue(@PathVariable int id, @RequestBody String newValue) {
        Configuration configuration = configurationRepository.findById(id);
        if (configuration == null) {
            throw new RuntimeException("Configuration not found");
        }
        configuration.setValue(newValue);
        Configuration updatedConfiguration = configurationRepository.save(configuration);
        return ResponseEntity.ok(updatedConfiguration);
    }

    // Dans ApiController.java
    @GetMapping("/type-configs")
    public ResponseEntity<List<TypeConfig>> getAllTypeConfigs() {
        List<TypeConfig> typeConfigs = typeConfigRepository.findAll();
        return ResponseEntity.ok(typeConfigs);
    }
}