package site.easy.to.build.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "type_config")
public class TypeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "type", nullable = false)
    private String type;

    // Constructeurs
    public TypeConfig() {
    }

    public TypeConfig(String type) {
        this.type = type;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}