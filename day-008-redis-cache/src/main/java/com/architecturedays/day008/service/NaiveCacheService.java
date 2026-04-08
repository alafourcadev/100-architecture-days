package com.architecturedays.day008.service;

import com.architecturedays.day008.model.Producto;
import com.architecturedays.day008.repository.ProductoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * BEFORE: Cacheable sin TTL, sin invalidacion, sin estrategia.
 * Actualizas un producto y Redis sigue sirviendo el viejo.
 * Datos fantasma por todos lados.
 */
@Service
@Profile("before")
public class NaiveCacheService implements ProductoService {

    private final ProductoRepository repository;

    public NaiveCacheService(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable("productos")
    public Map<String, Object> obtener(Long id) {
        System.out.println("BEFORE: Consultando BD para producto " + id + " (esto se cachea SIN TTL)");
        Producto p = repository.findById(id).orElseThrow();
        return Map.of(
                "id", p.getId(),
                "nombre", p.getNombre(),
                "precio", p.getPrecio(),
                "stock", p.getStock(),
                "source", "database (se cacheo en Redis sin TTL)"
        );
    }

    @Override
    public Map<String, Object> actualizar(Long id, BigDecimal nuevoPrecio) {
        Producto p = repository.findById(id).orElseThrow();
        BigDecimal precioAnterior = p.getPrecio();
        p.setPrecio(nuevoPrecio);
        repository.save(p);

        // NO invalida el cache. Redis sigue con el precio viejo.
        System.out.println("BEFORE: Precio actualizado en BD de " + precioAnterior + " a " + nuevoPrecio);
        System.out.println("BEFORE: Redis NO se entero del cambio. DATO FANTASMA.");

        return Map.of(
                "id", id,
                "precioAnterior", precioAnterior,
                "precioNuevo", nuevoPrecio,
                "cacheInvalidado", false,
                "warning", "Redis sigue sirviendo el precio viejo"
        );
    }

    @Override
    @Cacheable("todosProductos")
    public Map<String, Object> listar() {
        System.out.println("BEFORE: Consultando BD para TODOS los productos (esto se cachea SIN TTL)");
        List<Map<String, Object>> productos = repository.findAll().stream()
                .map(p -> Map.<String, Object>of(
                        "id", p.getId(),
                        "nombre", p.getNombre(),
                        "precio", p.getPrecio(),
                        "stock", p.getStock()
                )).toList();
        return Map.of("productos", productos, "source", "database");
    }
}
