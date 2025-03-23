package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.sql.Date;

@Entity
@Table(name = "depense")
public class Depense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "lead_id", columnDefinition = "INT UNSIGNED")
    private Lead lead;

    @ManyToOne
    @JoinColumn(name = "ticket_id", columnDefinition = "INT UNSIGNED")
    private Ticket ticket;

    @Column(name = "date", nullable = false)
    @NotNull(message = "Date is required")
    private Date date;

    @Column(name = "value", nullable = false)
    @NotNull(message = "Value is required")
    @Positive(message = "Value must be greater than 0")
    private Double value;

    // Constructeur par défaut
    public Depense() {
    }

    // Constructeur paramétré
    public Depense(Lead lead, Ticket ticket, Date date, Double value) {
        this.lead = lead;
        this.ticket = ticket;
        this.date = date;
        this.value = value;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}