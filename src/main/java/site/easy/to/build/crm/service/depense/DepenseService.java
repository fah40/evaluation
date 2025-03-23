package site.easy.to.build.crm.service.depense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Depense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.repository.DepenseRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DepenseService {

    private final DepenseRepository depenseRepository;

    @Autowired
    public DepenseService(DepenseRepository depenseRepository) {
        this.depenseRepository = depenseRepository;
    }

    public Depense save(Depense depense) {
        // Validation du CHECK : un seul des deux (lead ou ticket) doit être non null
        if ((depense.getLead() != null && depense.getTicket() != null) || 
            (depense.getLead() == null && depense.getTicket() == null)) {
            throw new IllegalArgumentException("Exactly one of lead or ticket must be specified.");
        }
        return depenseRepository.save(depense);
    }

    public Depense findById(int id) {
        Optional<Depense> depense = depenseRepository.findById(id);
        return depense.orElse(null);
    }

    public List<Depense> findAll() {
        return depenseRepository.findAll();
    }

    public List<Depense> findByLeadId(int leadId) {
        return depenseRepository.findByLeadLeadId(leadId);
    }

    public List<Depense> findByTicketId(int ticketId) {
        return depenseRepository.findByTicketTicketId(ticketId);
    }

    public List<Depense> findByDateRange(Date startDate, Date endDate) {
        return depenseRepository.findByDateBetween(startDate, endDate);
    }

    public Depense findByTicket(Ticket ticket) {
        return depenseRepository.findByTicket(ticket);
    }

    public Depense findByLead(Lead lead) {
        return depenseRepository.findByLead(lead);
    }

    public void delete(int id) {
        depenseRepository.deleteById(id);
    }
}