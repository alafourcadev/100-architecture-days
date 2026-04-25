package com.architecturedays.day014.despues;

import org.springframework.stereotype.Service;

/**
 * Cohesion alta: agrupa metodos que cambian juntos
 * (formato del email, copy, plantillas).
 */
@Service
public class UserNotificationService {

    public void sendWelcomeEmail(UserNotificationInfo user) {
        throw new UnsupportedOperationException();
    }

    public void sendDeactivationEmail(UserNotificationInfo user) {
        throw new UnsupportedOperationException();
    }
}
