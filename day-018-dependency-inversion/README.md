# Dia 018: Cambiar de proveedor sin tocar el core.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

`ReportService` necesita notificar a los usuarios cuando un reporte está listo.
La implementación obvia: instanciar `EmailNotificationService` directamente en el constructor.

Sprint 3: el negocio decide migrar a SMS. Para hacerlo hay que abrir `ReportService`,
eliminar el campo `EmailNotificationService`, agregar `SmsNotificationService`,
reescribir el método, y cruzar los dedos para que los tests de negocio no se rompan.

Sprint 8: quieren push notifications. El mismo circo. Sprint 12: Slack. Otra vez.

El módulo de alto nivel (lógica de negocio) depende del módulo de bajo nivel
(proveedor de infraestructura). Cada cambio de proveedor requiere abrir el core.

---

## La Solucion

```
ANTES:  ReportService instancia EmailNotificationService — proveedor hardcodeado en el core
DESPUES: ReportService depende de NotificationPort — cualquier adaptador se inyecta desde afuera
```

- `NotificationPort` (interfaz): el contrato que el dominio define. `notify(recipient, subject, message)`.
- `EmailNotificationAdapter`, `SmsNotificationAdapter`: implementaciones concretas de bajo nivel.
- `ReportService`: recibe `NotificationPort` por constructor. No importa ninguna clase de infraestructura.

Agregar push = crear `PushNotificationAdapter implements NotificationPort`.
`ReportService` no se toca. Sus tests no se rompen. El core no sabe que hubo un cambio.

---

## Antes (viola DIP)

```java
// Para cambiar a SMS hay que abrir esta clase
public class ReportService {

    // Módulo de alto nivel crea y conoce el módulo de bajo nivel
    private final EmailNotificationService emailNotificationService;

    public ReportService() {
        this.emailNotificationService = new EmailNotificationService();
    }

    public void generateAndNotify(String reportName, String recipient) {
        String result = generateReport(reportName);

        // Acoplado al contrato de EmailNotificationService (sendEmail, subject, body)
        // Cambiar a SMS = reescribir este bloque entero + el campo + el constructor
        emailNotificationService.sendEmail(recipient, "Report ready: " + reportName, result);
    }
}
```

## Despues (cumple DIP)

```java
// Abstracción definida por el dominio — ni email ni SMS, solo "notify"
public interface NotificationPort {
    void notify(String recipient, String subject, String message);
}

// Módulo de alto nivel depende de la abstracción
public class ReportService {

    private final NotificationPort notificationPort;

    public ReportService(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    public void generateAndNotify(String reportName, String recipient) {
        String result = generateReport(reportName);

        // No sabe qué hay detrás. Email, SMS, push — no importa.
        notificationPort.notify(recipient, "Report ready: " + reportName, result);
    }
}

// Canal nuevo = clase nueva. ReportService no se toca.
public class SmsNotificationAdapter implements NotificationPort {
    @Override
    public void notify(String recipient, String subject, String message) {
        System.out.printf("[SMS] To: %s | %s: %s%n", recipient, subject, message);
    }
}
```

---

## Estructura

```
src/main/java/dev/alafourca/day018/
  antes/    → ReportService con EmailNotificationService hardcodeada en el constructor
  despues/  → NotificationPort + EmailNotificationAdapter + SmsNotificationAdapter + ReportService desacoplado

src/test/java/dev/alafourca/day018/
  DipViolationTest.java   → documenta que el core queda cerrado a extensión en el diseño "antes"
  DipComplianceTest.java  → verifica que cualquier NotificationPort se inyecta sin tocar ReportService
```

---

## Concepto Clave

**Dependency Inversion Principle:** los módulos de alto nivel no deben depender
de módulos de bajo nivel. Ambos deben depender de abstracciones.

En criollo: tu lógica de negocio no debería saber si la notificación va por email,
SMS o palomas mensajeras. Eso es un detalle de infraestructura. El dominio define
el contrato (`NotificationPort`), la infraestructura lo implementa. Nunca al revés.

La violación clásica es instanciar clases concretas de bajo nivel dentro del core.
Cada `new EmailNotificationService()` en un servicio de negocio es una cadena
que te ata a ese proveedor. Cuando el negocio cambia de proveedor — y siempre lo hace —
la cadena te arrastra a abrir código que no debería cambiar.

Constructor injection no es solo una convención de Spring. Es el mecanismo
que hace posible la inversión: quien construye el objeto decide la implementación.
El objeto no lo sabe, no le importa, y no necesita saberlo.

La insinuación de `Port`/`Adapter` en los nombres no es accidental: DIP es el
fundamento del que emerge la arquitectura hexagonal. Cuando tu dominio define
puertos y la infraestructura provee adaptadores, cambiar de proveedor es
añadir un adaptador nuevo. El core no se entera.
