package com.architecturedays.day012.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * AFTER: Llamadas independientes en paralelo.
 *
 * Producto se busca primero (el vendedorId depende de esto).
 * Despues, vendedor + precio + reviews corren en paralelo.
 *
 * Producto (85ms) + max(Usuario 180ms, Precio 150ms, Reviews 120ms)
 * Total: ~265ms. Casi la mitad.
 */
@Service
@Profile("after")
public class ParallelProductoService implements ProductoService {

    private final ExternalServices external;

    public ParallelProductoService(ExternalServices external) {
        this.external = external;
    }

    @Override
    public Map<String, Object> obtenerDetalle(Long productoId) {
        long inicio = System.currentTimeMillis();

        // Paso 1: buscar producto (necesario antes de lo demas)
        Map<String, Object> producto = external.buscarProducto(productoId);
        Long vendedorId = (Long) producto.get("vendedorId");

        // Pasos 2, 3, 4: en paralelo — son independientes entre si
        CompletableFuture<Map<String, Object>> vendedorFuture =
                CompletableFuture.supplyAsync(() -> external.obtenerUsuario(vendedorId));

        CompletableFuture<BigDecimal> precioFuture =
                CompletableFuture.supplyAsync(() -> external.obtenerPrecioActual(productoId));

        CompletableFuture<Map<String, Object>> reviewsFuture =
                CompletableFuture.supplyAsync(() -> external.obtenerReviews(productoId));

        // Esperar a que los 3 terminen — toma lo que tarde el mas lento (180ms)
        Map<String, Object> vendedor = vendedorFuture.join();
        BigDecimal precio = precioFuture.join();
        Map<String, Object> reviews = reviewsFuture.join();

        long duracion = System.currentTimeMillis() - inicio;
        System.out.println("AFTER: Paralelo — " + duracion + "ms total");

        return Map.of(
                "producto", producto.get("nombre"),
                "vendedor", vendedor.get("nombre"),
                "precio", precio,
                "reviews", reviews,
                "latenciaMs", duracion,
                "modo", "PARALELO — solo el mas lento cuenta: " + duracion + "ms"
        );
    }
}
