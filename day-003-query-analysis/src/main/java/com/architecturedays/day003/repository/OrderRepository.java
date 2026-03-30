package com.architecturedays.day003.repository;

import com.architecturedays.day003.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * QUERY LENTA - Usa LOWER() que invalida el indice
     * PostgreSQL no puede usar el indice en status porque aplicamos una funcion
     */
    @Query("SELECT o FROM Order o WHERE LOWER(o.status) = LOWER(:status) AND o.createdAt BETWEEN :start AND :end")
    List<Order> findByStatusIgnoreCaseAndDateRange(
        @Param("status") String status,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * QUERY OPTIMIZADA - Asume que status ya esta normalizado en lowercase
     * Usa indice compuesto en (status, created_at)
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :start AND :end")
    List<Order> findByStatusAndDateRangeOptimized(
        @Param("status") String status,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * Cuenta ordenes por estado - para verificar distribucion
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();
}
