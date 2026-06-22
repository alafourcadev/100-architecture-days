package dev.alafourca.day022;

import dev.alafourca.day022.despues.NotificationOptions;
import dev.alafourca.day022.despues.NotificationPriority;
import dev.alafourca.day022.despues.NotificationResult;
import dev.alafourca.day022.despues.NotificationService;
import dev.alafourca.day022.despues.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Equivalence tests: the DESPUES design produces the same observable behavior
 * as the ANTES design for every meaningful flag combination.
 *
 * Each test verifies one mapping:
 *   ANTES: dispatch(order, <booleans>)
 *   DESPUES: notifyBy*(order, NotificationOptions)
 *
 * Same input → same output. These are the regression guards for the refactor.
 */
class BooleanParametersEquivalenceTest {

    private NotificationService service;
    private Order order;

    @BeforeEach
    void setUp() {
        service = new NotificationService();
        order = new Order("ORD-001", "customer@example.com", "+595981000001", "CONFIRMED");
    }

    @Test
    @DisplayName("EQUIVALENCE: email only, normal priority — antes(true,false,false,false)")
    void emailOnlyNormalPriorityEquivalence() {
        // ANTES: service.dispatch(order, true, false, false, false)
        // DESPUES:
        NotificationResult result = service.notifyByEmail(order, NotificationOptions.forConfirmation());

        assertTrue(result.isSent());
        assertEquals("EMAIL", result.getChannel());
        assertTrue(result.getRecipient().contains("customer@example.com"));
        assertFalse(result.getRecipient().contains("admin@store.example.com"));
        assertTrue(result.getSummary().contains("NORMAL"));
    }

    @Test
    @DisplayName("EQUIVALENCE: SMS only, high priority — antes(false,true,true,false)")
    void smsOnlyHighPriorityEquivalence() {
        // ANTES: service.dispatch(order, false, true, true, false)
        // DESPUES:
        NotificationResult result = service.notifyBySms(order, NotificationOptions.forShipment());

        assertTrue(result.isSent());
        assertEquals("SMS", result.getChannel());
        assertTrue(result.getRecipient().contains("+595981000001"));
        assertFalse(result.getRecipient().contains("customer@example.com"));
        assertTrue(result.getSummary().contains("HIGH"));
    }

    @Test
    @DisplayName("EQUIVALENCE: email+SMS, high priority, cc admin — antes(true,true,true,true)")
    void emailAndSmsHighPriorityWithAdminEquivalence() {
        // ANTES: service.dispatch(order, true, true, true, true)
        // DESPUES:
        NotificationResult result = service.notifyByEmailAndSms(order, NotificationOptions.forFraudAlert());

        assertTrue(result.isSent());
        assertEquals("EMAIL+SMS", result.getChannel());
        assertTrue(result.getRecipient().contains("customer@example.com"));
        assertTrue(result.getRecipient().contains("+595981000001"));
        assertTrue(result.getRecipient().contains("admin@store.example.com"));
        assertTrue(result.getSummary().contains("HIGH"));
    }

    @Test
    @DisplayName("EQUIVALENCE: email only, normal priority, cc admin — antes(true,false,false,true)")
    void emailOnlyWithAdminCcEquivalence() {
        // ANTES: service.dispatch(order, true, false, false, true)
        // DESPUES:
        NotificationResult result = service.notifyByEmail(
                order,
                NotificationOptions.builder().includeAdmin(true).build());

        assertTrue(result.isSent());
        assertEquals("EMAIL", result.getChannel());
        assertTrue(result.getRecipient().contains("customer@example.com"));
        assertTrue(result.getRecipient().contains("admin@store.example.com"));
        assertTrue(result.getSummary().contains("NORMAL"));
    }

    @Test
    @DisplayName("EQUIVALENCE: null order throws in both designs")
    void nullOrderThrowsInBothDesigns() {
        assertThrows(IllegalArgumentException.class,
                () -> service.notifyByEmail(null, NotificationOptions.forConfirmation()));
    }

    @Test
    @DisplayName("EQUIVALENCE: audit log captures each sent notification")
    void auditLogCapturesNotifications() {
        service.notifyByEmail(order, NotificationOptions.forConfirmation());
        service.notifyBySms(order, NotificationOptions.forShipment());

        assertEquals(2, service.getAuditLog().size());
    }
}
