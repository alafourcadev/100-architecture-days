# Dia 003: La consulta que trajo de rodillas mi BD

> **#100ArchitectureDays** | Spring Boot 4 + Java 25

---

## El Problema

Agregue un indice a la columna `status`. La query sigue tardando 3 segundos.

El DBA me mira. Yo miro el indice. El indice existe. Pero no se usa.

10,000 ordenes. Query simple: buscar por estado y rango de fechas. Deberia ser instantaneo. Pero no.

Spoiler: el indice no era el problema. Era como lo usaba.

---

## La Solucion

```
ANTES:  WHERE LOWER(status) = 'pending'  -> Seq Scan (3000ms)
DESPUES: WHERE status = 'pending'        -> Index Scan (15ms)
```

**Concepto:** Database Query Performance Analysis
**Patron:** Query Optimization con EXPLAIN ANALYZE
**Herramientas:** PostgreSQL EXPLAIN, Hibernate Statistics

---

## Por que LOWER() mata tu indice

```sql
-- PostgreSQL crea un indice en la columna 'status'
CREATE INDEX idx_status ON orders(status);

-- Esta query NO usa el indice (Seq Scan)
SELECT * FROM orders WHERE LOWER(status) = 'pending';

-- Esta query SI usa el indice (Index Scan)
SELECT * FROM orders WHERE status = 'pending';
```

Cuando aplicas una funcion (`LOWER()`, `UPPER()`, `TRIM()`, etc.) a una columna, PostgreSQL no puede usar el indice porque:

1. El indice esta ordenado por el valor ORIGINAL de la columna
2. La funcion transforma el valor ANTES de comparar
3. PostgreSQL tiene que leer CADA fila, aplicar la funcion, y comparar

**Solucion real:** Normaliza los datos al insertarlos, no al consultarlos.

---

## Implementacion

### SlowQueryService (@Profile "before")

```java
@Service
@Profile("before")
public class SlowQueryService implements OrderService {

    @Override
    public List<Order> findOrdersByStatusAndDateRange(String status, ...) {
        // LOWER() invalida el indice - Seq Scan siempre
        return repository.findByStatusIgnoreCaseAndDateRange(status, start, end);
    }
}
```

### Query JPQL (lenta)

```java
@Query("SELECT o FROM Order o WHERE LOWER(o.status) = LOWER(:status) " +
       "AND o.createdAt BETWEEN :start AND :end")
List<Order> findByStatusIgnoreCaseAndDateRange(...);
```

### OptimizedQueryService (@Profile "after")

```java
@Service
@Profile("after")
public class OptimizedQueryService implements OrderService {

    @Override
    public List<Order> findOrdersByStatusAndDateRange(String status, ...) {
        // Normalizamos ANTES de la query, no dentro de ella
        String normalizedStatus = status.toLowerCase();
        return repository.findByStatusAndDateRangeOptimized(normalizedStatus, start, end);
    }
}
```

### Query JPQL (optimizada)

```java
@Query("SELECT o FROM Order o WHERE o.status = :status " +
       "AND o.createdAt BETWEEN :start AND :end")
List<Order> findByStatusAndDateRangeOptimized(...);
```

---

## EXPLAIN ANALYZE: Tu mejor amigo

```sql
EXPLAIN ANALYZE
SELECT * FROM orders
WHERE LOWER(status) = 'pending'
AND created_at BETWEEN '2024-01-01' AND '2024-12-31';
```

**Resultado ANTES (Seq Scan):**
```
Seq Scan on orders  (cost=0.00..285.00 rows=5000 width=64)
  Filter: ((lower(status) = 'pending') AND (created_at >= '2024-01-01') AND ...)
  Rows Removed by Filter: 5000
  Planning Time: 0.15 ms
  Execution Time: 45.23 ms
```

**Resultado DESPUES (Index Scan):**
```
Index Scan using idx_orders_status_created on orders (cost=0.29..8.30 rows=1 width=64)
  Index Cond: ((status = 'pending') AND (created_at >= '2024-01-01') AND ...)
  Planning Time: 0.12 ms
  Execution Time: 0.05 ms
```

La diferencia: **45ms vs 0.05ms** - casi **1000x mas rapido**.

---

## Hibernate Statistics

Habilita estadisticas en `application.yml`:

```yaml
spring:
  jpa:
    properties:
      hibernate:
        generate_statistics: true

logging:
  level:
    org.hibernate.stat: DEBUG
```

Ahora puedes ver:
- Cuantas queries se ejecutaron
- Tiempo de la query mas lenta
- Cache hits/misses

```java
Statistics stats = entityManager.unwrap(SessionFactory.class).getStatistics();
log.info("Queries ejecutadas: {}", stats.getQueryExecutionCount());
log.info("Query mas lenta: {} ms", stats.getQueryExecutionMaxTime());
```

---

## Ejecutar

### 1. Levantar PostgreSQL

```bash
docker compose up -d
```

### 2. Ejecutar la aplicacion

```bash
# Version BEFORE (query lenta con LOWER)
mvn spring-boot:run -Dspring-boot.run.profiles=before

# Version AFTER (query optimizada)
mvn spring-boot:run -Dspring-boot.run.profiles=after
```

### 3. Probar

```bash
# Buscar ordenes pending del 2024
curl "http://localhost:8080/api/orders?status=PENDING&start=2024-01-01T00:00:00&end=2024-12-31T23:59:59"

# Ver estadisticas de Hibernate
curl http://localhost:8080/api/orders/stats

# Ver EXPLAIN de query lenta
curl http://localhost:8080/api/orders/explain/slow

# Ver EXPLAIN de query optimizada
curl http://localhost:8080/api/orders/explain/optimized

# Ver indices de la tabla
curl http://localhost:8080/api/orders/indexes
```

### 4. Parar PostgreSQL

```bash
docker compose down
```

---

## Resultados

| | BEFORE | AFTER | Mejora |
|---|---|---|---|
| Tipo de Scan | Seq Scan | Index Scan | - |
| Tiempo | ~45ms | ~0.05ms | **900x** |
| Filas escaneadas | 10,000 | ~50 | **200x** |

---

## Estructura

```
day-003-query-analysis/
├── docker-compose.yml              # PostgreSQL 16, puerto 5436
├── pom.xml
├── src/main/java/.../
│   ├── Day003Application.java      # Inicializa 10,000 ordenes
│   ├── controller/OrderController.java
│   ├── model/
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── repository/
│   │   ├── OrderRepository.java
│   │   └── QueryAnalysisRepository.java  # EXPLAIN ANALYZE
│   └── service/
│       ├── OrderService.java
│       ├── SlowQueryService.java         # @Profile("before")
│       └── OptimizedQueryService.java    # @Profile("after")
└── diagrams/
```

---

## La Leccion

**El indice no te salva si no lo puedes usar.**

Antes de agregar indices:
1. Corre `EXPLAIN ANALYZE` en tu query
2. Busca "Seq Scan" - eso es malo
3. Busca funciones en el WHERE que invaliden indices
4. Normaliza datos al INSERT, no al SELECT

---

## Tech Stack

| Tecnologia | Version |
|------------|---------|
| Java | 25 |
| Spring Boot | 4.0.0 |
| Spring Framework | 7.0 |
| Spring Data JPA | 4.0 |
| Hibernate | 7.0 |
| PostgreSQL | 16 |
| Docker | Para BD local |

---

## Caracteristicas de Spring Boot 4 / Spring Framework 7 / Java 25 usadas

### Java 25

| Caracteristica | Uso en este proyecto |
|----------------|---------------------|
| **Records** | `IndexInfo` record para datos inmutables de indices |
| **Text Blocks** | Queries SQL multilinea con `"""` |
| **Pattern Matching** | Switch expressions en tests |
| **var** | Inferencia de tipos local |

```java
// Record (Java 14+, maduro en Java 25)
public record IndexInfo(String name, String definition) {}

// Text Block (Java 15+)
String sql = """
    SELECT indexname, indexdef
    FROM pg_indexes
    WHERE tablename = 'orders'
    """;
```

### Spring Boot 4 / Spring Framework 7

| Caracteristica | Descripcion |
|----------------|-------------|
| **Jakarta EE 11** | Namespace `jakarta.persistence.*` en lugar de `javax` |
| **Hibernate 7** | Estadisticas mejoradas, mejor SQL generation |
| **Virtual Threads Ready** | Queries pueden ejecutarse en virtual threads |
| **Observability** | Integracion nativa con Micrometer y OpenTelemetry |

### Hibernate 7 Statistics

```java
// Hibernate 7 en Spring Boot 4 - API mejorada
Statistics stats = entityManager.unwrap(SessionFactory.class).getStatistics();
stats.setStatisticsEnabled(true);

// Metricas disponibles
stats.getQueryExecutionCount();      // Total de queries
stats.getQueryExecutionMaxTime();    // Query mas lenta (ms)
stats.getQueryExecutionMaxTimeQueryString();  // Cual fue
```

### Jakarta Persistence (antes JPA)

```java
// Spring Boot 4 usa Jakarta EE 11
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

// Ya no es javax.persistence
```

---

## Requisitos

- Java 25
- Maven 3.9+
- Docker

---

**#100ArchitectureDays** | Dia 003/110
