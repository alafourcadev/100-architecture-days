package dev.alafourca.day022;

import dev.alafourca.day022.despues.NotificationOptions;
import dev.alafourca.day022.despues.NotificationPriority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the NotificationOptions parameter object and its factory methods.
 * Each test verifies a single domain-named preset or builder option.
 */
class NotificationOptionsTest {

    @Test
    @DisplayName("forConfirmation() — normal priority, no admin cc")
    void forConfirmationDefaults() {
        NotificationOptions opts = NotificationOptions.forConfirmation();

        assertEquals(NotificationPriority.NORMAL, opts.getPriority());
        assertFalse(opts.isIncludeAdmin());
    }

    @Test
    @DisplayName("forShipment() — high priority, no admin cc")
    void forShipmentIsHighPriority() {
        NotificationOptions opts = NotificationOptions.forShipment();

        assertEquals(NotificationPriority.HIGH, opts.getPriority());
        assertFalse(opts.isIncludeAdmin());
    }

    @Test
    @DisplayName("forFraudAlert() — high priority, admin always included")
    void forFraudAlertIncludesAdmin() {
        NotificationOptions opts = NotificationOptions.forFraudAlert();

        assertEquals(NotificationPriority.HIGH, opts.getPriority());
        assertTrue(opts.isIncludeAdmin());
    }

    @Test
    @DisplayName("Builder can override priority independently")
    void builderOverridesPriority() {
        NotificationOptions opts = NotificationOptions.builder()
                .priority(NotificationPriority.HIGH)
                .build();

        assertEquals(NotificationPriority.HIGH, opts.getPriority());
        assertFalse(opts.isIncludeAdmin());
    }

    @Test
    @DisplayName("Builder can set includeAdmin without touching priority")
    void builderSetsIncludeAdminIndependently() {
        NotificationOptions opts = NotificationOptions.builder()
                .includeAdmin(true)
                .build();

        assertTrue(opts.isIncludeAdmin());
        assertEquals(NotificationPriority.NORMAL, opts.getPriority());
    }

    @Test
    @DisplayName("Builder with all options set produces correct object")
    void builderWithAllOptions() {
        NotificationOptions opts = NotificationOptions.builder()
                .priority(NotificationPriority.HIGH)
                .includeAdmin(true)
                .build();

        assertEquals(NotificationPriority.HIGH, opts.getPriority());
        assertTrue(opts.isIncludeAdmin());
    }
}
