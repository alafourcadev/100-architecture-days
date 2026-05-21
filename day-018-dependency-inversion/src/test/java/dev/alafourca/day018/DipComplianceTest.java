package dev.alafourca.day018;

import dev.alafourca.day018.despues.EmailNotificationAdapter;
import dev.alafourca.day018.despues.NotificationPort;
import dev.alafourca.day018.despues.ReportService;
import dev.alafourca.day018.despues.SmsNotificationAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that verify DIP compliance in the "despues" design.
 *
 * Key properties under test:
 * - ReportService works with any NotificationPort implementation
 * - Swapping providers requires zero changes to ReportService
 * - Tests can inject a fake notifier — no infrastructure needed
 */
class DipComplianceTest {

    @Test
    @DisplayName("DESPUES: ReportService accepts any NotificationPort — email adapter works")
    void reportServiceWorksWithEmailAdapter() {
        NotificationPort emailAdapter = new EmailNotificationAdapter();
        ReportService service = new ReportService(emailAdapter);

        assertDoesNotThrow(() -> service.generateAndNotify("Q1-Sales", "cfo@company.com"));
    }

    @Test
    @DisplayName("DESPUES: ReportService accepts any NotificationPort — SMS adapter works without touching the service")
    void reportServiceWorksWithSmsAdapterWithoutModification() {
        // Swapping from email to SMS: one line changes at the composition root.
        // ReportService is identical. Its tests are identical. The core did not move.
        NotificationPort smsAdapter = new SmsNotificationAdapter();
        ReportService service = new ReportService(smsAdapter);

        assertDoesNotThrow(() -> service.generateAndNotify("Q1-Sales", "+595981000000"));
    }

    @Test
    @DisplayName("DESPUES: a fake NotificationPort can be injected for testing — no infrastructure required")
    void fakeAdapterCanBeInjectedForTesting() {
        List<String> captured = new ArrayList<>();

        // Inline fake — no Mockito, no Spring context, no network.
        // This is only possible because ReportService depends on the abstraction.
        NotificationPort fakePort = (recipient, subject, message) ->
                captured.add(recipient + "|" + subject + "|" + message);

        ReportService service = new ReportService(fakePort);
        service.generateAndNotify("Audit-2025", "auditor@company.com");

        assertEquals(1, captured.size());
        assertTrue(captured.getFirst().contains("auditor@company.com"));
        assertTrue(captured.getFirst().contains("Audit-2025"));
    }

    @Test
    @DisplayName("DESPUES: adding a new provider (push, Slack) does not touch ReportService or existing adapters")
    void newProviderRequiresOnlyNewClass() {
        // This test documents the extension guarantee.
        // A new push notification adapter is expressed as a lambda here
        // to simulate what a real PushNotificationAdapter class would be.
        //
        // Steps to add a new channel in production:
        //   1. Create PushNotificationAdapter implements NotificationPort
        //   2. Register it as a Spring @Component or configure it
        //   3. Done — ReportService, EmailNotificationAdapter, SmsNotificationAdapter
        //      and all their tests remain untouched.

        List<String> pushSent = new ArrayList<>();

        NotificationPort pushAdapter = (recipient, subject, message) -> {
            System.out.printf("[PUSH] To: %s | %s%n", recipient, subject);
            pushSent.add(recipient);
        };

        ReportService service = new ReportService(pushAdapter);
        service.generateAndNotify("Flash-Report", "device-token-abc123");

        assertEquals(1, pushSent.size());
        assertEquals("device-token-abc123", pushSent.getFirst());
    }

    @Test
    @DisplayName("DESPUES: ReportService.generateReport produces correct content regardless of notifier")
    void generateReportProducesCorrectContent() {
        NotificationPort noop = (r, s, m) -> {};
        ReportService service = new ReportService(noop);

        assertEquals("Report content for: Q4-Revenue", service.generateReport("Q4-Revenue"));
    }
}
