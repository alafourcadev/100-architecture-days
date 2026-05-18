package com.architecturedays.day016.despues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests for NotificationService — verifies OCP compliance:
 * dispatch logic does not change when new channels are added.
 */
class NotificationServiceTest {

    private NotificationSender emailSender;
    private NotificationSender smsSender;
    private NotificationService service;

    @BeforeEach
    void setUp() {
        emailSender = mock(NotificationSender.class);
        smsSender   = mock(NotificationSender.class);

        when(emailSender.supports("EMAIL")).thenReturn(true);
        when(emailSender.supports(argThat(ch -> !"EMAIL".equals(ch)))).thenReturn(false);

        when(smsSender.supports("SMS")).thenReturn(true);
        when(smsSender.supports(argThat(ch -> !"SMS".equals(ch)))).thenReturn(false);

        service = new NotificationService(List.of(emailSender, smsSender));
    }

    @Test
    @DisplayName("Dispatches EMAIL request to the email sender")
    void dispatchesToEmailSender() {
        NotificationRequest request = new NotificationRequest("EMAIL", "user@example.com", "Hello");

        service.send(request);

        verify(emailSender).send(request);
        verify(smsSender, never()).send(any());
    }

    @Test
    @DisplayName("Dispatches SMS request to the sms sender")
    void dispatchesToSmsSender() {
        NotificationRequest request = new NotificationRequest("SMS", "+595981000000", "Hello");

        service.send(request);

        verify(smsSender).send(request);
        verify(emailSender, never()).send(any());
    }

    @Test
    @DisplayName("Adding a new sender handles its channel without touching NotificationService")
    void newSenderHandlesNewChannelWithoutModifyingService() {
        NotificationSender pushSender = mock(NotificationSender.class);
        when(pushSender.supports("PUSH")).thenReturn(true);
        when(pushSender.supports(argThat(ch -> !"PUSH".equals(ch)))).thenReturn(false);

        // Service is rebuilt with the extra sender — no code change inside NotificationService
        NotificationService serviceWithPush =
                new NotificationService(List.of(emailSender, smsSender, pushSender));

        NotificationRequest request = new NotificationRequest("PUSH", "device-token-abc", "Update available");

        serviceWithPush.send(request);

        verify(pushSender).send(request);
        verify(emailSender, never()).send(any());
        verify(smsSender, never()).send(any());
    }

    @Test
    @DisplayName("Throws IllegalArgumentException for unknown channel")
    void throwsForUnknownChannel() {
        NotificationRequest request = new NotificationRequest("TELEGRAM", "user123", "Hello");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.send(request),
                "Expected IllegalArgumentException for unregistered channel");
    }

    @Test
    @DisplayName("Throws when sender list is empty")
    void throwsWhenNoSendersRegistered() {
        NotificationService emptyService = new NotificationService(List.of());
        NotificationRequest request = new NotificationRequest("EMAIL", "user@example.com", "Hello");

        assertThrows(IllegalArgumentException.class, () -> emptyService.send(request));
    }
}
