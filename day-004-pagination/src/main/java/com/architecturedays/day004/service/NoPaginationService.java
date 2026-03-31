package com.architecturedays.day004.service;

import com.architecturedays.day004.model.Product;
import com.architecturedays.day004.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICIO "ANTES" - Sin paginacion, retorna TODO
 * Problema: payload enorme, memoria explotada, red lenta
 */
@Service
@Profile("before")
public class NoPaginationService implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(NoPaginationService.class);
    private final ProductRepository repository;
    private long lastQueryTime = 0;
    private int lastResultCount = 0;

    public NoPaginationService(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Object getProducts(int page, int size, Long cursor) {
        log.warn("[BEFORE] Cargando TODOS los productos - sin paginacion!");

        long startTime = System.currentTimeMillis();

        // PROBLEMA: Carga TODO en memoria
        List<Product> allProducts = repository.findAll();

        lastQueryTime = System.currentTimeMillis() - startTime;
        lastResultCount = allProducts.size();

        log.warn("[BEFORE] {} productos cargados en {} ms", lastResultCount, lastQueryTime);
        log.warn("[BEFORE] Estimado de memoria: {} MB", (lastResultCount * 200) / 1_000_000);

        return allProducts;
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("profile", "before");
        stats.put("strategy", "Sin paginacion - findAll()");
        stats.put("problema", "Carga TODO en memoria");
        stats.put("lastQueryTimeMs", lastQueryTime);
        stats.put("lastResultCount", lastResultCount);
        stats.put("estimatedMemoryMB", (lastResultCount * 200) / 1_000_000);
        return stats;
    }
}
