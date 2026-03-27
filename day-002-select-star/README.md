# Dia 002: SELECT *

> **#100ArchitectureDays** | Spring Boot 4 + Java 25

---

## El Problema

Hice un `SELECT * FROM users` y la API responde en 10 segundos.

Spoiler: no necesitaba traer las fotos en base64.

50 usuarios. Cada uno con foto de 500KB. El endpoint devuelve 25MB de JSON. El frontend solo necesitaba id, email y nombre.

---

## La Solucion

```
ANTES:  SELECT * FROM users     -> 25 MB payload
DESPUES: SELECT id, email, name -> 5 KB payload
```

**Concepto:** Query Optimization
**Patron:** Interface Projection (Spring Data JPA)

---

## Implementacion

### SelectStarUserService (@Profile "before")

```java
@Service
@Profile("before")
public class SelectStarUserService implements UserService {

    @Override
    public List<?> findAllUsers() {
        return repository.findAll();  // SELECT * - trae TODO
    }
}
```

### ProjectionUserService (@Profile "after")

```java
@Service
@Profile("after")
public class ProjectionUserService implements UserService {

    @Override
    public List<?> findAllUsers() {
        return repository.findAllProjectedBy();  // Solo id, email, name
    }
}
```

### Interface Projection (la forma idiomatica de Spring)

```java
public interface UserSummary {
    Long getId();
    String getEmail();
    String getName();
}
```

### El Repository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring genera automaticamente: SELECT id, email, name FROM users
    List<UserSummary> findAllProjectedBy();
}
```

Spring Data JPA detecta que `UserSummary` es una interfaz y genera un proxy que solo trae las columnas necesarias. Sin `@Query`, sin fully qualified names, sin magia oscura.

---

## Ejecutar

### 1. Levantar PostgreSQL

```bash
docker compose up -d
```

### 2. Ejecutar la aplicacion

```bash
# Version BEFORE (SELECT *)
mvn spring-boot:run -Dspring-boot.run.profiles=before

# Version AFTER (proyeccion)
mvn spring-boot:run -Dspring-boot.run.profiles=after
```

### 3. Probar

```bash
# Ver usuarios
curl http://localhost:8080/api/users

# Ver estadisticas de payload
curl http://localhost:8080/api/users/stats
```

### 4. Parar PostgreSQL

```bash
docker compose down
```

---

## Resultados

| | BEFORE | AFTER | Mejora |
|---|---|---|---|
| Payload | ~25 MB | ~5 KB | **99.98%** |

---

## Estructura

```
day-002-select-star/
├── docker-compose.yml              # PostgreSQL
├── pom.xml
├── src/main/java/.../
│   ├── Day002Application.java
│   ├── controller/UserController.java
│   ├── model/User.java
│   ├── dto/UserSummary.java          # Interface Projection
│   ├── repository/UserRepository.java
│   └── service/
│       ├── UserService.java
│       ├── SelectStarUserService.java    # @Profile("before")
│       └── ProjectionUserService.java    # @Profile("after")
└── diagrams/
```

---

## La Leccion

No traigas lo que no necesitas.

Si el frontend solo muestra nombre y email, no le mandes la foto de 500KB.

Las proyecciones de JPA existen por algo. Usalas.

---

## Tech Stack

| Tecnologia | Version |
|------------|---------|
| Java | 25 |
| Spring Boot | 4.0.0 |
| Spring Framework | 7.0 |
| Spring Data JPA | 4.0 |
| PostgreSQL | 16 |
| Docker | Para BD local |

---

## Caracteristicas de Spring Boot 4 / Spring Framework 7 / Java 25 usadas

### Java 25

| Caracteristica | Uso en este proyecto |
|----------------|---------------------|
| **Interface como Projection** | `UserSummary` interface para datos inmutables |
| **Virtual Threads Ready** | Queries pueden ejecutarse en virtual threads |

```java
// Interface Projection - Spring genera proxy automaticamente
public interface UserSummary {
    Long getId();
    String getEmail();
    String getName();
}
```

### Spring Boot 4 / Spring Framework 7

| Caracteristica | Descripcion |
|----------------|-------------|
| **Jakarta EE 11** | Namespace `jakarta.persistence.*` en lugar de `javax` |
| **Hibernate 7** | Mejor optimizacion de queries con proyecciones |
| **Spring Data JPA 4.0** | Interface Projections mejoradas |

### Spring Data JPA Interface Projection

```java
// Spring Data JPA 4.0 detecta la interface y genera:
// SELECT id, email, name FROM users
// Sin @Query, sin magia oscura
List<UserSummary> findAllProjectedBy();
```

Este patron existe desde Spring Data JPA 1.x, pero en Spring Boot 4 con Hibernate 7 las proyecciones son mas eficientes.

---

## Requisitos

- Java 25
- Maven 3.9+
- Docker

---

**#100ArchitectureDays** | Dia 002/110
