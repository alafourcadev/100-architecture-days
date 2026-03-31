package com.architecturedays.day004.service;

import com.architecturedays.day004.dto.ProductSummary;
import com.architecturedays.day004.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * SERVICIO "OFFSET" - Paginacion tradicional con OFFSET
 * Bueno para datasets pequenos, problematico para grandes
 */
@Service
@Profile("offset")
public class OffsetPaginationService implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(OffsetPaginationService.class);
    private final ProductRepository repository;
    private long lastQueryTime = 0;
    private int lastPage = 0;

    public OffsetPaginationService(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Object getProducts(int page, int size, Long cursor) {
        log.info("[OFFSET] Pagina {} con {} elementos", page, size);

        long startTime = System.currentTimeMillis();

        // Offset pagination: LIMIT size OFFSET page*size
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<ProductSummary> productPage = repository.findAllProjectedBy(pageRequest);

        lastQueryTime = System.currentTimeMillis() - startTime;
        lastPage = page;

        log.info("[OFFSET] Pagina {}/{} en {} ms, {} elementos",
            page, productPage.getTotalPages(), lastQueryTime, productPage.getNumberOfElements());

        // Problema: En paginas altas, PostgreSQL tiene que "saltar" muchos registros
        if (page > 100) {
            log.warn("[OFFSET] Pagina {} - OFFSET alto puede ser lento!", page);
        }

        return productPage;
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("profile", "offset");
        stats.put("strategy", "Offset Pagination con Spring Data Pageable");
        stats.put("pros", "Simple, metadata completa (totalPages, totalElements)");
        stats.put("cons", "Lento en paginas altas (OFFSET grande)");
        stats.put("lastQueryTimeMs", lastQueryTime);
        stats.put("lastPage", lastPage);
        return stats;
    }
}
