# Day 005 — N+1 Queries

> El bug que tu DBA ya sabe que tenés

Para mostrar 50 usuarios tu app hace 251 queries a la base de datos. No falla. No tira excepción. Simplemente tarda 4 segundos y nadie sabe por qué.

## El problema

Tu ORM carga relaciones lazy: cada vez que accedés a una lista de pedidos dentro de un loop, dispara un SELECT nuevo. Uno por cada entidad. 1 + N = 51 queries mínimo.

## La solución

```java
@Query("SELECT DISTINCT u FROM Usuario u JOIN FETCH u.pedidos")
List<Usuario> findAllConPedidos();
```

251 queries → 1 query. De 4200ms a 85ms.

## Cómo correr

### Prerequisitos

- Java 25+
- Docker

### Levantar la base de datos

```bash
docker compose up -d
```

### Correr con el problema N+1

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=before
```

Llamá a `GET http://localhost:8080/api/usuarios` y mirá el log SQL. Vas a ver 51+ SELECT statements.

### Correr con la solución (JOIN FETCH)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=after
```

Mismo endpoint, 1 solo SELECT con JOIN. Mirá la diferencia.

### Tests

```bash
mvn test
```

Los tests usan Hibernate Statistics para verificar programáticamente que:
- `findAll()` genera 50 cargas lazy (N+1)
- `findAllConPedidos()` genera 0 cargas lazy (JOIN FETCH)
- `findAllConPedidosEntityGraph()` también genera 0 (@EntityGraph)

## Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/usuarios` | Lista usuarios con resumen de pedidos |

## Qué observar

Con `show-sql: true` y `generate_statistics: true` activados:

**Profile `before`:**
```
SELECT * FROM usuarios;
SELECT * FROM pedidos WHERE usuario_id = 1;
SELECT * FROM pedidos WHERE usuario_id = 2;
... (50 veces más)
```

**Profile `after`:**
```
SELECT u.*, p.* FROM usuarios u JOIN pedidos p ON u.id = p.usuario_id;
```

## Artículo completo

[N+1 Queries: el bug que tu DBA ya sabe que tenés](https://alafourca.dev/blog/n-plus-one-queries-spring)

Parte de [#100ArchitectureDays](https://github.com/alafourcadev/100-architecture-days) — 100 problemas reales de arquitectura con soluciones reales.
