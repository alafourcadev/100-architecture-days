package com.architecturedays.day010.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * BEFORE: 10 indices. Todas las columnas indexadas "por si acaso".
 * Cada INSERT tiene que actualizar 10 estructuras B-tree adicionales.
 */
@Entity
@Table(name = "pedido_navideno", indexes = {
        @Index(name = "idx_nav_cliente",    columnList = "clienteId"),
        @Index(name = "idx_nav_fecha",      columnList = "fecha"),
        @Index(name = "idx_nav_estado",     columnList = "estado"),
        @Index(name = "idx_nav_total",      columnList = "total"),
        @Index(name = "idx_nav_moneda",     columnList = "moneda"),
        @Index(name = "idx_nav_sucursal",   columnList = "sucursal"),
        @Index(name = "idx_nav_vendedor",   columnList = "vendedorId"),
        @Index(name = "idx_nav_canal",      columnList = "canal"),
        @Index(name = "idx_nav_prioridad",  columnList = "prioridad"),
        @Index(name = "idx_nav_tipo",       columnList = "tipo")
})
public class PedidoNavideno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;
    private LocalDate fecha;
    private String estado;
    private BigDecimal total;
    private String moneda;
    private String sucursal;
    private Long vendedorId;
    private String canal;
    private String prioridad;
    private String tipo;

    public PedidoNavideno() {}

    public PedidoNavideno(Long clienteId, LocalDate fecha, String estado, BigDecimal total,
                          String moneda, String sucursal, Long vendedorId,
                          String canal, String prioridad, String tipo) {
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
        this.moneda = moneda;
        this.sucursal = sucursal;
        this.vendedorId = vendedorId;
        this.canal = canal;
        this.prioridad = prioridad;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public LocalDate getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public BigDecimal getTotal() { return total; }
}
