package com.architecturedays.day012.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TiendaService {

    private final AtomicInteger requestsRecibidas = new AtomicInteger(0);
    private final AtomicInteger comprasExitosas = new AtomicInteger(0);
    private final AtomicInteger requestsRechazadas = new AtomicInteger(0);

    public Map<String, Object> listarProductos() {
        requestsRecibidas.incrementAndGet();
        simulateWork(50);
        return Map.of(
                "productos", java.util.List.of(
                        Map.of("id", 1, "nombre", "Zapatillas Edicion Limitada", "precio", 299.99, "stock", 100),
                        Map.of("id", 2, "nombre", "Camiseta Drop Exclusivo", "precio", 89.99, "stock", 200),
                        Map.of("id", 3, "nombre", "Gorra Coleccionista", "precio", 49.99, "stock", 500)
                )
        );
    }

    public Map<String, Object> comprar(Long productoId, String cliente) {
        requestsRecibidas.incrementAndGet();
        simulateWork(200);
        comprasExitosas.incrementAndGet();
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        return Map.of(
                "status", "OK",
                "orderId", orderId,
                "productoId", productoId,
                "cliente", cliente,
                "mensaje", "Compra procesada"
        );
    }

    public Map<String, Object> estadisticas() {
        return Map.of(
                "requestsRecibidas", requestsRecibidas.get(),
                "comprasExitosas", comprasExitosas.get(),
                "requestsRechazadas", requestsRechazadas.get()
        );
    }

    public void registrarRechazo() {
        requestsRechazadas.incrementAndGet();
        requestsRecibidas.incrementAndGet();
    }

    private void simulateWork(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
