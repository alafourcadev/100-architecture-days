package dev.alafourca.day018;

import dev.alafourca.day018.antes.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests that document the DIP violation in the "antes" design.
 *
 * These tests verify behavior but also expose the design problem:
 * ReportService cannot be tested without EmailNotificationService.
 * There is no seam to substitute the notification channel.
 * To change provider, you must open ReportService itself.
 */
class DipViolationTest {

    @Test
    @DisplayName("ANTES: ReportService creates EmailNotificationService internally — no seam to replace it")
    void reportServiceCreatesConcreteDependencyInternally() {
        // No way to inject a fake or alternative notifier.
        // The concrete dependency is hardcoded inside the constructor.
        // If EmailNotificationService had a real network call, this test would hit production.
        ReportService service = new ReportService();

        // The business logic works — but the design is locked.
        String report = service.generateReport("Q1-Sales");

        assertEquals("Report content for: Q1-Sales", report);
    }

    @Test
    @DisplayName("ANTES: changing from email to SMS requires opening ReportService — the core module")
    void changingProviderRequiresModifyingHighLevelModule() {
        // This test documents the design problem, not a runtime failure.
        //
        // To switch ReportService from email to SMS you must:
        //   1. Remove the EmailNotificationService field
        //   2. Add an SmsNotificationService field
        //   3. Change the constructor
        //   4. Rewrite the generateAndNotify method
        //
        // Every test that covers generateAndNotify breaks during that refactor,
        // even though the business rule ("generate report and notify") never changed.
        //
        // High-level modules must not depend on low-level modules.
        // Both must depend on abstractions. — Robert C. Martin

        ReportService service = new ReportService();

        // We can only observe the side effect indirectly — no assertion on channel.
        // The test cannot verify WHICH channel was used without opening the class.
        String report = service.generateReport("Q4-Revenue");
        assertEquals("Report content for: Q4-Revenue", report);
    }

    @Test
    @DisplayName("ANTES: generateAndNotify executes without exception — business logic is not wrong, the design is")
    void generateAndNotifyExecutesWithoutException() {
        ReportService service = new ReportService();

        // No exception expected — the code runs.
        // The problem is architectural: the module is closed to extension but open to breakage.
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> service.generateAndNotify("Annual-Report", "ceo@company.com")
        );
    }
}
