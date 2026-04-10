# Dia 010: Pusiste indices en todas las columnas. Ahora los INSERT son lentos.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

Un indice por columna "por si acaso". Diez indices en una tabla transaccional.
Cada INSERT tiene que actualizar 10 estructuras B-tree adicionales.
Las escrituras se arrastran.

---

## La Solucion

```
ANTES:  10 indices → escrituras lentas, disco inflado, sin mejora real en reads
DESPUES: 2 indices quirurgicos → escrituras veloces, mismos reads
```

**Concepto:** Index Selectivity & Write Amplification
**Regla:** Un indice es un contrato: aceptas pagar mas en escritura para ganar en UNA lectura especifica.

---

## Ejecutar

```bash
mvn spring-boot:run

# Benchmark de INSERT: compara tabla con 10 indices vs 2 indices
curl -X POST "http://localhost:8080/api/benchmark/insert?cantidad=5000"

# Response:
# {
#   "cantidadRegistros": 5000,
#   "antesConDiezIndices": "1240ms",
#   "despuesConDosIndices": "380ms",
#   "mejoraPorcentual": "69.4%",
#   "veloces": "DESPUES es 3.3x mas rapido"
# }

# Probar con mas volumen para ver mas diferencia
curl -X POST "http://localhost:8080/api/benchmark/insert?cantidad=20000"

# Benchmark de SELECT: los reads apenas cambian
curl http://localhost:8080/api/benchmark/select/1

# Estadisticas
curl http://localhost:8080/api/benchmark/estadisticas
```

---

## Que vas a ver

| | ANTES (10 indices) | DESPUES (2 indices) |
|---|---|---|
| INSERT 5K | ~1200ms | ~380ms |
| SELECT por cliente | ~5ms | ~5ms |
| Indices | 10 | 2 |

Los SELECTs apenas cambian porque los 8 indices extra casi nunca se usaban.
Estaban ahi consumiendo disco y frenando escrituras sin aportar nada.

---

## Las 4 reglas para decidir que indexar

1. **Mira las queries reales, no las que imaginas**
   Si no hay una query real que use esa columna en un WHERE/JOIN/ORDER BY, no la indexes.

2. **Indices compuestos > indices individuales**
   Un indice en (clienteId, fecha) cubre muchas mas queries que dos indices separados.

3. **Baja cardinalidad = mal candidato**
   Una columna "estado" con 4 valores filtra el 25% de la tabla. Casi nunca justifica indice solo.

4. **Regla del 5%**
   Si una query devuelve mas del 5% de las filas, el optimizador ignora el indice y hace full scan.

---

## Concepto Clave

Poner un indice en cada columna es como poner un semaforo en cada esquina.
En algun momento, el remedio es peor que la enfermedad.
