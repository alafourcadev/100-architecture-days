package com.architecturedays.day003.controller;

import com.architecturedays.day003.model.Order;
import com.architecturedays.day003.repository.QueryAnalysisRepository;
import com.architecturedays.day003.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final QueryAnalysisRepository queryAnalysisRepository;

    public OrderController(OrderService orderService, QueryAnalysisRepository queryAnalysisRepository) {
        this.orderService = orderService;
        this.queryAnalysisRepository = queryAnalysisRepository;
    }

    /**
     * Busca ordenes por estado y rango de fechas
     * GET /api/orders?status=pending&start=2024-01-01T00:00:00&end=2024-12-31T23:59:59
     */
    @GetMapping
    public Map<String, Object> findOrders(
            @RequestParam String status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        long startTime = System.currentTimeMillis();
        List<Order> orders = orderService.findOrdersByStatusAndDateRange(status, start, end);
        long duration = System.currentTimeMillis() - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("count", orders.size());
        response.put("durationMs", duration);
        response.put("orders", orders.stream()
            .map(o -> Map.of(
                "id", o.getId(),
                "customerEmail", o.getCustomerEmail(),
                "status", o.getStatus(),
                "total", o.getTotal()
            ))
            .toList());

        return response;
    }

    /**
     * Muestra estadisticas de Hibernate
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return orderService.getQueryStats();
    }

    /**
     * Ejecuta EXPLAIN ANALYZE para la query LENTA
     */
    @GetMapping("/explain/slow")
    public Map<String, Object> explainSlowQuery(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "2024-01-01 00:00:00") String start,
            @RequestParam(defaultValue = "2024-12-31 23:59:59") String end) {

        List<String> plan = queryAnalysisRepository.explainSlowQuery(status, start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("queryType", "SLOW - con LOWER()");
        response.put("executionPlan", plan);
        return response;
    }

    /**
     * Ejecuta EXPLAIN ANALYZE para la query OPTIMIZADA
     */
    @GetMapping("/explain/optimized")
    public Map<String, Object> explainOptimizedQuery(
            @RequestParam(defaultValue = "pending") String status,
            @RequestParam(defaultValue = "2024-01-01 00:00:00") String start,
            @RequestParam(defaultValue = "2024-12-31 23:59:59") String end) {

        List<String> plan = queryAnalysisRepository.explainOptimizedQuery(status, start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("queryType", "OPTIMIZED - sin funciones");
        response.put("executionPlan", plan);
        return response;
    }

    /**
     * Lista los indices de la tabla orders
     */
    @GetMapping("/indexes")
    public Map<String, Object> getIndexes() {
        Map<String, Object> response = new HashMap<>();
        response.put("table", "orders");
        response.put("indexes", queryAnalysisRepository.getIndexes());
        return response;
    }
}
