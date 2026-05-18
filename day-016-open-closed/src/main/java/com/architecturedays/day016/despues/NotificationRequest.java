package com.architecturedays.day016.despues;

/**
 * Representa una solicitud de notificacion a un usuario.
 */
public class NotificationRequest {

    private final String channel;
    private final String recipient;
    private final String message;

    public NotificationRequest(String channel, String recipient, String message) {
        this.channel = channel;
        this.recipient = recipient;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }
}
