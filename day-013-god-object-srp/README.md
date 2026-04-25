# Dia 013: El OrderService que sabia demasiado.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

`OrderService` con 11 dependencias en el constructor y 50+ metodos.
Sabe de pagos, descuentos, emails, PDFs, almacenes, metricas y auditoria.
Cada PR que toca esta clase rompe algo no relacionado.

---

## La Solucion

```
ANTES:  1 servicio con 11 deps que hace todo
DESPUES: 4 servicios pequenos comunicados por eventos
```

- `OrderService` (3 deps): crea la orden y publica `OrderCreatedEvent`.
- `OrderPricingService` (1 dep): calcula totales con descuentos.
- `OrderNotificationService` (2 deps): escucha el evento y manda email + PDF.
- `OrderFulfillmentService` (1 dep): escucha el evento y reserva stock.

**Concepto:** Single Responsibility Principle (SRP).
**Regla:** una clase debe tener una unica razon para cambiar.

---

## Estructura

```
src/main/java/com/architecturedays/day013/
  antes/    → OrderService monstruo (11 deps, throws UnsupportedOperationException)
  despues/  → 4 servicios pequenos + OrderCreatedEvent
```

---

## Concepto Clave

Si tu clase necesita una scrollbar para leer su constructor, no es una clase.
Es un equipo entero disfrazado de objeto.

Una razon para cambiar. Una sola.
