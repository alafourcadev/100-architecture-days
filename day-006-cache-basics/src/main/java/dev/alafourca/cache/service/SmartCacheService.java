package dev.alafourca.cache.service;

import dev.alafourca.cache.model.Product;
import dev.alafourca.cache.repository.ProductRepository;
import dev.alafourca.cache.repository.CategoryRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DESPUÉS: Cacheo SOLO lo que tiene sentido.
 *
 * Regla: cacheá datos que se leen MUCHO y cambian POCO.
 * - Categorías → cambian 1 vez al mes → CACHEAR
 * - Precios → cambian cada minuto → NO CACHEAR
 * - Stock → cambia con cada compra → NO CACHEAR
 */
@Service
@Profile("after")
public class SmartCacheService implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public SmartCacheService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // ✅ Sin caché — los precios y stock cambian constantemente
    @Override
    public List<Map<String, Object>> getAllProducts() {
        System.out.println("✅ AFTER: Consultando BD para productos (datos frescos siempre)");
        return productRepository.findAll().stream().map(p -> Map.<String, Object>of(
                "id", p.getId(),
                "name", p.getName(),
                "price", p.getPrice(),    // ← siempre fresco
                "stock", p.getStock()     // ← siempre fresco
        )).toList();
    }

    // ✅ Sin caché — el precio necesita ser real-time
    @Override
    public BigDecimal getProductPrice(Long id) {
        System.out.println("✅ AFTER: Consultando precio fresco del producto " + id);
        return productRepository.findById(id)
                .map(Product::getPrice)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public void updatePrice(Long id, BigDecimal newPrice) {
        productRepository.findById(id).ifPresent(p -> {
            p.setPrice(newPrice);
            productRepository.save(p);
            System.out.println("✅ Precio actualizado a " + newPrice + " — sin caché stale");
        });
    }

    // ✅ CACHEAR — las categorías cambian 1 vez al mes
    @Override
    @Cacheable("categories")
    public List<Map<String, Object>> getAllCategories() {
        System.out.println("📦 Consultando categorías (solo la primera vez)");
        return categoryRepository.findAll().stream().map(c -> Map.<String, Object>of(
                "id", c.getId(),
                "name", c.getName()
        )).toList();
    }
}
