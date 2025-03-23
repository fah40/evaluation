package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.sql.Date;

@Entity
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false, columnDefinition = "INT UNSIGNED")
    @NotNull(message = "Customer is required")
    private Customer customer;

    @Column(name = "date", nullable = false)
    @NotNull(message = "Date is required")
    private Date date;

    @Column(name = "value", nullable = false)
    @NotNull(message = "Value is required")
    @Positive(message = "Value must be greater than 0")
    private Double value;

    @Column(name = "is_depass", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDepass;

    // Default constructor
    public Budget() {
    }

    // Parameterized constructor
    public Budget(Customer customer, Date date, Double value,boolean isDepass) {
        this.customer = customer;
        this.date = date;
        this.value = value;
        this.isDepass = isDepass;
    }

    // Getters and Setters
    
    public boolean getIsDepass() {
        return isDepass;
    }

    public void setIsDepass(boolean isDepass) {
        this.isDepass = isDepass;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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