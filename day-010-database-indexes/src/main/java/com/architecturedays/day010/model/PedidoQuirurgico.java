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
 * AFTER: 2 indices. Solo los que cubren queries reales.
 *
 * idx_cliente_fecha: indice compuesto que cubre el 80% de las queries
 *                    (buscar pedidos de un cliente en un rango de fechas)
 * idx_estado: unico filtro por estado que usa el dashboard
 */
@Entity
@Table(name = "pedido_quirurgico", indexes = {
        @Index(name = "idx_qui_cliente_fecha", columnList = "clienteId, fecha"),
        @Index(name = "idx_qui_estado",        columnList = "estado")
})
public class PedidoQuirurgico {

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

    public PedidoQuirurgico() {}

    public PedidoQuirurgico(Long clienteId, LocalDate fecha, String estado, BigDecimal total,
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
