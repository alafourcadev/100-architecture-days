# 🏗️ #100ArchitectureDays

> 110 problemas reales de arquitectura de software. Con código. Con métricas. Sin humo.

**Por [Alejandro Lafourcade](https://alafourca.dev)** — Ingeniero de Software | +10 años | 8 países

---

## ¿Qué es esto?

Un reto de 110 días donde resuelvo un problema real de arquitectura por día. Cada día incluye:

- 🔥 **El problema** — situación real que te va a sonar familiar
- 💻 **Código ANTES** — cómo se hace mal (y por qué)
- ✅ **Código DESPUÉS** — la solución correcta
- 📊 **Métricas** — números reales, no promesas
- 🧠 **El concepto** — el patrón arquitectónico detrás de la solución

**Stack:** Spring Boot 4 + Java 25 (pero los conceptos aplican a cualquier lenguaje)

---

## 📋 Bloques Temáticos

| Bloque | Días | Tema |
|--------|------|------|
| 1 | 001-025 | "¿Por qué mi app es tan lenta?" — Performance y optimización |
| 2 | 026-050 | "Mi código es un desastre y nadie lo entiende" — Clean code y testing |
| 3 | 051-070 | "El sistema se cayó y era viernes a las 6pm" — Resiliencia |
| 4 | 071-090 | "Deployear me da miedo" — CI/CD y operaciones |
| 5 | 091-110 | "Nadie entiende mis decisiones técnicas" — Comunicación técnica |

---

## 📅 Progreso

| Día | Tema | Artículo |
|-----|------|----------|
| 001 | Cold Start — Application Startup Optimization | [Leer artículo](https://alafourca.dev/blog/cold-start-spring-boot) |
| 002 | SELECT * — DTO Projection Pattern | [Leer artículo](https://alafourca.dev/blog/select-star-spring) |
| 003 | Query Analysis — EXPLAIN ANALYZE | [Leer artículo](https://alafourca.dev/blog/query-analysis-spring) |
| 004 | Pagination — Offset vs Cursor | [Leer artículo](https://alafourca.dev/blog/paginacion-spring) |
| 005 | N+1 Queries — JOIN FETCH & EntityGraph | [Leer artículo](https://alafourca.dev/blog/n-plus-one-queries-spring) |
| 006 | Cache Basics — Caching Strategy & Invalidation | [Leer artículo](https://alafourca.dev/blog/cache-spring-boot) |
| 007 | Profiling Bottleneck — Actuator & Micrometer | [Leer artículo](https://alafourca.dev/blog/profiling-bottleneck-spring) |
| 008 | Batch Processing — Async & Chunked Processing | [Leer artículo](https://alafourca.dev/blog/batch-processing-spring) |
| 009 | Connection Pool — HikariCP & Leak Prevention | [Leer artículo](https://alafourca.dev/blog/connection-pool-spring) |
| 010 | Database Indexes — Index Selectivity & Write Amplification | [Leer artículo](https://alafourca.dev/blog/indices-database) |
| 011 | Logging Strategy — Structured Logging & Observability | [Leer artículo](https://alafourca.dev/blog/logging-strategy) |
| 012 | Latency API — Parallel I/O & Latency Optimization | [Leer artículo](https://alafourca.dev/blog/latency-api) |

---

## 🔗 Links

- 📝 **Blog:** [alafourca.dev](https://alafourca.dev)
- 💼 **LinkedIn:** [Alejandro Lafourcade](https://www.linkedin.com/in/alafourcadedespaigne/)
- 🐦 **Twitter/X:** [@alafourcadev](https://x.com/alafourcadev)
- 📅 **Consultoría:** [Agendar sesión 1:1](https://cal.com/alafourcadev/consultoria-tecnica-1-1)

---

## ⚡ Cómo ejecutar cualquier día

```bash
cd day-XXX-slug
mvn spring-boot:run
```

Cada día es un proyecto Maven independiente. Solo necesitás Java 25 y Maven.

---

**#100ArchitectureDays** | Ingeniería sin filtros
