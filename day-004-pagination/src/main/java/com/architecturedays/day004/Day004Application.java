package com.architecturedays.day004;

import com.architecturedays.day004.model.Product;
import com.architecturedays.day004.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Day 004: Paginacion - O como deje de cargar 10,000 registros
 *
 * Este dia demuestra el impacto de diferentes estrategias de paginacion:
 * - before: Sin paginacion (carga TODO en memoria)
 * - offset: Paginacion tradicional con OFFSET
 * - after: Cursor pagination (keyset)
 *
 * Ejecutar con diferentes profiles:
 * - mvn spring-boot:run -Dspring-boot.run.profiles=before
 * - mvn spring-boot:run -Dspring-boot.run.profiles=offset
 * - mvn spring-boot:run -Dspring-boot.run.profiles=after
 */
@SpringBootApplication
public class Day004Application {

    private static final Logger log = LoggerFactory.getLogger(Day004Application.class);
    private static final int TOTAL_PRODUCTS = 50_000;

    public static void main(String[] args) {
        SpringApplication.run(Day004Application.class, args);
    }

    @Bean
    CommandLineRunner initData(ProductRepository repository, Environment env) {
        return args -> {
            String[] activeProfiles = env.getActiveProfiles();
            String profile = activeProfiles.length > 0 ? activeProfiles[0] : "default";

            log.info("=".repeat(60));
            log.info("Day 004: Paginacion");
            log.info("Profile activo: {}", profile);
            log.info("=".repeat(60));

            long count = repository.count();
            if (count >= TOTAL_PRODUCTS) {
                log.info("Base de datos ya tiene {} productos", count);
            } else {
                log.info("Creando {} productos para demostrar paginacion...", TOTAL_PRODUCTS);
                createProducts(repository);
            }

            log.info("");
            log.info("Endpoints disponibles:");
            log.info("  GET /api/products?page=0&size=20     - Obtener productos");
            log.info("  GET /api/products?cursor=100&size=20 - Cursor pagination");
            log.info("  GET /api/products/stats              - Estadisticas");
            log.info("  GET /api/products/compare            - Comparar estrategias");
            log.info("");

            switch (profile) {
                case "before" -> {
                    log.warn("ATENCION: Profile 'before' cargara TODOS los productos!");
                    log.warn("Esto puede causar:");
                    log.warn("  - Alto consumo de memoria");
                    log.warn("  - Respuesta lenta");
                    log.warn("  - Posible OutOfMemoryError");
                }
                case "offset" -> {
                    log.info("Profile 'offset' usa paginacion tradicional");
                    log.info("Prueba con paginas altas para ver degradacion:");
                    log.info("  curl 'localhost:8080/api/products?page=0&size=20'");
                    log.info("  curl 'localhost:8080/api/products?page=1000&size=20'");
                    log.info("  curl 'localhost:8080/api/products?page=2000&size=20'");
                }
                case "after" -> {
                    log.info("Profile 'after' usa cursor pagination");
                    log.info("El rendimiento es constante sin importar la posicion:");
                    log.info("  curl 'localhost:8080/api/products?size=20'");
                    log.info("  curl 'localhost:8080/api/products?cursor=100&size=20'");
                    log.info("  curl 'localhost:8080/api/products?cursor=40000&size=20'");
                }
                default -> log.warn("Profile no reconocido: {}", profile);
            }

            log.info("=".repeat(60));
        };
    }

    private void createProducts(ProductRepository repository) {
        Random random = new Random(42);
        String[] categories = {"Electronics", "Clothing", "Home", "Sports", "Books", "Toys"};
        String[] adjectives = {"Premium", "Basic", "Pro", "Ultra", "Mini", "Max"};
        String[] nouns = {"Widget", "Gadget", "Device", "Tool", "Item", "Product"};

        List<Product> batch = new ArrayList<>(1000);
        int batchCount = 0;

        for (int i = 1; i <= TOTAL_PRODUCTS; i++) {
            String name = adjectives[random.nextInt(adjectives.length)] + " " +
                         nouns[random.nextInt(nouns.length)] + " " + i;
            String category = categories[random.nextInt(categories.length)];
            BigDecimal price = BigDecimal.valueOf(10 + random.nextDouble() * 990)
                                         .setScale(2, java.math.RoundingMode.HALF_UP);
            int stock = random.nextInt(1000);
            String description = "Description for " + name + ". " +
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(3);

            Product product = new Product(name, description, price, category, stock);
            batch.add(product);

            if (batch.size() >= 1000) {
                repository.saveAll(batch);
                batch.clear();
                batchCount++;
                if (batchCount % 10 == 0) {
                    log.info("  Progreso: {}/{} productos", batchCount * 1000, TOTAL_PRODUCTS);
                }
            }
        }

        if (!batch.isEmpty()) {
            repository.saveAll(batch);
        }

        log.info("Creados {} productos exitosamente", TOTAL_PRODUCTS);
    }
}
