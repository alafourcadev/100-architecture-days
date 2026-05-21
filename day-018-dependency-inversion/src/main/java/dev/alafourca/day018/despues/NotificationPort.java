package dev.alafourca.day018.despues;

/**
 * Puerto de notificación — la abstracción de la que depende el core.
 *
 * Tanto el módulo de alto nivel (ReportService) como los módulos de bajo nivel
 * (EmailNotificationAdapter, SmsNotificationAdapter) dependen de esta interfaz.
 * Ninguno de los dos depende del otro directamente. Eso es Dependency Inversion.
 *
 * El nombre "Port" insinúa arquitectura hexagonal: este es el contrato que el
 * dominio define. Los adaptadores lo implementan desde afuera.
 */
public interface NotificationPort {

    void notify(String recipient, String subject, String message);
}
