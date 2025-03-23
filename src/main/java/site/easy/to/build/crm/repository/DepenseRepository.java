package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import site.easy.to.build.crm.entity.Depense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;

import java.sql.Date;
import java.util.List;

public interface DepenseRepository extends JpaRepository<Depense, Integer> {

    List<Depense> findByLeadLeadId(int leadId);

    List<Depense> findByTicketTicketId(int ticketId);
    
    Depense findByTicket(Ticket ticket);
    
    Depense findByLead(Lead ticket);

    List<Depense> findByDateBetween(Date startDate, Date endDate);

    @Query("SELECT SUM(d.value) FROM Depense d")
    Double getTotalDepense();

    // Pour un customer spécifique (via lead ou ticket)
    @Query("SELECT SUM(d.value) FROM Depense d WHERE d.lead.customer.customerId = :customerId OR d.ticket.customer.customerId = :customerId")
    Double getTotalDepenseByCustomerId(int customerId);

    @Query("SELECT SUM(d.value) FROM Depense d WHERE d.lead.customer.customerId = :customerId")
    Double getTotalDepenseByLeadCustomerId(int customerId);

    @Query("SELECT SUM(d.value) FROM Depense d WHERE d.ticket.customer.customerId = :customerId")
    Double getTotalDepenseByTicketCustomerId(int customerId);

}