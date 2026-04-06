package dev.alafourca.cache.config;

import dev.alafourca.cache.model.Category;
import dev.alafourca.cache.model.Product;
import dev.alafourca.cache.repository.CategoryRepository;
import dev.alafourca.cache.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public DataLoader(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        var electronics = categoryRepository.save(new Category("Electrónica", "Dispositivos y gadgets"));
        var peripherals = categoryRepository.save(new Category("Periféricos", "Teclados, mouse, monitores"));
        var storage = categoryRepository.save(new Category("Almacenamiento", "Discos y memorias"));

        productRepository.save(new Product("MacBook Pro 16\"", new BigDecimal("2499.99"), 15, electronics));
        productRepository.save(new Product("iPhone 16 Pro", new BigDecimal("1199.99"), 50, electronics));
        productRepository.save(new Product("Logitech MX Master 3S", new BigDecimal("99.99"), 200, peripherals));
        productRepository.save(new Product("Teclado AULA F99", new BigDecimal("79.99"), 150, peripherals));
        productRepository.save(new Product("Samsung SSD 1TB", new BigDecimal("89.99"), 300, storage));

        System.out.println("✅ Datos inicializados: 3 categorías, 5 productos");
    }
}
