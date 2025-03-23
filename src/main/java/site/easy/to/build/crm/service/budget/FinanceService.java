package site.easy.to.build.crm.service.budget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.repository.DepenseRepository;

@Service
public class FinanceService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private DepenseRepository depenseRepository;

    public Double calculateTotalBudget() {
        Double total = budgetRepository.getTotalBudget();
        return total != null ? total : 0.0;
    }

    public Double calculateTotalDepense() {
        Double total = depenseRepository.getTotalDepense();
        return total != null ? total : 0.0;
    }

    public Double calculateTotalBudgetByCustomer(int customerId) {
        Double total = budgetRepository.getTotalBudgetByCustomerId(customerId);
        return total != null ? total : 0.0;
    }

    public Double calculateTotalDepenseByCustomer(int customerId) {
        Double leadTotal = depenseRepository.getTotalDepenseByLeadCustomerId(customerId);
        Double ticketTotal = depenseRepository.getTotalDepenseByTicketCustomerId(customerId);
        leadTotal = leadTotal != null ? leadTotal : 0.0;
        ticketTotal = ticketTotal != null ? ticketTotal : 0.0;
        return leadTotal + ticketTotal;
    }

    public void updateThresholdExceededStatus(int customerId, double percentageThreshold) {
        Budget latestBudget = budgetRepository.findTopByCustomerCustomerIdOrderByDateDesc(customerId);
        if (latestBudget != null) {
            Double totalDepense = calculateTotalDepenseByCustomer(customerId);
            double threshold = latestBudget.getValue() * (percentageThreshold / 100.0);
            boolean isExceeded = totalDepense >= threshold;
            latestBudget.setIsDepass(isExceeded);
            budgetRepository.save(latestBudget);
        }
    }
}