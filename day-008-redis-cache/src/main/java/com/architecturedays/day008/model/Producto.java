package com.architecturedays.day008.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private int stock;
    private String categoria;

    public Producto() {}

    public Producto(String nombre, BigDecimal precio, int stock, String categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getCategoria() { return categoria; }

    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
