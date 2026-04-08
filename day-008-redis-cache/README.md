# Dia 008: Pusiste Redis y ahora tenes datos fantasma

> **#100ArchitectureDays** | Spring Boot 4 + Java 21 + Redis

---

## El Problema

Redis cachea datos que ya cambiaron en la BD. Los usuarios ven precios viejos, stock desactualizado, datos que ya no existen. Datos fantasma.

---

## La Solucion

```
ANTES:  @Cacheable sin TTL, sin invalidacion → datos fantasma por todos lados
DESPUES: Cache-Aside con RedisTemplate, TTL explicito, invalidacion en cada escritura
```

**Concepto:** Distributed Caching Patterns (Cache-Aside, Write-Through, Write-Behind)
**Regla:** Redis no es un upgrade gratuito. Necesita TTL, invalidacion y monitoreo.

---

## Los 3 Patrones

| Patron | Descripcion | Riesgo |
|--------|-------------|--------|
| Cache-Aside | App consulta Redis, si no esta va a BD | Cache miss en primer request |
| Write-Through | Escribe en BD y Redis sincrono | Escrituras mas lentas |
| Write-Behind | Escribe en Redis primero, BD asincrono | Si Redis cae, perdes datos |

---

## Requisitos

- Docker (para Redis)
- Java 21
- Maven

---

## Ejecutar

```bash
# 1. Levantar Redis
docker compose up -d

# 2. Version BEFORE (sin TTL, sin invalidacion — datos fantasma)
mvn spring-boot:run -Dspring-boot.run.profiles=before

curl http://localhost:8080/api/productos/1          # Precio: 2499.99 (se cachea)
curl -X PUT "http://localhost:8080/api/productos/1/precio?precio=1999"  # Cambio precio
curl http://localhost:8080/api/productos/1          # Sigue mostrando 2499.99 — DATO FANTASMA

# 3. Version AFTER (Cache-Aside con TTL e invalidacion)
mvn spring-boot:run -Dspring-boot.run.profiles=after

curl http://localhost:8080/api/productos/1          # Cache MISS → BD → guarda en Redis con TTL
curl http://localhost:8080/api/productos/1          # Cache HIT → Redis
curl -X PUT "http://localhost:8080/api/productos/1/precio?precio=1999"  # Actualiza BD + INVALIDA Redis
curl http://localhost:8080/api/productos/1          # Cache MISS → BD → dato fresco

# 4. Parar Redis
docker compose down
```

---

## Concepto Clave

Redis no es "solo poner un servidor". Es un componente de infraestructura que necesita TTL, invalidacion y monitoreo. Sin esas tres cosas, tus datos fantasma van a generar mas bugs de los que el cache resuelve.
