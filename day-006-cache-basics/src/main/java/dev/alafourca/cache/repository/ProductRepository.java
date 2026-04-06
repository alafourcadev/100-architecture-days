package dev.alafourca.cache.repository;

import dev.alafourca.cache.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
