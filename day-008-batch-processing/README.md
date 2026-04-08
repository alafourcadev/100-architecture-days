# Dia 008: Tu endpoint procesa 1 millon de registros de golpe. Se nota.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

Un endpoint sincrono que carga todo en memoria, procesa uno por uno, y bloquea el thread HTTP durante minutos. Timeout. OOM. Usuarios frustrados.

---

## La Solucion

```
ANTES:  Sincrono, todo en memoria, 1 save por registro → timeout + OOM
DESPUES: Async, batches de 500, job tracking, progreso en tiempo real
```

**Concepto:** Batch Processing & Async Patterns
**Regla:** Segundos = sincrono. Minutos = async. Horas = colas o Spring Batch.

---

## Ejecutar

```bash
# Version BEFORE (sincrono — bloquea el thread HTTP)
mvn spring-boot:run -Dspring-boot.run.profiles=before

# Inicia procesamiento (va a tardar ~5s bloqueando)
curl -X POST http://localhost:8080/api/procesamiento/iniciar
# → Esperas... esperas... resultado despues de varios segundos

# Version AFTER (async — responde inmediatamente)
mvn spring-boot:run -Dspring-boot.run.profiles=after

# Inicia procesamiento (responde en < 100ms)
curl -X POST http://localhost:8080/api/procesamiento/iniciar
# → { "jobId": "abc123", "estado": "PENDIENTE", "urlEstado": "..." }

# Consulta progreso
curl http://localhost:8080/api/procesamiento/estado/abc123
# → { "procesados": 2500, "porcentaje": 50, "estado": "EN_PROGRESO" }
```

---

## Concepto Clave

HTTP fue disenado para respuestas rapidas. Si tu operacion tarda mas de unos segundos, no va en un endpoint sincrono. Recibe, encola, procesa en background, notifica.
