# Dia 014: El UserDTO que viajaba por todas las capas.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

Un `UserDTO` con 9 campos viaja por controller, service, notificaciones,
reportes y persistencia. Agregar un campo afecta 5 capas.
La password termina en clases que no deberian verla.

---

## La Solucion

```
ANTES:  Un UserDTO compartido por todas las capas
DESPUES: Un record por capa, cada uno con lo minimo necesario
```

- `CreateUserRequest`: lo que llega del cliente.
- `UserResponse`: lo que se devuelve al cliente.
- `UserNotificationInfo`: lo que necesita el servicio de notificaciones.
- `UserReportData`: lo que necesita el servicio de reportes.

**Concepto:** Coupling bajo entre capas, cohesion alta dentro de cada modulo.

---

## La regla de las 3 preguntas

Antes de reusar un DTO, preguntate:
1. Quien lo crea?
2. Quien lo consume?
3. Quien cambia cuando cambia el DTO?

Si la respuesta a 3 incluye capas distintas, no es un DTO compartido.
Es un acople disfrazado.

---

## Concepto Clave

DRY no aplica a DTOs.
Dos records identicos hoy no significan que evolucionaran iguales.
Mejor duplicar tres campos que acoplar tres capas.
