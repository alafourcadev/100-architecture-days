package com.architecturedays.day003;

import com.architecturedays.day003.model.Order;
import com.architecturedays.day003.model.OrderItem;
import com.architecturedays.day003.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@SpringBootApplication
public class Day003Application {

    private static final Logger log = LoggerFactory.getLogger(Day003Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Day003Application.class, args);
    }

    @Bean
    CommandLineRunner initData(OrderRepository repository, EntityManager entityManager) {
        return args -> {
            if (repository.count() > 0) {
                log.info("Datos ya existen, saltando inicializacion");
                return;
            }

            log.info("Inicializando 10,000 ordenes de prueba...");
            Random random = new Random(42);
            String[] statuses = {"pending", "PENDING", "Pending", "confirmed", "shipped", "delivered", "cancelled"};

            for (int i = 0; i < 10_000; i++) {
                String status = statuses[random.nextInt(statuses.length)];
                LocalDateTime createdAt = LocalDateTime.of(2024, random.nextInt(12) + 1, random.nextInt(28) + 1, random.nextInt(24), random.nextInt(60));
                BigDecimal total = BigDecimal.valueOf(random.nextDouble() * 1000);

                Order order = new Order(
                    "user" + i + "@email.com",
                    status,
                    createdAt,
                    total
                );

                // Agregar 1-5 items por orden
                int itemCount = random.nextInt(5) + 1;
                for (int j = 0; j < itemCount; j++) {
                    OrderItem item = new OrderItem(
                        "Product " + (random.nextInt(100) + 1),
                        random.nextInt(5) + 1,
                        BigDecimal.valueOf(random.nextDouble() * 100)
                    );
                    order.addItem(item);
                }

                repository.save(order);

                if (i % 1000 == 0) {
                    log.info("Creadas {} ordenes...", i);
                }
            }

            log.info("10,000 ordenes creadas con diferentes status (mezclando mayusculas/minusculas)");
            log.info("Status disponibles: pending, PENDING, Pending, confirmed, shipped, delivered, cancelled");

            // Crear indice compuesto para la query optimizada
            try {
                entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXISTS idx_orders_status_created ON orders(status, created_at)"
                ).executeUpdate();
                log.info("Indice compuesto creado: idx_orders_status_created");
            } catch (Exception e) {
                log.warn("No se pudo crear el indice (puede que ya exista): {}", e.getMessage());
            }
        };
    }
}
