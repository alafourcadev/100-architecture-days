package dev.alafourca.day018.antes;

/**
 * Servicio de alto nivel que genera reportes y notifica a los usuarios.
 *
 * DIP VIOLATION: ReportService instancia directamente EmailNotificationService.
 * El módulo de alto nivel (lógica de negocio) depende del módulo de bajo nivel
 * (proveedor de infraestructura). Cualquier cambio de proveedor — pasar a SMS,
 * push, o Slack — obliga a abrir esta clase y modificarla.
 *
 * El "cambiar de proveedor sin tocar el core" es imposible con este diseño.
 */
public class ReportService {

    // Dependencia hardcodeada hacia la clase concreta.
    // ReportService sabe QUÉ proveedor usa y CÓMO llamarlo.
    private final EmailNotificationService emailNotificationService;

    public ReportService() {
        // El módulo de alto nivel crea el módulo de bajo nivel.
        // Control acoplado, no invertido.
        this.emailNotificationService = new EmailNotificationService();
    }

    public void generateAndNotify(String reportName, String recipient) {
        String result = generateReport(reportName);

        // Para cambiar a SMS aquí hay que:
        // 1. Borrar estas dos líneas
        // 2. Agregar un SmsNotificationService como campo
        // 3. Cambiar el constructor
        // 4. Reescribir la llamada con la firma del método SMS
        // ... y todos los tests de ReportService se rompen durante la transición.
        emailNotificationService.sendEmail(
                recipient,
                "Report ready: " + reportName,
                result
        );
    }

    public String generateReport(String reportName) {
        return "Report content for: " + reportName;
    }
}
