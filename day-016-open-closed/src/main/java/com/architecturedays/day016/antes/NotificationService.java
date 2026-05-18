package com.architecturedays.day016.antes;

import org.springframework.stereotype.Service;

/**
 * Servicio centralizado que despacha notificaciones segun el canal.
 *
 * PROBLEMA: cada vez que el negocio agrega un canal nuevo (PUSH, WHATSAPP,
 * SLACK...) alguien tiene que abrir este archivo, agregar un else-if, y
 * rezar para no romper los canales que ya andaban.
 *
 * Rompimos EMAIL cuando agregamos SMS. Rompimos SMS cuando agregamos PUSH.
 * El QA ya nos odia.
 *
 * Viola Open/Closed: la clase no esta cerrada para modificacion.
 */
@Service
public class NotificationService {

    public void send(NotificationRequest request) {
        String channel = request.getChannel();

        if (channel.equals("EMAIL")) {
            String body = "<html><body>" + request.getMessage() + "</body></html>";
            System.out.println("[EMAIL] To: " + request.getRecipient()
                    + " | Subject: Notification | Body: " + body);

        } else if (channel.equals("SMS")) {
            String truncated = request.getMessage().length() > 160
                    ? request.getMessage().substring(0, 160)
                    : request.getMessage();
            System.out.println("[SMS] To: " + request.getRecipient()
                    + " | Text: " + truncated);

        } else if (channel.equals("PUSH")) {
            // Agregado en el sprint 4. Rompio los tests de EMAIL porque alguien
            // toco la validacion de `channel` en la linea de arriba por error.
            System.out.println("[PUSH] Device: " + request.getRecipient()
                    + " | Payload: {\"body\":\"" + request.getMessage() + "\"}");

        } else if (channel.equals("SLACK")) {
            // Sprint 7. El if crece. Nadie recuerda por que hay un trim() solo aqui.
            String slackMessage = request.getMessage().trim();
            System.out.println("[SLACK] Channel: " + request.getRecipient()
                    + " | Text: " + slackMessage);

        } else if (channel.equals("WHATSAPP")) {
            // Sprint 11. El merge conflict de este archivo ya va en la tercera vez.
            System.out.println("[WHATSAPP] Phone: " + request.getRecipient()
                    + " | Message: " + request.getMessage());

        } else {
            throw new IllegalArgumentException("Unknown notification channel: " + channel);
        }
    }
}
