# Day 004: Paginacion - O como deje de cargar 10,000 registros

## El Problema

Tu API de productos funciona perfectamente en desarrollo con 100 registros. Pero en produccion tienes 50,000 productos y cada request de `/api/products`:

- **Carga 50,000 registros en memoria** (potencial OutOfMemoryError)
- **Serializa 50,000 objetos a JSON** (CPU al 100%)
- **Transmite ~10MB por request** (red saturada)
- **El cliente espera 30 segundos** (timeout, usuarios frustrados)

## Estrategias de Paginacion

### 1. Sin Paginacion (Profile: `before`)

```sql
SELECT * FROM products
```

| Aspecto | Evaluacion |
|---------|------------|
| Simplicidad | Alta |
| Rendimiento | Terrible O(n) |
| Uso de memoria | Todo en RAM |
| Caso de uso | Solo datasets < 100 registros |

### 2. Offset Pagination (Profile: `offset`)

```sql
SELECT * FROM products ORDER BY id LIMIT 20 OFFSET 1000
```

| Aspecto | Evaluacion |
|---------|------------|
| Simplicidad | Alta (Spring Data Pageable) |
| Rendimiento | O(offset) - degrada en paginas altas |
| Metadata | Completa (totalPages, totalElements) |
| Caso de uso | UI con paginas numeradas |
| Problema | Pagina 2000 es muy lenta |

### 3. Cursor Pagination (Profile: `after`)

```sql
SELECT * FROM products WHERE id > 1000 ORDER BY id LIMIT 20
```

| Aspecto | Evaluacion |
|---------|------------|
| Simplicidad | Media |
| Rendimiento | O(1) - constante siempre |
| Metadata | Solo hasNext/hasPrevious |
| Caso de uso | Scroll infinito, APIs, grandes datasets |
| Problema | No puede saltar a pagina N |

## Comparativa de Rendimiento

```
Pagina 1 (offset=0):
  - Offset:  5ms
  - Cursor:  5ms

Pagina 100 (offset=2000):
  - Offset:  15ms
  - Cursor:  5ms

Pagina 1000 (offset=20000):
  - Offset:  150ms
  - Cursor:  5ms

Pagina 2500 (offset=50000):
  - Offset:  400ms+
  - Cursor:  5ms
```

## Como Ejecutar

### 1. Iniciar PostgreSQL

```bash
cd day-004-pagination
docker-compose up -d
```

### 2. Ejecutar con diferentes profiles

```bash
# Sin paginacion (PELIGROSO en produccion)
mvn spring-boot:run -Dspring-boot.run.profiles=before

# Offset pagination (tradicional)
mvn spring-boot:run -Dspring-boot.run.profiles=offset

# Cursor pagination (recomendado)
mvn spring-boot:run -Dspring-boot.run.profiles=after
```

### 3. Probar endpoints

```bash
# Obtener productos
curl "localhost:8080/api/products?page=0&size=20"

# Offset pagination - comparar tiempos
curl "localhost:8080/api/products?page=0&size=20"    # Rapido
curl "localhost:8080/api/products?page=1000&size=20" # Lento
curl "localhost:8080/api/products?page=2000&size=20" # Muy lento

# Cursor pagination - siempre rapido
curl "localhost:8080/api/products?size=20"
curl "localhost:8080/api/products?cursor=100&size=20"
curl "localhost:8080/api/products?cursor=40000&size=20"  # Igual de rapido

# Estadisticas de la ultima consulta
curl "localhost:8080/api/products/stats"

# Comparar estrategias
curl "localhost:8080/api/products/compare"
```

## Estructura del Proyecto

```
day-004-pagination/
├── docker-compose.yml          # PostgreSQL en puerto 5437
├── pom.xml                     # Spring Boot 4.0.0 + Java 25
├── src/main/java/.../
│   ├── Day004Application.java  # Main + inicializacion de datos
│   ├── controller/
│   │   └── ProductController.java
│   ├── dto/
│   │   ├── CursorPage.java     # Record para cursor pagination
│   │   └── ProductSummary.java # Interface Projection
│   ├── model/
│   │   └── Product.java
│   ├── repository/
│   │   └── ProductRepository.java
│   └── service/
│       ├── ProductService.java          # Interface
│       ├── NoPaginationService.java     # @Profile("before")
│       ├── OffsetPaginationService.java # @Profile("offset")
│       └── CursorPaginationService.java # @Profile("after")
└── src/main/resources/
    └── application.yml
```

## El Patron Before/After

```java
// BEFORE: Sin paginacion - carga TODO
@Service
@Profile("before")
public class NoPaginationService implements ProductService {
    public Object getProducts(int page, int size, Long cursor) {
        return repository.findAll(); // PELIGRO: 50,000 registros!
    }
}

// OFFSET: Paginacion tradicional
@Service
@Profile("offset")
public class OffsetPaginationService implements ProductService {
    public Object getProducts(int page, int size, Long cursor) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));
        return repository.findAllProjectedBy(pageRequest);
    }
}

// AFTER: Cursor pagination
@Service
@Profile("after")
public class CursorPaginationService implements ProductService {
    public Object getProducts(int page, int size, Long cursor) {
        List<Product> products;
        if (cursor == null || cursor == 0) {
            products = repository.findFirstPage(PageRequest.of(0, size + 1));
        } else {
            products = repository.findByCursorAfter(cursor, PageRequest.of(0, size + 1));
        }
        boolean hasNext = products.size() > size;
        // ... construir CursorPage
    }
}
```

## Por que Offset es Lento en Paginas Altas?

PostgreSQL debe:
1. **Leer todos los registros** hasta el offset
2. **Ordenarlos** (si hay ORDER BY)
3. **Descartar** los primeros N registros
4. **Retornar** solo los siguientes M

```sql
-- Pagina 2000 con size=20
SELECT * FROM products ORDER BY id LIMIT 20 OFFSET 40000;
-- PostgreSQL lee 40,020 registros para retornar 20!
```

Con cursor pagination:
```sql
-- Cursor en ID 40000, size=20
SELECT * FROM products WHERE id > 40000 ORDER BY id LIMIT 20;
-- PostgreSQL usa el indice, lee solo 20 registros!
```

## Caracteristicas de Spring Boot 4 / Java 25

| Caracteristica | Donde se Usa | Descripcion |
|----------------|--------------|-------------|
| **Java Records** | `CursorPage.java` | DTOs inmutables con sintaxis concisa |
| **Interface Projection** | `ProductSummary.java` | Spring Data genera SELECT optimizado |
| **Text Blocks** | `application.yml` logs | Strings multilinea legibles |
| **Pattern Matching** | `Day004Application.java` | Switch expressions con `->` |
| **Spring Boot 4.0** | `pom.xml` | Ultima version con mejoras de rendimiento |
| **Spring Framework 7** | Dependencia transitiva | Soporte mejorado para virtual threads |
| **Hibernate 7** | JPA provider | Mejor generacion de SQL para paginacion |
| **Jakarta EE 11** | `jakarta.persistence.*` | Namespace actualizado |

### Records para Respuestas de Paginacion

```java
// Java 25 Record - inmutable, conciso, perfecto para DTOs
public record CursorPage<T>(
    List<T> content,
    String nextCursor,
    String previousCursor,
    boolean hasNext,
    boolean hasPrevious,
    int size
) {
    public static <T> CursorPage<T> of(List<T> content, String next, String prev, boolean hasNext) {
        return new CursorPage<>(content, next, prev, hasNext, prev != null, content.size());
    }
}
```

### Interface Projection

```java
// Spring Data JPA genera: SELECT id, name, price, category FROM products
public interface ProductSummary {
    Long getId();
    String getName();
    BigDecimal getPrice();
    String getCategory();
}
```

## Cuando Usar Cada Estrategia

| Escenario | Estrategia Recomendada |
|-----------|------------------------|
| Dashboard con pocas filas | Offset (metadata util) |
| UI con paginas numeradas | Offset (puede saltar) |
| Scroll infinito | Cursor (rendimiento constante) |
| API publica | Cursor (escalable) |
| Exportar datos | Cursor con streaming |
| Busqueda con filtros | Cursor sobre indice compuesto |

## Puntos Clave

1. **Offset pagination escala mal** - O(offset) significa que paginas altas son lentas
2. **Cursor pagination es O(1)** - El rendimiento es constante
3. **No hay solucion perfecta** - Offset tiene mejor UX, cursor mejor rendimiento
4. **El indice es crucial** - Sin indice en la columna de ordenamiento, ambos son lentos
5. **Considera el caso de uso** - Scroll infinito = cursor, paginas numeradas = offset

## Errores Comunes

1. **No tener indice en la columna de cursor**
   ```sql
   -- Asegurate de tener indice en la columna de ordenamiento
   CREATE INDEX idx_products_id ON products(id);
   ```

2. **Cursor inestable con inserciones**
   - Si usas timestamp como cursor, nuevos registros pueden aparecer/desaparecer

3. **Offset muy grande sin limite**
   - Siempre pon un limite maximo de pagina

4. **No manejar el caso "sin resultados"**
   - Verifica `hasNext` antes de seguir paginando
