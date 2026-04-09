package com.architecturedays.day009.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Reporte {

    private Long id;
    private String nombre;
    private BigDecimal total;
    private LocalDate fecha;

    public Reporte(Long id, String nombre, BigDecimal total, LocalDate fecha) {
        this.id = id;
        this.nombre = nombre;
        this.total = total;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getTotal() { return total; }
    public LocalDate getFecha() { return fecha; }
}
