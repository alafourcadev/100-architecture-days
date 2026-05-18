package com.architecturedays.day016.despues;

import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public boolean supports(String channel) {
        return "EMAIL".equals(channel);
    }

    @Override
    public void send(NotificationRequest request) {
        String body = "<html><body>" + request.getMessage() + "</body></html>";
        System.out.println("[EMAIL] To: " + request.getRecipient()
                + " | Subject: Notification | Body: " + body);
    }
}
