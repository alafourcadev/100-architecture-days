package com.architecturedays.day016.despues;

import org.springframework.stereotype.Component;

@Component
public class SmsNotificationSender implements NotificationSender {

    private static final int SMS_MAX_LENGTH = 160;

    @Override
    public boolean supports(String channel) {
        return "SMS".equals(channel);
    }

    @Override
    public void send(NotificationRequest request) {
        String truncated = request.getMessage().length() > SMS_MAX_LENGTH
                ? request.getMessage().substring(0, SMS_MAX_LENGTH)
                : request.getMessage();
        System.out.println("[SMS] To: " + request.getRecipient()
                + " | Text: " + truncated);
    }
}
