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
 * SERVICIO "ANTES" - Usa query con LOWER() que invalida indices
 */
@Service
@Profile("before")
public class SlowQueryService implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(SlowQueryService.class);
    private final OrderRepository repository;
    private final Statistics hibernateStats;

    public SlowQueryService(OrderRepository repository, EntityManagerFactory emf) {
        this.repository = repository;
        this.hibernateStats = emf.unwrap(SessionFactory.class).getStatistics();
        this.hibernateStats.setStatisticsEnabled(true);
    }

    @Override
    public List<Order> findOrdersByStatusAndDateRange(String status, LocalDateTime start, LocalDateTime end) {
        log.info("[BEFORE] Ejecutando query con LOWER() - NO usa indice");

        hibernateStats.clear();
        long startTime = System.currentTimeMillis();

        // Query LENTA: LOWER() invalida el indice en status
        List<Order> orders = repository.findByStatusIgnoreCaseAndDateRange(status, start, end);

        long duration = System.currentTimeMillis() - startTime;
        log.info("[BEFORE] Query ejecutada en {} ms, {} resultados", duration, orders.size());
        log.info("[BEFORE] Queries ejecutadas: {}", hibernateStats.getQueryExecutionCount());

        return orders;
    }

    @Override
    public Map<String, Object> getQueryStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("profile", "before");
        stats.put("problema", "Query usa LOWER() que invalida el indice");
        stats.put("queryExecutionCount", hibernateStats.getQueryExecutionCount());
        stats.put("queryExecutionMaxTime", hibernateStats.getQueryExecutionMaxTime());
        stats.put("slowestQuery", hibernateStats.getQueryExecutionMaxTimeQueryString());
        return stats;
    }
}
