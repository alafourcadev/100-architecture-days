# Dia 007: Cacheaste todo y la app sigue lenta

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

Tu endpoint tarda 3.3 segundos. No sabes por que. Cacheas cosas al azar. Sigue lento.

---

## La Solucion

```
ANTES:  Sin instrumentacion → optimizacion ciega, tirando dardos con los ojos vendados
DESPUES: Micrometer + Actuator → sabes exactamente donde estan los 3.2 segundos
```

**Concepto:** Profiling & Observability
**Regla:** Antes de optimizar, medi

---

## El Waterfall

```
pedido.buscar        ████  8ms
cliente.buscar       ██████  15ms
producto.buscarIds   ████████  22ms
descuento.calcular   ████████████████████████████████████████  3200ms
                     |--- Aqui esta el 98% del tiempo ---|
```

Sin medir, hubieras cacheado `producto.buscarIds` (22ms).
Con datos, sabes que `descuento.calcular` es el culpable.

---

## 3 preguntas antes de optimizar

1. **¿Donde esta el cuello de botella?** (Datos, no intuicion)
2. **¿Cuanto impacto tiene?** (Si es el 2% del tiempo, no importa)
3. **¿Cual es el costo de optimizarlo?** (A veces la solucion es mas cara que el problema)

---

## Ejecutar

```bash
# Version BEFORE (sin instrumentacion — no sabes donde esta el bottleneck)
mvn spring-boot:run -Dspring-boot.run.profiles=before
curl http://localhost:8080/api/pedidos/1
# → Tarda ~3.3s pero no sabes por que

# Version AFTER (con Micrometer — ves exactamente donde duele)
mvn spring-boot:run -Dspring-boot.run.profiles=after
curl http://localhost:8080/api/pedidos/1
# → Misma respuesta, misma duracion

# Pero ahora consulta las metricas:
curl http://localhost:8080/actuator/metrics/descuento.calcular
# → COUNT: 1, TOTAL_TIME: 3.2s, MAX: 3.2s — AHI ESTA EL BOTTLENECK

curl http://localhost:8080/actuator/metrics/pedido.buscar
# → COUNT: 1, TOTAL_TIME: 0.008s — Este no es el problema

# Prometheus endpoint con todas las metricas:
curl http://localhost:8080/actuator/prometheus | grep descuento
```

---

## Concepto Clave

La optimizacion no es una actividad creativa. Es una actividad cientifica. Hipotesis, medicion, conclusion. Sin datos, estas tirando dardos con los ojos vendados.
