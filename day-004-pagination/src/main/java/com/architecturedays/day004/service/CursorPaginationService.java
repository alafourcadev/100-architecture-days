package com.architecturedays.day004.service;

import com.architecturedays.day004.dto.CursorPage;
import com.architecturedays.day004.model.Product;
import com.architecturedays.day004.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICIO "AFTER" - Cursor Pagination (Keyset Pagination)
 * Eficiente para grandes datasets y scroll infinito
 */
@Service
@Profile("after")
public class CursorPaginationService implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(CursorPaginationService.class);
    private final ProductRepository repository;
    private long lastQueryTime = 0;
    private Long lastCursor = null;

    public CursorPaginationService(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Object getProducts(int page, int size, Long cursor) {
        log.info("[CURSOR] Cursor: {}, Size: {}", cursor, size);

        long startTime = System.currentTimeMillis();

        List<Product> products;
        String previousCursor = null;

        if (cursor == null || cursor == 0) {
            // Primera pagina
            products = repository.findFirstPage(PageRequest.of(0, size + 1));
        } else {
            // Pagina siguiente usando cursor
            products = repository.findByCursorAfter(cursor, PageRequest.of(0, size + 1));
            previousCursor = String.valueOf(cursor);
        }

        // Verificar si hay mas resultados
        boolean hasNext = products.size() > size;
        if (hasNext) {
            products = products.subList(0, size); // Remover el elemento extra
        }

        // El siguiente cursor es el ID del ultimo elemento
        String nextCursor = null;
        if (!products.isEmpty() && hasNext) {
            nextCursor = String.valueOf(products.getLast().getId());
        }

        lastQueryTime = System.currentTimeMillis() - startTime;
        lastCursor = cursor;

        log.info("[CURSOR] {} productos en {} ms, hasNext: {}",
            products.size(), lastQueryTime, hasNext);

        return CursorPage.of(products, nextCursor, previousCursor, hasNext);
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("profile", "after (cursor)");
        stats.put("strategy", "Cursor Pagination (Keyset)");
        stats.put("pros", "Constante O(1) sin importar posicion, ideal para scroll infinito");
        stats.put("cons", "No tiene totalPages, no puede saltar a pagina N");
        stats.put("lastQueryTimeMs", lastQueryTime);
        stats.put("lastCursor", lastCursor);
        return stats;
    }
}
