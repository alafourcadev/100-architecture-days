package com.architecturedays.day016.despues;

/**
 * Punto de extension para canales de notificacion.
 *
 * Agregar un canal nuevo = implementar esta interfaz en una clase nueva.
 * NotificationService no cambia. Los canales existentes no se tocan.
 */
public interface NotificationSender {

    /**
     * Indica si este sender sabe manejar el canal dado.
     */
    boolean supports(String channel);

    /**
     * Envia la notificacion.
     */
    void send(NotificationRequest request);
}
