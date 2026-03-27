package com.architecturedays.day003.service;

import com.architecturedays.day003.model.Order;
import com.architecturedays.day003.repository.OrderRepository;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICIO "DESPUES" - Usa query optimizada con indice compuesto
 */
@Service
@Profile("after")
public class OptimizedQueryService implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OptimizedQueryService.class);
    private final OrderRepository repository;
    private final Statistics hibernateStats;

    public OptimizedQueryService(OrderRepository repository, EntityManagerFactory emf) {
        this.repository = repository;
        this.hibernateStats = emf.unwrap(SessionFactory.class).getStatistics();
        this.hibernateStats.setStatisticsEnabled(true);
    }

    @Override
    public List<Order> findOrdersByStatusAndDateRange(String status, LocalDateTime start, LocalDateTime end) {
        log.info("[AFTER] Ejecutando query optimizada - USA indice compuesto");

        hibernateStats.clear();
        long startTime = System.currentTimeMillis();

        // Normalizamos el status a lowercase ANTES de la query
        // Asi la query puede usar el indice directamente
        String normalizedStatus = status.toLowerCase();
        List<Order> orders = repository.findByStatusAndDateRangeOptimized(normalizedStatus, start, end);

        long duration = System.currentTimeMillis() - startTime;
        log.info("[AFTER] Query ejecutada en {} ms, {} resultados", duration, orders.size());
        log.info("[AFTER] Queries ejecutadas: {}", hibernateStats.getQueryExecutionCount());

        return orders;
    }

    @Override
    public Map<String, Object> getQueryStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("profile", "after");
        stats.put("solucion", "Status normalizado + indice compuesto (status, created_at)");
        stats.put("queryExecutionCount", hibernateStats.getQueryExecutionCount());
        stats.put("queryExecutionMaxTime", hibernateStats.getQueryExecutionMaxTime());
        stats.put("slowestQuery", hibernateStats.getQueryExecutionMaxTimeQueryString());
        return stats;
    }
}
