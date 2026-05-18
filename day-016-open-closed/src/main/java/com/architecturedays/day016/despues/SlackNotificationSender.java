package com.architecturedays.day016.despues;

import org.springframework.stereotype.Component;

/**
 * Canal SLACK agregado en sprint 7.
 * NotificationService no se toco. Ningun test existente se rompio.
 */
@Component
public class SlackNotificationSender implements NotificationSender {

    @Override
    public boolean supports(String channel) {
        return "SLACK".equals(channel);
    }

    @Override
    public void send(NotificationRequest request) {
        System.out.println("[SLACK] Channel: " + request.getRecipient()
                + " | Text: " + request.getMessage().trim());
    }
}
