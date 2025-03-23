package site.easy.to.build.crm.service.budget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.repository.BudgetRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    // Save a new budget
    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    // Find budget by ID
    public Budget findById(int id) {
        Optional<Budget> budget = budgetRepository.findById(id);
        return budget.orElse(null);
    }

    // Find all budgets
    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    // Find budgets by customer ID
    public List<Budget> findByCustomerId(int customerId) {
        return budgetRepository.findByCustomerCustomerId(customerId);
    }

    // Find budgets by date range
    public List<Budget> findByDateRange(Date startDate, Date endDate) {
        return budgetRepository.findByDateBetween(startDate, endDate);
    }

    // Find budgets by customer ID and date range
    public List<Budget> findByCustomerIdAndDateRange(int customerId, Date startDate, Date endDate) {
        return budgetRepository.findByCustomerCustomerIdAndDateBetween(customerId, startDate, endDate);
    }

    // Delete a budget
    public void delete(int id) {
        budgetRepository.deleteById(id);
    }
    
}