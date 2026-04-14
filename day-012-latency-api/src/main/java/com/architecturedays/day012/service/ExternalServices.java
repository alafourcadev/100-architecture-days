package com.architecturedays.day012.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Simula servicios externos con latencia realista.
 * En un sistema real, estos serian llamadas HTTP a otros microservicios.
 */
@Component
public class ExternalServices {

    public Map<String, Object> buscarProducto(Long id) {
        simulateLatency(85);
        return Map.of(
                "id", id,
                "nombre", "MacBook Pro M4",
                "categoria", "Electronics",
                "vendedorId", 42L
        );
    }

    public Map<String, Object> obtenerUsuario(Long vendedorId) {
        simulateLatency(180);
        return Map.of(
                "vendedorId", vendedorId,
                "nombre", "TechStore Official",
                "verificado", true
        );
    }

    public BigDecimal obtenerPrecioActual(Long productoId) {
        simulateLatency(150);
        return new BigDecimal("2499.99");
    }

    public Map<String, Object> obtenerReviews(Long productoId) {
        simulateLatency(120);
        return Map.of(
                "productoId", productoId,
                "promedio", 4.7,
                "total", 1284
        );
    }

    private void simulateLatency(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
