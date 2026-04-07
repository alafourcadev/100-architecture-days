package com.architecturedays.day007.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long clienteId;
    private String productoIds;
    private BigDecimal total;
    private String estado;

    public Pedido() {}

    public Pedido(Long clienteId, String productoIds, BigDecimal total, String estado) {
        this.clienteId = clienteId;
        this.productoIds = productoIds;
        this.total = total;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public String getProductoIds() { return productoIds; }
    public BigDecimal getTotal() { return total; }
    public String getEstado() { return estado; }
}
