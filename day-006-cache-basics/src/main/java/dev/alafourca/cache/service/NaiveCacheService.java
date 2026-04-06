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
 * ANTES: Cacheo TODO sin pensar.
 *
 * Problema: el precio y stock cambian cada minuto.
 * Si cacheo eso, el usuario ve un precio viejo,
 * compra, y le cobran otro precio.
 *
 * Bug silencioso. El peor tipo de bug.
 */
@Service
@Profile("before")
public class NaiveCacheService implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public NaiveCacheService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // ❌ MAL: cacheando precio que cambia cada minuto
    @Override
    @Cacheable("products")
    public List<Map<String, Object>> getAllProducts() {
        System.out.println("⚠️ BEFORE: Consultando BD para productos (esto se cachea TODO)");
        return productRepository.findAll().stream().map(p -> Map.<String, Object>of(
                "id", p.getId(),
                "name", p.getName(),
                "price", p.getPrice(),    // ← precio cacheado = precio VIEJO
                "stock", p.getStock()     // ← stock cacheado = stock VIEJO
        )).toList();
    }

    // ❌ MAL: cacheando precio individual también
    @Override
    @Cacheable("productPrice")
    public BigDecimal getProductPrice(Long id) {
        System.out.println("⚠️ BEFORE: Consultando precio del producto " + id);
        return productRepository.findById(id)
                .map(Product::getPrice)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public void updatePrice(Long id, BigDecimal newPrice) {
        // Actualizo el precio en BD pero el caché sigue con el viejo
        productRepository.findById(id).ifPresent(p -> {
            p.setPrice(newPrice);
            productRepository.save(p);
            System.out.println("✅ Precio actualizado en BD a " + newPrice);
            System.out.println("❌ PERO el caché sigue con el precio viejo!");
        });
    }

    @Override
    @Cacheable("categories")
    public List<Map<String, Object>> getAllCategories() {
        System.out.println("📦 Consultando categorías");
        return categoryRepository.findAll().stream().map(c -> Map.<String, Object>of(
                "id", c.getId(),
                "name", c.getName()
        )).toList();
    }
}
