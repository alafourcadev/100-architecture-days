package com.architecturedays.day008.service;

import com.architecturedays.day008.model.Producto;
import com.architecturedays.day008.repository.ProductoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * AFTER: Cache-Aside pattern con RedisTemplate.
 * TTL explicito, invalidacion en cada escritura, datos siempre frescos.
 */
@Service
@Profile("after")
public class SmartCacheService implements ProductoService {

    private final ProductoRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration TTL_PRODUCTO = Duration.ofMinutes(5);
    private static final String KEY_PREFIX = "producto:";

    public SmartCacheService(ProductoRepository repository,
                             RedisTemplate<String, Object> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Map<String, Object> obtener(Long id) {
        String key = KEY_PREFIX + id;

        // 1. Consultar Redis primero
        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            System.out.println("AFTER: Cache HIT para producto " + id);
            cached = new java.util.HashMap<>(cached);
            cached.put("source", "redis (TTL: " + TTL_PRODUCTO.toMinutes() + " min)");
            return cached;
        }

        // 2. Cache miss — ir a la BD
        System.out.println("AFTER: Cache MISS para producto " + id + " — consultando BD");
        Producto p = repository.findById(id).orElseThrow();
        Map<String, Object> data = Map.of(
                "id", p.getId(),
                "nombre", p.getNombre(),
                "precio", p.getPrecio(),
                "stock", p.getStock(),
                "source", "database (guardado en Redis con TTL " + TTL_PRODUCTO.toMinutes() + " min)"
        );

        // 3. Guardar en Redis con TTL
        redisTemplate.opsForValue().set(key, data, TTL_PRODUCTO);
        return data;
    }

    @Override
    public Map<String, Object> actualizar(Long id, BigDecimal nuevoPrecio) {
        Producto p = repository.findById(id).orElseThrow();
        BigDecimal precioAnterior = p.getPrecio();
        p.setPrecio(nuevoPrecio);
        repository.save(p);

        // INVALIDAR el cache — no actualizar, INVALIDAR
        String key = KEY_PREFIX + id;
        redisTemplate.delete(key);
        System.out.println("AFTER: Precio actualizado Y cache invalidado. Cero datos fantasma.");

        return Map.of(
                "id", id,
                "precioAnterior", precioAnterior,
                "precioNuevo", nuevoPrecio,
                "cacheInvalidado", true,
                "message", "Proximo GET traera el dato fresco de la BD"
        );
    }

    @Override
    public Map<String, Object> listar() {
        System.out.println("AFTER: Listado siempre desde BD (no se cachean listas mutables)");
        List<Map<String, Object>> productos = repository.findAll().stream()
                .map(p -> Map.<String, Object>of(
                        "id", p.getId(),
                        "nombre", p.getNombre(),
                        "precio", p.getPrecio(),
                        "stock", p.getStock()
                )).toList();
        return Map.of("productos", productos, "source", "database (listas no se cachean)");
    }
}
