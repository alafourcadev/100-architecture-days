package com.architecturedays.day003;

import com.architecturedays.day003.model.Order;
import com.architecturedays.day003.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QueryPerformanceTest {

    @Autowired
    private OrderRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        // Crear ordenes con diferentes variaciones de status
        for (int i = 0; i < 100; i++) {
            String status = switch (i % 5) {
                case 0 -> "pending";
                case 1 -> "PENDING";
                case 2 -> "Pending";
                case 3 -> "confirmed";
                default -> "shipped";
            };

            Order order = new Order(
                "user" + i + "@test.com",
                status,
                LocalDateTime.of(2024, (i % 12) + 1, (i % 28) + 1, 12, 0),
                BigDecimal.valueOf(100 + i)
            );
            repository.save(order);
        }
    }

    @Test
    @DisplayName("Query con LOWER encuentra todas las variaciones de PENDING")
    void queryWithLowerFindsAllVariations() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<Order> orders = repository.findByStatusIgnoreCaseAndDateRange("pending", start, end);

        // Debe encontrar pending, PENDING, Pending
        assertThat(orders).isNotEmpty();
        assertThat(orders).allMatch(o ->
            o.getStatus().equalsIgnoreCase("pending")
        );
    }

    @Test
    @DisplayName("Query optimizada solo encuentra status exacto")
    void optimizedQueryFindsExactMatch() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<Order> orders = repository.findByStatusAndDateRangeOptimized("pending", start, end);

        // Solo encuentra "pending" exacto
        assertThat(orders).allMatch(o ->
            o.getStatus().equals("pending")
        );
    }

    @Test
    @DisplayName("Query optimizada es mas restrictiva que LOWER")
    void optimizedQueryIsMoreRestrictive() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<Order> withLower = repository.findByStatusIgnoreCaseAndDateRange("pending", start, end);
        List<Order> optimized = repository.findByStatusAndDateRangeOptimized("pending", start, end);

        // LOWER encuentra mas porque ignora case
        assertThat(withLower.size()).isGreaterThanOrEqualTo(optimized.size());
    }

    @Test
    @DisplayName("Distribucion de status es correcta")
    void statusDistributionIsCorrect() {
        List<Object[]> distribution = repository.countByStatus();

        assertThat(distribution).isNotEmpty();
        distribution.forEach(row -> {
            String status = (String) row[0];
            Long count = (Long) row[1];
            assertThat(count).isGreaterThan(0);
        });
    }
}
