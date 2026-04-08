# Dia 008: El usuario subio un Excel y tu servidor pidio perdon

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

Un endpoint sincrono que carga todo un CSV en memoria, procesa fila por fila, guarda uno por uno, y bloquea el thread HTTP hasta terminar. Timeout. OOM. Usuarios frustrados.

---

## La Solucion

```
ANTES:  Sincrono, todo en memoria, 1 save por registro → timeout + OOM
DESPUES: Async con jobId, batches de 500, progreso en tiempo real
```

**Concepto:** Batch Processing & Async Patterns
**Regla:** Segundos = sincrono. Minutos = async. Horas = colas o Spring Batch.

---

## CSV de ejemplo incluido

El proyecto incluye `ejemplo-importacion.csv` con 5.000 registros listos para probar.

---

## Ejecutar

```bash
# Version BEFORE (sincrono — bloquea el thread HTTP)
mvn spring-boot:run -Dspring-boot.run.profiles=before

# Subir el CSV — espera... espera... el usuario sufre
curl -X POST -F "archivo=@src/main/resources/ejemplo-importacion.csv" \
  http://localhost:8080/api/importar
# → Tarda ~8 segundos bloqueando. Resultado recien al final.

# ---

# Version AFTER (async — responde en < 100ms)
mvn spring-boot:run -Dspring-boot.run.profiles=after

# Subir el CSV — respuesta inmediata
curl -X POST -F "archivo=@src/main/resources/ejemplo-importacion.csv" \
  http://localhost:8080/api/importar
# → { "jobId": "a1b2c3d4", "estado": "PENDIENTE", "urlEstado": "..." }

# Consultar progreso
curl http://localhost:8080/api/importar/estado/a1b2c3d4
# → { "procesados": 2500, "porcentaje": 50, "estado": "EN_PROGRESO" }

# Consultar de nuevo
curl http://localhost:8080/api/importar/estado/a1b2c3d4
# → { "procesados": 5000, "porcentaje": 100, "estado": "COMPLETADO" }
```

---

## Concepto Clave

HTTP fue disenado para respuestas rapidas. Si tu operacion tarda mas de unos segundos, no va en un endpoint sincrono. Recibi, encola, procesa en background, notifica. Trata al usuario como adulto: decile cuanto falta.
