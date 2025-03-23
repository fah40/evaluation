package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import site.easy.to.build.crm.entity.Budget;

import java.sql.Date;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    // Find all budgets for a specific customer
    List<Budget> findByCustomerCustomerId(int customerId);

    // Find budgets by date range
    List<Budget> findByDateBetween(Date startDate, Date endDate);

    List<Budget> findByCustomerCustomerIdAndDateBetween(int customerId, Date startDate, Date endDate);

    Budget findTopByCustomerCustomerIdOrderByDateDesc(int customerId);

    @Query("SELECT SUM(b.value) FROM Budget b")
    Double getTotalBudget();

    // Pour un customer spécifique
    @Query("SELECT SUM(b.value) FROM Budget b WHERE b.customer.customerId = :customerId")
    Double getTotalBudgetByCustomerId(int customerId);
}