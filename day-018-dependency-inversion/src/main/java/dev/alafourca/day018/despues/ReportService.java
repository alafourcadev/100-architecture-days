package dev.alafourca.day018.despues;

/**
 * Servicio de alto nivel que genera reportes y notifica a los usuarios.
 *
 * DIP COMPLIANT: ReportService depende únicamente de NotificationPort (abstracción).
 * No sabe si la notificación va por email, SMS, push, o Slack.
 * No instancia ningún proveedor concreto. No importa ninguna clase de infraestructura.
 *
 * Cambiar de email a SMS = crear SmsNotificationAdapter e inyectarlo.
 * Esta clase no se toca. Sus tests no se rompen. El core no sabe que hubo un cambio.
 */
public class ReportService {

    // Depende de la abstracción — nunca de la implementación concreta.
    private final NotificationPort notificationPort;

    // La dependencia se inyecta desde afuera (constructor injection).
    // Spring, un test, o cualquier cliente puede pasar cualquier implementación.
    public ReportService(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    public void generateAndNotify(String reportName, String recipient) {
        String result = generateReport(reportName);

        // ReportService no sabe nada del transporte. Solo cumple el contrato.
        // Cambiar el proveedor es responsabilidad de quien configura el contexto.
        notificationPort.notify(
                recipient,
                "Report ready: " + reportName,
                result
        );
    }

    public String generateReport(String reportName) {
        return "Report content for: " + reportName;
    }
}
