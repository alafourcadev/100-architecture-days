package com.architecturedays.day012.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * BEFORE: Todo secuencial.
 *
 * Cada llamada espera a que la anterior termine,
 * aunque NO dependen entre si.
 *
 * Producto (85ms) + Usuario (180ms) + Precio (150ms) + Reviews (120ms)
 * Total: ~535ms. Todo sumado. El usuario espera medio segundo.
 */
@Service
@Profile("before")
public class SequentialProductoService implements ProductoService {

    private final ExternalServices external;

    public SequentialProductoService(ExternalServices external) {
        this.external = external;
    }

    @Override
    public Map<String, Object> obtenerDetalle(Long productoId) {
        long inicio = System.currentTimeMillis();

        // Paso 1: buscar producto (85ms)
        Map<String, Object> producto = external.buscarProducto(productoId);

        // Paso 2: buscar vendedor — depende del paso 1, OK que sea secuencial
        Long vendedorId = (Long) producto.get("vendedorId");
        Map<String, Object> vendedor = external.obtenerUsuario(vendedorId);

        // Paso 3: obtener precio — NO depende del paso 2, pero espera igual
        BigDecimal precio = external.obtenerPrecioActual(productoId);

        // Paso 4: obtener reviews — NO depende de nada anterior, pero espera igual
        Map<String, Object> reviews = external.obtenerReviews(productoId);

        long duracion = System.currentTimeMillis() - inicio;
        System.out.println("BEFORE: Secuencial — " + duracion + "ms total");

        return Map.of(
                "producto", producto.get("nombre"),
                "vendedor", vendedor.get("nombre"),
                "precio", precio,
                "reviews", reviews,
                "latenciaMs", duracion,
                "modo", "SECUENCIAL — todo sumado: " + duracion + "ms"
        );
    }
}
