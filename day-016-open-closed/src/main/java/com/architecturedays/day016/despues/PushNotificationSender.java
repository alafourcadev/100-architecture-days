package com.architecturedays.day016.despues;

import org.springframework.stereotype.Component;

/**
 * Canal PUSH agregado en sprint 4.
 * NotificationService no se toco. Ningun test existente se rompio.
 */
@Component
public class PushNotificationSender implements NotificationSender {

    @Override
    public boolean supports(String channel) {
        return "PUSH".equals(channel);
    }

    @Override
    public void send(NotificationRequest request) {
        System.out.println("[PUSH] Device: " + request.getRecipient()
                + " | Payload: {\"body\":\"" + request.getMessage() + "\"}");
    }
}
