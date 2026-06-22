package dev.alafourca.day022;

import dev.alafourca.day022.antes.NotificationResult;
import dev.alafourca.day022.antes.NotificationService;
import dev.alafourca.day022.antes.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that document the ANTES behavior.
 * Each test isolates a single boolean combination so failures are unambiguous.
 */
class BooleanParametersAntesBehaviorTest {

    private NotificationService service;
    private Order order;

    @BeforeEach
    void setUp() {
        service = new NotificationService();
        order = new Order("ORD-001", "customer@example.com", "+595981000001", "CONFIRMED");
    }

    @Test
    @DisplayName("ANTES: dispatch(order, true, false, false, false) — email only, normal priority")
    void emailOnlyNormalPriority() {
        // The call-site: what does each true/false mean? You have to open dispatch() to know.
        NotificationResult result = service.dispatch(order, true, false, false, false);

        assertTrue(result.isSent());
        assertEquals("EMAIL", result.getChannel());
        assertTrue(result.getRecipient().contains("customer@example.com"));
        assertFalse(result.getRecipient().contains("admin@store.example.com"));
        assertTrue(result.getSummary().contains("NORMAL"));
    }

    @Test
    @DisplayName("ANTES: dispatch(order, false, true, true, false) — SMS only, high priority")
    void smsOnlyHighPriority() {
        NotificationResult result = service.dispatch(order, false, true, true, false);

        assertTrue(result.isSent());
        assertEquals("SMS", result.getChannel());
        assertTrue(result.getRecipient().contains("+595981000001"));
        assertTrue(result.getSummary().contains("HIGH"));
    }

    @Test
    @DisplayName("ANTES: dispatch(order, true, true, true, false) — email+SMS, high priority")
    void emailAndSmsHighPriority() {
        NotificationResult result = service.dispatch(order, true, true, true, false);

        assertTrue(result.isSent());
        assertEquals("EMAIL+SMS", result.getChannel());
        assertTrue(result.getRecipient().contains("customer@example.com"));
        assertTrue(result.getRecipient().contains("+595981000001"));
        assertTrue(result.getSummary().contains("HIGH"));
    }

    @Test
    @DisplayName("ANTES: dispatch(order, true, false, false, true) — email only, normal priority, cc admin")
    void emailWithAdminCc() {
        NotificationResult result = service.dispatch(order, true, false, false, true);

        assertTrue(result.isSent());
        assertEquals("EMAIL", result.getChannel());
        assertTrue(result.getRecipient().contains("customer@example.com"));
        assertTrue(result.getRecipient().contains("admin@store.example.com"));
    }

    @Test
    @DisplayName("ANTES: dispatch(order, false, false, false, false) — silent no-op, nobody gets notified")
    void noChannelSelectedIsASilentNoOp() {
        // The bug hidden in the boolean signature: (false, false, false, false) is a valid
        // call that compiles and runs — but nobody receives anything.
        // The type system cannot prevent this useless combination.
        NotificationResult result = service.dispatch(order, false, false, false, false);

        assertFalse(result.isSent());
        assertEquals("NONE", result.getChannel());
    }

    @Test
    @DisplayName("ANTES: dispatch(order, false, false, true, true) — unexpected: admin receives even with no customer channel")
    void adminReceivesEvenWhenNoCustomerChannelSelected() {
        // Surprising behavior: ccAdmin=true adds the admin even when the customer gets nothing.
        // With booleans, this combination is invisible at the call-site and hard to prevent.
        NotificationResult result = service.dispatch(order, false, false, true, true);

        // The implementation sends to admin regardless — unintended behavior that booleans allow silently.
        assertTrue(result.isSent());
        assertTrue(result.getRecipient().contains("admin@store.example.com"));
        assertFalse(result.getRecipient().contains("customer@example.com"));
    }

    @Test
    @DisplayName("ANTES: null order throws IllegalArgumentException")
    void nullOrderThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.dispatch(null, true, false, false, false));
    }

    @Test
    @DisplayName("ANTES: audit log records each dispatched notification")
    void auditLogRecordsDispatched() {
        service.dispatch(order, true, false, false, false);
        service.dispatch(order, false, true, true, false);

        assertEquals(2, service.getAuditLog().size());
    }
}
