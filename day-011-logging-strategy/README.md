# Dia 011: System.out.println en produccion

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

System.out.println para todo. Sin timestamp, sin nivel, sin thread, sin contexto.
3AM, produccion caido, 50.000 lineas de logs que no dicen nada.

---

## La Solucion

```
ANTES:  System.out.println("Error: " + e.getMessage()) → ruido puro
DESPUES: log.error("Fallo. clienteId={}, monto={}", clienteId, monto, e) → diagnostico en 2 segundos
```

**Concepto:** Structured Logging & Observability
**Regla:** Si no tiene timestamp, nivel y contexto, no es un log. Es ruido.

---

## Ejecutar

```bash
# BEFORE: System.out.println
mvn spring-boot:run -Dspring-boot.run.profiles=before

# Pago exitoso
curl -X POST "http://localhost:8080/api/pagos?clienteId=4821&monto=5000"
# → Mira la consola: "Procesando pago..." "Monto: 5000" — sin contexto

# Pago que falla (monto > 9000)
curl -X POST "http://localhost:8080/api/pagos?clienteId=9102&monto=15000"
# → "Error en pago: Gateway timeout" — que cliente? cuando? que thread?

# ---

# AFTER: SLF4J structured logging
mvn spring-boot:run -Dspring-boot.run.profiles=after

# Mismos requests
curl -X POST "http://localhost:8080/api/pagos?clienteId=4821&monto=5000"
# → 2026-04-13 INFO  [exec-7] PagoService : Procesando pago. clienteId=4821, monto=5000

curl -X POST "http://localhost:8080/api/pagos?clienteId=9102&monto=15000"
# → 2026-04-13 ERROR [exec-3] PagoService : Fallo. clienteId=9102, monto=15000
#   java.lang.RuntimeException: Gateway timeout...

# Cambiar nivel de log en caliente (sin redeploy):
curl -X POST http://localhost:8080/actuator/loggers/com.architecturedays.day011 \
  -H 'Content-Type: application/json' \
  -d '{"configuredLevel": "DEBUG"}'
```

---

## Concepto Clave

System.out.println no es logging. Es debugging de estudiante.
Un log de produccion necesita: timestamp, nivel, thread, contexto de negocio.
Sin eso, estas buscando una aguja en un pajar de ruido.
