package dev.alafourca.cache.controller;

import dev.alafourca.cache.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CacheController {

    private final ProductService productService;

    public CacheController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<Map<String, Object>> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/{id}/price")
    public Map<String, Object> getPrice(@PathVariable Long id) {
        return Map.of("productId", id, "price", productService.getProductPrice(id));
    }

    // Cambiá el precio y después consultá — vas a ver si el caché te miente
    @PutMapping("/products/{id}/price")
    public Map<String, Object> updatePrice(@PathVariable Long id, @RequestParam BigDecimal price) {
        productService.updatePrice(id, price);
        return Map.of("productId", id, "newPrice", price, "message", "Precio actualizado");
    }

    @GetMapping("/categories")
    public List<Map<String, Object>> getCategories() {
        return productService.getAllCategories();
    }
}
