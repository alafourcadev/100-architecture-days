# Dia 012: Tu API responde pero 500ms despues

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

Tu endpoint devuelve 200 OK siempre. Cero errores. Pero tarda 500ms.
Para el usuario, medio segundo de nada pasando = "esta app anda mal".

---

## La Solucion

```
ANTES:  4 llamadas secuenciales = 85 + 180 + 150 + 120 = 535ms
DESPUES: 1 secuencial + 3 en paralelo = 85 + max(180, 150, 120) = 265ms
```

**Concepto:** Parallel I/O & Latency Optimization
**Regla:** Si dos operaciones no dependen entre si, no las hagas esperar.

---

## Ejecutar

```bash
# BEFORE — todo secuencial (~535ms)
mvn spring-boot:run -Dspring-boot.run.profiles=before
curl http://localhost:8080/api/productos/1
# → "latenciaMs": 535, "modo": "SECUENCIAL"

# AFTER — paralelo donde tiene sentido (~265ms)
mvn spring-boot:run -Dspring-boot.run.profiles=after
curl http://localhost:8080/api/productos/1
# → "latenciaMs": 265, "modo": "PARALELO"
```

---

## Percepcion del usuario

| Latencia | Percepcion |
|----------|-----------|
| < 100ms | Instantaneo |
| 100-300ms | Rapido |
| 300-1000ms | Lento, necesita loading indicator |
| > 1000ms | Roto, el usuario piensa que fallo |

---

## Concepto Clave

La latencia se acumula silenciosamente. Tu endpoint no tiene UNA operacion de 500ms.
Tiene cinco de 100ms cada una, esperando en fila sin necesidad.
