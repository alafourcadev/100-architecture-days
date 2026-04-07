package com.architecturedays.day007.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private String nivel;

    public Cliente() {}

    public Cliente(String nombre, String email, String nivel) {
        this.nombre = nombre;
        this.email = email;
        this.nivel = nivel;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getNivel() { return nivel; }
}
