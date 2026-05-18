package com.architecturedays.day016.despues;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Despacha notificaciones delegando en el sender adecuado.
 *
 * Esta clase no cambia cuando llega un canal nuevo. Solo conoce la
 * abstraccion NotificationSender. El resto lo resuelve Spring al
 * inyectar todos los @Component que implementan esa interfaz.
 *
 * Cumple Open/Closed: abierta para extension (nueva implementacion),
 * cerrada para modificacion (esta clase no se toca).
 */
@Service
public class NotificationService {

    private final List<NotificationSender> senders;

    public NotificationService(List<NotificationSender> senders) {
        this.senders = senders;
    }

    public void send(NotificationRequest request) {
        senders.stream()
                .filter(sender -> sender.supports(request.getChannel()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown notification channel: " + request.getChannel()))
                .send(request);
    }
}
