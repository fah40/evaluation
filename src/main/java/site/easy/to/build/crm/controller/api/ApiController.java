package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Depense;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.DepenseRepository;
import site.easy.to.build.crm.repository.TicketRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5000")
public class ApiController {

    private final CustomerRepository customerRepository;
    private final DepenseRepository depenseRepository;
    private final BudgetRepository budgetRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public ApiController(TicketRepository ticketRepository,CustomerRepository customerRepository, DepenseRepository depenseRepository, BudgetRepository budgetRepository) {
        this.customerRepository = customerRepository;
        this.depenseRepository = depenseRepository;
        this.budgetRepository = budgetRepository;
        this.ticketRepository = ticketRepository;
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

    // Liste des budgets
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
}