package dev.alafourca.cache.repository;

import dev.alafourca.cache.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
