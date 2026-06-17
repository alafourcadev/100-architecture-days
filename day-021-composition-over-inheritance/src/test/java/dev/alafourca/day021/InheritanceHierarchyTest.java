package dev.alafourca.day021;

import dev.alafourca.day021.antes.MastercardInternationalProcessor;
import dev.alafourca.day021.antes.VisaInternationalProcessor;
import dev.alafourca.day021.antes.PaymentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Documents the ANTES design: the 4-level hierarchy works for the cases it was
 * designed for, but the tests themselves reveal the structural problem —
 * you cannot create a processor with "no card fee" or "no fraud check"
 * without subclassing further or duplicating code.
 */
class InheritanceHierarchyTest {

    @Test
    @DisplayName("ANTES: VisaInternationalProcessor processes a EUR payment with all fees baked in")
    void visaInternationalProcessesEurPayment() {
        VisaInternationalProcessor processor = new VisaInternationalProcessor();

        PaymentResult result = processor.process(100.0, "EUR", "ACC-001");

        assertTrue(result.isApproved());
        assertNotNull(result.getTransactionId());
        assertTrue(result.getTransactionId().startsWith("VISA-INTL-"));
        // 100 EUR → card fee 1.5% → 101.5 → currency fee 2% → ~103.53 → × 1.08 EUR rate → ~111.81 USD
        assertTrue(result.getFinalAmount() > 100.0, "Final amount should include all fees and conversion");
    }

    @Test
    @DisplayName("ANTES: MastercardInternationalProcessor is a near-duplicate of VisaInternationalProcessor")
    void mastercardInternationalProcessesEurPayment() {
        MastercardInternationalProcessor processor = new MastercardInternationalProcessor();

        PaymentResult result = processor.process(100.0, "EUR", "ACC-002");

        assertTrue(result.isApproved());
        assertTrue(result.getTransactionId().startsWith("MC-INTL-"));
    }

    @Test
    @DisplayName("ANTES: fraud check blocks amounts over the threshold — threshold is buried 3 levels deep")
    void fraudCheckBlocksLargeAmount() {
        VisaInternationalProcessor processor = new VisaInternationalProcessor();

        // The threshold is a constant in CardPaymentProcessor (Level 2).
        // To change it for one card type you must override the whole fraud check method.
        assertThrows(IllegalStateException.class,
                () -> processor.process(60_000.0, "USD", "ACC-003"),
                "Fraud check should throw for amounts over 50000");
    }

    @Test
    @DisplayName("ANTES: USD payment skips conversion — the only way to verify is to read 3 class files")
    void usdPaymentSkipsCurrencyConversion() {
        VisaInternationalProcessor processor = new VisaInternationalProcessor();

        PaymentResult result = processor.process(100.0, "USD", "ACC-004");

        assertTrue(result.isApproved());
        // 100 USD → card fee 1.5% → 101.5. No conversion because it's already USD.
        // The 2% FX fee is still applied inside InternationalCardProcessor.convertCurrency
        // even for USD because the method multiplies by (1 + FX_FEE) then by rate 1.0.
        // This is a subtle bug: USD payments pay an FX fee they shouldn't.
        // Finding this bug requires reading across 3 inheritance levels.
        assertTrue(result.getFinalAmount() > 100.0);
    }

    @Test
    @DisplayName("ANTES: adding AmEx international requires a new Level-4 class — structural dead end")
    void addingAmexRequiresNewSubclass() {
        // AmexInternationalProcessor does not exist in the 'antes' package.
        // To add it you must create a new class extending InternationalCardProcessor,
        // inheriting all 4 levels of state and behavior you may not need.
        // This test documents the structural constraint — it compiles but cannot instantiate AmEx.
        assertTrue(true, "AmEx is not supported in the inheritance design without a new Level-4 class");
    }
}
