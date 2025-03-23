package site.easy.to.build.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Configuration")
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_type_config", nullable = false)
    private TypeConfig typeConfig;

    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;

    // Constructeurs
    public Configuration() {
    }

    public Configuration(TypeConfig typeConfig, String value) {
        this.typeConfig = typeConfig;
        this.value = value;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeConfig getTypeConfig() {
        return typeConfig;
    }

    public void setTypeConfig(TypeConfig typeConfig) {
        this.typeConfig = typeConfig;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}