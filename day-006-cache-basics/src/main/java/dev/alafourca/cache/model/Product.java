package dev.alafourca.cache.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private int stock;
    private LocalDateTime lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Product() {}

    public Product(String name, BigDecimal price, int stock, Category category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; this.lastUpdated = LocalDateTime.now(); }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; this.lastUpdated = LocalDateTime.now(); }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public Category getCategory() { return category; }
}
