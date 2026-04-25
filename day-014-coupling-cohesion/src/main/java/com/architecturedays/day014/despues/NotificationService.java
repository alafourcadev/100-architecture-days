package com.architecturedays.day014.despues;

import org.springframework.stereotype.Service;

/**
 * Recibe solo lo que necesita: nombre y email.
 * No conoce la password, ni el rol, ni las ordenes.
 */
@Service
public class NotificationService {

    public void sendWelcome(UserNotificationInfo user) {
        throw new UnsupportedOperationException();
    }
}
