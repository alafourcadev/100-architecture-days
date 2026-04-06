package dev.alafourca.cache.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Map<String, Object>> getAllProducts();
    BigDecimal getProductPrice(Long id);
    void updatePrice(Long id, BigDecimal newPrice);
    List<Map<String, Object>> getAllCategories();
}
