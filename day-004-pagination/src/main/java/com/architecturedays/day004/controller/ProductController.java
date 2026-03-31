package com.architecturedays.day004.controller;

import com.architecturedays.day004.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para demostrar diferentes estrategias de paginacion
 *
 * Profiles disponibles:
 * - before: Sin paginacion (carga TODO)
 * - offset: Paginacion tradicional con OFFSET
 * - after: Cursor pagination (keyset)
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products
     *
     * Parametros:
     * - page: numero de pagina (para offset pagination)
     * - size: cantidad de elementos por pagina
     * - cursor: ID del ultimo elemento visto (para cursor pagination)
     *
     * El comportamiento depende del profile activo:
     * - before: ignora parametros, retorna TODO
     * - offset: usa page y size
     * - after: usa cursor y size
     */
    @GetMapping
    public ResponseEntity<Object> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long cursor) {

        return ResponseEntity.ok(productService.getProducts(page, size, cursor));
    }

    /**
     * GET /api/products/stats
     *
     * Retorna estadisticas de la ultima consulta:
     * - Tiempo de ejecucion
     * - Estrategia utilizada
     * - Pros y contras
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(productService.getStats());
    }

    /**
     * GET /api/products/compare
     *
     * Endpoint informativo sobre las estrategias
     */
    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareStrategies() {
        return ResponseEntity.ok(Map.of(
            "strategies", Map.of(
                "no_pagination", Map.of(
                    "profile", "before",
                    "description", "Carga todos los registros en memoria",
                    "sql", "SELECT * FROM products",
                    "complexity", "O(n) - crece linealmente con los datos",
                    "use_case", "Datasets muy pequenos (<100 registros)",
                    "problems", "Memory overflow, red lenta, timeout"
                ),
                "offset_pagination", Map.of(
                    "profile", "offset",
                    "description", "Paginacion tradicional con LIMIT/OFFSET",
                    "sql", "SELECT * FROM products ORDER BY id LIMIT 20 OFFSET 1000",
                    "complexity", "O(offset) - mas lento en paginas altas",
                    "use_case", "UI con paginas numeradas, datasets medianos",
                    "problems", "Paginas altas son lentas, datos inconsistentes si hay inserciones"
                ),
                "cursor_pagination", Map.of(
                    "profile", "after",
                    "description", "Keyset pagination usando el ultimo ID visto",
                    "sql", "SELECT * FROM products WHERE id > 1000 ORDER BY id LIMIT 20",
                    "complexity", "O(1) - constante sin importar posicion",
                    "use_case", "Scroll infinito, APIs, grandes datasets",
                    "problems", "No puede saltar a pagina N, necesita ordenamiento estable"
                )
            ),
            "recommendation", "Usar cursor pagination para APIs y grandes datasets"
        ));
    }
}
