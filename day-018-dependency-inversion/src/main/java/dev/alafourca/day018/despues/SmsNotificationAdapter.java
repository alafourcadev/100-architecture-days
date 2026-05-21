package dev.alafourca.day018.despues;

/**
 * Adaptador de SMS — implementación alternativa del puerto de notificación.
 *
 * El negocio decide migrar de email a SMS. Lo que cambia: se crea esta clase.
 * Lo que NO cambia: ReportService, NotificationPort, los tests de negocio.
 *
 * Nota: no lleva @Component aquí para que el test pueda inyectarlo manualmente
 * sin activar detección de componentes de Spring en el paquete despues.
 * En un sistema real llevaría @Component y se elegiría el activo via @Primary
 * o @Qualifier según la configuración.
 */
public class SmsNotificationAdapter implements NotificationPort {

    @Override
    public void notify(String recipient, String subject, String message) {
        System.out.printf("[SMS] To: %s | %s: %s%n", recipient, subject, message);
    }
}
