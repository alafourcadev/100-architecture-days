# Dia 001: Cold Start

> **#100ArchitectureDays** | Spring Boot 4 + Java 25

---

## El Problema

La app tarda 11 segundos en cargar. Los usuarios piensan que esta caida.

El problema: confundir **"app lista"** con **"todo inicializado"**.

---

## La Solucion

```
ANTES:  App Startup -> [Operaciones Pesadas BLOQUEAN] -> App Ready (10.7s)
DESPUES: App Startup -> App Ready (1.3s) -> [Operaciones en Background]
```

**Concepto:** Application Startup Optimization
**Patron:** Deferred Initialization

---

## Implementacion

### BlockingProductService (@Profile "before")

```java
@Service
@Profile("before")
public class BlockingProductService implements ProductService {

    @PostConstruct
    public void initialize() {
        syncWithSupplierAPI();   // 5s - BLOQUEA
        validateWarehouse();      // 3s - BLOQUEA
        initializeProducts();     // 1s - BLOQUEA
    }
}
```

### AsyncProductService (@Profile "after")

```java
@Service
@Profile("after")
public class AsyncProductService implements ProductService {

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void initializeAsync() {
        syncWithSupplierAPI();   // 5s - background
        validateWarehouse();      // 3s - background
        initializeProducts();     // 1s - background
    }
}
```

---

## Resultados

| | BEFORE | AFTER | Mejora |
|---|---|---|---|
| Startup | 10.7s | 1.3s | **88%** |

---

## Ejecutar

```bash
# Version BEFORE (lenta)
mvn spring-boot:run -Dspring-boot.run.profiles=before

# Version AFTER (rapida)
mvn spring-boot:run -Dspring-boot.run.profiles=after

# Health check
curl http://localhost:8080/api/products/health
```

---

## Estructura

```
day-001-cold-start/
├── pom.xml
├── src/main/java/.../
│   ├── Day001Application.java
│   ├── controller/ProductController.java
│   ├── model/Product.java
│   ├── repository/ProductRepository.java
│   └── service/
│       ├── ProductService.java
│       ├── BlockingProductService.java   # @Profile("before")
│       └── AsyncProductService.java      # @Profile("after")
```

---

## La Leccion

El usuario no necesita esperar a que TODO este listo. Solo necesita que la app responda.

Lo que puede esperar, que espere en background.

---

## Tech Stack

| Tecnologia | Version |
|------------|---------|
| Java | 25 |
| Spring Boot | 4.0.0 |
| Spring Framework | 7.0 |
| Spring Data JPA | 4.0 |
| Hibernate | 7.0 |

---

## Caracteristicas de Spring Boot 4 / Spring Framework 7 / Java 25 usadas

### Anotaciones de Spring

| Anotacion | Descripcion |
|-----------|-------------|
| `@Async` | Ejecuta metodos en thread separado (o Virtual Thread en Java 25) |
| `@EventListener(ApplicationReadyEvent.class)` | Dispara despues del startup |
| `@Profile` | Activa beans segun perfil activo |
| `@PostConstruct` | Ejecuta durante inicializacion del bean |
| `@EnableAsync` | Habilita procesamiento asincrono |

### Spring Boot 4 / Spring Framework 7

| Caracteristica | Descripcion |
|----------------|-------------|
| **Jakarta EE 11** | Namespace `jakarta.*` en lugar de `javax` |
| **Virtual Threads** | `@Async` puede usar virtual threads con config |
| **Observability** | Metricas de startup integradas con Micrometer |

### Java 25

| Caracteristica | Uso |
|----------------|-----|
| **Virtual Threads** | Threads livianos para operaciones async |
| **Records** | Inmutabilidad para DTOs |

---

## Requisitos

- Java 25
- Maven 3.9+

---

**#100ArchitectureDays** | Dia 001/110
