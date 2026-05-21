package dev.alafourca.day018.antes;

/**
 * Servicio concreto de notificaciones por email.
 *
 * Clase de bajo nivel que sabe cómo enviar un email.
 * En un sistema real conectaría a un proveedor como SendGrid o SES.
 *
 * DIP VIOLATION: ReportService depende directamente de esta clase concreta,
 * no de una abstracción. Cambiar de email a SMS obliga a abrir ReportService.
 */
public class EmailNotificationService {

    public void sendEmail(String recipient, String subject, String body) {
        System.out.printf("[EMAIL] To: %s | Subject: %s | Body: %s%n",
                recipient, subject, body);
    }
}
