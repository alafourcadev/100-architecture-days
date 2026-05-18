# Dia 016: El if nuevo que rompio 5 features.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

`NotificationService` con un if/else-if por cada canal soportado.
Sprint 4: alguien agrego PUSH y, al tocar la condicion de validacion
de canal, rompio los tests de EMAIL y SMS.
Sprint 7: lo mismo con SLACK. Sprint 11: con WHATSAPP.

Cada canal nuevo obliga a abrir la misma clase y operar en codigo
que ya andaba. La superficie de riesgo crece con cada feature.

---

## La Solucion

```
ANTES:  1 clase con 5 else-if — se modifica por cada canal nuevo
DESPUES: interfaz NotificationSender + 1 clase por canal, NotificationService no cambia
```

- `NotificationSender` (interfaz): `supports(channel)` + `send(request)`.
- `EmailNotificationSender`, `SmsNotificationSender`, `PushNotificationSender`,
  `SlackNotificationSender`: cada uno encapsula la logica de su canal.
- `NotificationService`: recibe `List<NotificationSender>` inyectada por Spring,
  delega sin saber que implementaciones existen.

Agregar WHATSAPP = crear `WhatsappNotificationSender implements NotificationSender`.
`NotificationService` no se toca. Los tests de EMAIL, SMS y PUSH no se tocan.

---

## Antes (viola OCP)

```java
// Para agregar WHATSAPP hay que modificar esta clase
public void send(NotificationRequest request) {
    if (channel.equals("EMAIL")) {
        // logica email
    } else if (channel.equals("SMS")) {
        // logica sms
    } else if (channel.equals("PUSH")) {
        // logica push — sprint 4, rompio EMAIL
    } else if (channel.equals("SLACK")) {
        // logica slack — sprint 7, rompio SMS
    } else if (channel.equals("WHATSAPP")) {
        // logica whatsapp — sprint 11, merge conflict otra vez
    }
}
```

## Despues (cumple OCP)

```java
// Punto de extension — nadie toca esto para agregar un canal
public interface NotificationSender {
    boolean supports(String channel);
    void send(NotificationRequest request);
}

// NotificationService no tiene un solo if sobre el canal
public void send(NotificationRequest request) {
    senders.stream()
            .filter(sender -> sender.supports(request.getChannel()))
            .findFirst()
            .orElseThrow(...)
            .send(request);
}

// Canal nuevo = clase nueva. Nada existente cambia.
@Component
public class WhatsappNotificationSender implements NotificationSender { ... }
```

---

## Estructura

```
src/main/java/com/architecturedays/day016/
  antes/    → NotificationService con else-if por canal
  despues/  → NotificationSender + implementaciones independientes por canal
```

---

## Concepto Clave

**Open/Closed Principle:** una clase debe estar abierta para extension
y cerrada para modificacion.

No significa "nunca toques ese archivo". Significa que agregar comportamiento
nuevo no deberia requerir modificar comportamiento existente.

Si cada feature nueva te obliga a abrir la misma clase, esa clase es un
cuello de botella de cambio. El if nuevo que rompió 5 features no fue un
error de quien lo escribio: fue un error de diseño previo que lo hizo necesario.
