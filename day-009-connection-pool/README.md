# Dia 009: Tu app funciona con 10 usuarios y explota con 100

> **#100ArchitectureDays** | Spring Boot 4 + Java 21 + HikariCP

---

## El Problema

Un endpoint que pide una conexion al DataSource y "olvida" cerrarla.
Con un pool de 5 conexiones, a la sexta llamada el pool esta agotado.
Bienvenido a `SQLTransientConnectionException: Connection is not available`.

---

## La Solucion

```
ANTES:  dataSource.getConnection() sin cerrar → leak en cada llamada → pool agotado
DESPUES: JdbcTemplate → Spring gestiona el ciclo de vida automaticamente
```

**Concepto:** Connection Pool Management & Leak Prevention
**Regla:** Nunca llames a dataSource.getConnection() directamente. Dejá que Spring lo haga.

---

## Ejecutar

```bash
# Version BEFORE (leak en cada request)
mvn spring-boot:run -Dspring-boot.run.profiles=before

# Mira el pool fresco
curl http://localhost:8080/api/pool/estado
# → { "totalConexiones": 5, "activas": 0, "idle": 5, "esperando": 0 }

# Llama 5 veces — cada llamada leakea una conexion
for i in 1 2 3 4 5; do curl http://localhost:8080/api/reportes; echo; done

# Mira el pool — conexiones activas crecen y nunca bajan
curl http://localhost:8080/api/pool/estado
# → { "activas": 5, "idle": 0 }  ← POOL AGOTADO

# Llamada #6 — se cuelga 5 segundos y explota
curl http://localhost:8080/api/reportes
# → "Connection is not available, request timed out"

# ---

# Version AFTER (sin leaks)
mvn spring-boot:run -Dspring-boot.run.profiles=after

# Llama 1000 veces — el pool sigue impecable
for i in {1..1000}; do curl -s http://localhost:8080/api/reportes > /dev/null; done

curl http://localhost:8080/api/pool/estado
# → { "activas": 0, "idle": 5 }  ← Nada cambio
```

---

## La Formula

Del autor de HikariCP:

```
connections = ((core_count * 2) + effective_spindle_count)
```

4 cores + 1 SSD = **9 conexiones**. Si, nueve. En un pool bien configurado,
9 conexiones manejan miles de requests por segundo.

La clave no es tener muchas conexiones. Es **devolverlas rapido**.

---

## Concepto Clave

Si tu app dice "too many connections", el problema no es el limite.
Es cuanto tardas en devolver cada una. Mas conexiones sin arreglar el leak
solo te da mas tiempo antes de que explote.
