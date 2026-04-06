package dev.alafourca.cache.model;

import jakarta.persistence.*;

/**
 * Categoría — dato que cambia MUY POCO.
 * Ideal para cachear: se lee mucho, se modifica casi nunca.
 */
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
