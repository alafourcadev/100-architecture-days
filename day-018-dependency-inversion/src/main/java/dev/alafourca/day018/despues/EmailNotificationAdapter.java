package dev.alafourca.day018.despues;

import org.springframework.stereotype.Component;

/**
 * Adaptador de email — implementación concreta del puerto de notificación.
 *
 * Módulo de bajo nivel que implementa la abstracción definida por el dominio.
 * En un sistema real conectaría a SendGrid, SES, o cualquier proveedor de email.
 *
 * ReportService no sabe que este adaptador existe. Solo conoce NotificationPort.
 * Spring inyecta esta implementación cuando el contexto arranca con @Component.
 */
@Component
public class EmailNotificationAdapter implements NotificationPort {

    @Override
    public void notify(String recipient, String subject, String message) {
        System.out.printf("[EMAIL] To: %s | Subject: %s | Body: %s%n",
                recipient, subject, message);
    }
}
