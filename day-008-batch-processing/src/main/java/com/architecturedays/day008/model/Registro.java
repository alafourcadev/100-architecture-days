package com.architecturedays.day008.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codigo;
    private String descripcion;
    private BigDecimal monto;
    private boolean procesado;

    public Registro() {}

    public Registro(String codigo, String descripcion, BigDecimal monto) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.monto = monto;
        this.procesado = false;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getMonto() { return monto; }
    public boolean isProcesado() { return procesado; }
    public void setProcesado(boolean procesado) { this.procesado = procesado; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
}
