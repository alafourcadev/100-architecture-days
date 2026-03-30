package com.architecturedays.day003.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para ejecutar EXPLAIN ANALYZE directamente en PostgreSQL
 */
@Repository
public class QueryAnalysisRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Ejecuta EXPLAIN ANALYZE en la query LENTA (con LOWER)
     */
    public List<String> explainSlowQuery(String status, String startDate, String endDate) {
        String sql = """
            EXPLAIN ANALYZE
            SELECT * FROM orders
            WHERE LOWER(status) = LOWER('%s')
            AND created_at BETWEEN '%s' AND '%s'
            """.formatted(status, startDate, endDate);

        return executeExplain(sql);
    }

    /**
     * Ejecuta EXPLAIN ANALYZE en la query OPTIMIZADA
     */
    public List<String> explainOptimizedQuery(String status, String startDate, String endDate) {
        String sql = """
            EXPLAIN ANALYZE
            SELECT * FROM orders
            WHERE status = '%s'
            AND created_at BETWEEN '%s' AND '%s'
            """.formatted(status.toLowerCase(), startDate, endDate);

        return executeExplain(sql);
    }

    @SuppressWarnings("unchecked")
    private List<String> executeExplain(String sql) {
        return entityManager.createNativeQuery(sql)
            .getResultList()
            .stream()
            .map(Object::toString)
            .toList();
    }

    /**
     * Verifica si existe el indice compuesto
     */
    @SuppressWarnings("unchecked")
    public List<IndexInfo> getIndexes() {
        String sql = """
            SELECT indexname, indexdef
            FROM pg_indexes
            WHERE tablename = 'orders'
            """;

        return entityManager.createNativeQuery(sql)
            .getResultList()
            .stream()
            .map(row -> {
                Object[] cols = (Object[]) row;
                return new IndexInfo((String) cols[0], (String) cols[1]);
            })
            .toList();
    }

    public record IndexInfo(String name, String definition) {}
}
