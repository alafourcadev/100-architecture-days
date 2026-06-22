# Dia 022: La call-site que te obliga a abrir la definición para saber qué hace.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

El equipo de notificaciones tiene un método central para enviarle mensajes al cliente
durante el ciclo de vida de un pedido. Después de tres sprints de features, la firma quedó así:

```java
notificationService.dispatch(order, true, false, true, false);
notificationService.dispatch(order, false, true, true, false);
notificationService.dispatch(order, true, false, false, true);
```

Tres llamadas reales en el código de producción. Tres enigmas.

Para saber qué hace cada una, tenés que abrir `NotificationService.dispatch()` y leer
la firma completa:

```java
public NotificationResult dispatch(Order order,
                                   boolean useEmail,
                                   boolean useSms,
                                   boolean highPriority,
                                   boolean ccAdmin)
```

Y si te equivocás en el orden de dos booleanos, el compilador no dice nada. Silencio total.

---

## Por qué duele

Un parámetro booleano en la firma de un método casi siempre grita que el método hace DOS cosas.
El daño es cuádruple:

**1. La call-site es ilegible.** `dispatch(order, true, false, true, false)` no le dice nada
al lector sin abrir la definición. El `true` de la posición 2 y el `true` de la posición 4 son
indistinguibles visualmente, pero significan cosas distintas.

**2. Los booleanos son posicionales, no nominales.** Swap dos argumentos y compilás igual.
El compilador no puede detectar `dispatch(order, false, true, true, false)` vs
`dispatch(order, true, false, true, false)`. Son dos tipos distintos de bug para el mismo
tipo de dato.

**3. Acopla decisiones no relacionadas.** La elección de canal (email/SMS) y la prioridad de
despacho son decisiones ortogonales. Vivir en la misma firma las hace parecer un solo concepto
cuando no lo son.

**4. Las combinaciones inválidas son irrepresentables... como error.** `(false, false, true, true)`
compila y corre. En la implementación ANTES el admin recibe notificaciones aunque el cliente
no esté en ningún canal — un comportamiento sorpresivo que ningún tipo previene.

Y la proyección obvia: el siguiente feature agrega un quinto booleano. Después un sexto.
`dispatch(order, true, false, true, false, true, false)` es adonde va este camino.

---

## La Trampa

Agregar booleanos se siente rápido. Un parámetro, un `if` adentro, el sprint cierra.

El costo se paga en code review: nadie puede leer la call-site sin saltar a la definición.
Y en mantenimiento: el día que alguien refactoriza el orden de los parámetros, los tests
que pasaban siguen pasando si se swapean dos `boolean` del mismo tipo.

La trampa es que el boolean parece "simple" — y lo es. El problema no es la complejidad
del tipo. El problema es que le da al compilador cero información sobre la intención.

---

## La Decisión y su Porqué

Tres técnicas, aplicadas con criterio según el caso:

### Técnica 1 — Separar en métodos con nombre e intención

Cuando un booleano elige entre dos comportamientos, el método hace dos cosas.
La solución es hacer dos métodos.

```java
// ANTES: el booleano en posición 2 y 3 elige el canal
service.dispatch(order, true,  false, true, false); // email
service.dispatch(order, false, true,  true, false); // SMS
service.dispatch(order, true,  true,  true, false); // ambos

// DESPUES: el nombre del método es el canal
service.notifyByEmail(order, options);
service.notifyBySms(order, options);
service.notifyByEmailAndSms(order, options);
```

La call-site ahora se lee como una frase. No hay nada que interpretar.

**Trade-off:** más métodos públicos en la interfaz. Vale la pena si los comportamientos
son distintos y estables. Si el canal fuera completamente dinámico (definido en runtime
desde una config externa), un enum sería mejor.

### Técnica 2 — Reemplazar el booleano por un enum con significado de dominio

Cuando el booleano representa una opción de configuración legítima (no elige entre dos
comportamientos, sino que ajusta uno), un enum le da nombre a cada estado.

```java
// ANTES
service.dispatch(order, true, false, true,  false); // highPriority=true
service.dispatch(order, true, false, false, false); // highPriority=false

// DESPUES
service.notifyByEmail(order, NotificationOptions.builder()
        .priority(NotificationPriority.HIGH)
        .build());
```

Si mañana aparece `CRITICAL`, se agrega al enum. Los call-sites existentes no cambian.
Con `boolean`, agregar un tercer estado requiere romper la firma.

### Técnica 3 — Parameter object con builder (cuando las opciones viajan juntas)

Cuando varios booleanos/opciones forman un grupo cohesivo, un parameter object las agrupa.
El builder les da nombre en el call-site y las factory methods encapsulan los presets de dominio.

```java
// ANTES: cuatro booleans de intención opaca
service.dispatch(order, true, false, false, true);

// DESPUES: el objeto de opciones y su factory dicen lo que pasa
service.notifyByEmail(order, NotificationOptions.builder()
        .includeAdmin(true)
        .build());

// O con preset de dominio
service.notifyByEmailAndSms(order, NotificationOptions.forFraudAlert());
```

Agregar una opción nueva (`includePdf`, `replyTo`) no cambia ningún call-site existente.
El campo tiene un default razonable y los sites que no lo necesitan no lo ven.

---

## Antes (cuatro booleanos, call-site ilegible)

```java
// ¿Qué hace esta línea? No hay forma de saberlo sin abrir dispatch().
// El compilador no detecta si swapeás el orden de dos boolean.
notificationService.dispatch(order, true, false, true, false);

// Combinación inválida que compila y corre sin error:
// el admin recibe aunque el cliente no esté en ningún canal.
notificationService.dispatch(order, false, false, true, true);
```

## Despues (call-site legible, estados inválidos irrepresentables)

```java
// Envío por email, confirmación estándar.
notificationService.notifyByEmail(order, NotificationOptions.forConfirmation());

// Envío por SMS, alta prioridad (pedido despachado).
notificationService.notifyBySms(order, NotificationOptions.forShipment());

// Email + SMS, alta prioridad, con copia al admin (alerta de fraude).
notificationService.notifyByEmailAndSms(order, NotificationOptions.forFraudAlert());

// La combinación "admin sin canal de cliente" ya no existe.
// No se puede construir: notifyByEmail siempre incluye al cliente.
```

---

## Estructura

```
src/main/java/dev/alafourca/day022/
  antes/
    NotificationService.java    → dispatch() con cuatro booleanos, call-site opaca
    Order.java                  → entidad de pedido
    NotificationResult.java     → resultado del envío

  despues/
    NotificationService.java    → notifyByEmail / notifyBySms / notifyByEmailAndSms
    NotificationOptions.java    → parameter object con builder y factory methods de dominio
    NotificationPriority.java   → enum en lugar de boolean highPriority
    Order.java                  → entidad de pedido
    NotificationResult.java     → resultado del envío

src/test/java/dev/alafourca/day022/
  BooleanParametersAntesBehaviorTest.java  → documenta el comportamiento ANTES, incluye el caso inválido
  BooleanParametersEquivalenceTest.java    → verifica que DESPUES produce el mismo output para cada combinación
  NotificationOptionsTest.java             → verifica cada preset y cada opción del builder de forma aislada
```

---

## Métricas

| Métrica | Antes | Después |
|---------|-------|---------|
| Parámetros booleanos en la firma | 4 | 0 |
| Combinaciones posibles (2^n) | 16 | todas representables con nombre |
| Call-site legible sin abrir la definición | No | Si |
| El compilador previene swap accidental de booleans | No | Si (tipos distintos) |
| Agregar "notificación push" requiere cambiar firmas existentes | Si (5to boolean) | No (nuevo método + opción) |
| Estado inválido (admin sin canal cliente) | Representable y silencioso | Irrepresentable |

---

## Concepto Clave

**Flag argument:** un parámetro booleano que controla el comportamiento interno de un método.
La señal de que el método hace más de una cosa, y que la call-site pierde toda legibilidad.

Las tres herramientas en orden de aplicación:

1. **Métodos con intención** — cuando el booleano elige entre dos comportamientos: hacé dos métodos.
2. **Enum** — cuando el booleano representa un estado de dominio con nombre: dale nombre.
3. **Parameter object** — cuando son varias opciones relacionadas que viajan juntas: agrupalas.

El objetivo no es eliminar la configuración. Es que la call-site se lea como una frase,
y que los estados inválidos sean irrepresentables en el tipo — no en la documentación.
