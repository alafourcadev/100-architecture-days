package dev.alafourca.day021;

import dev.alafourca.day021.despues.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that verify the DESPUES design: composable processors that cover
 * every combination without new classes.
 */
class CompositionTest {

    private AuditLogger logger;

    @BeforeEach
    void setUp() {
        logger = new AuditLogger();
    }

    @Test
    @DisplayName("DESPUES: Visa international processes a EUR payment correctly")
    void visaInternationalProcessesEurPayment() {
        PaymentProcessor processor = PaymentProcessorFactory.visaInternational(logger);
        PaymentRequest request = new PaymentRequest(100.0, "EUR", "ACC-001");

        PaymentResult result = processor.process(request);

        assertTrue(result.isApproved());
        assertTrue(result.getTransactionId().startsWith("VISA-INTL-"));
        assertTrue(result.getFinalAmount() > 100.0);
    }

    @Test
    @DisplayName("DESPUES: Mastercard international — zero new classes vs the inheritance version")
    void mastercardInternationalProcessesPayment() {
        PaymentProcessor processor = PaymentProcessorFactory.mastercardInternational(logger);
        PaymentRequest request = new PaymentRequest(200.0, "GBP", "ACC-002");

        PaymentResult result = processor.process(request);

        assertTrue(result.isApproved());
        assertTrue(result.getTransactionId().startsWith("MC-INTL-"));
    }

    @Test
    @DisplayName("DESPUES: Visa Infinite (no card fee) — the requirement that broke the hierarchy, solved with a factory method")
    void visaInfiniteHasNoCardFee() {
        AuditLogger infiniteLogger = new AuditLogger();
        AuditLogger standardLogger = new AuditLogger();

        PaymentProcessor infinite = PaymentProcessorFactory.visaInfiniteInternational(infiniteLogger);
        PaymentProcessor standard = PaymentProcessorFactory.visaInternational(standardLogger);

        PaymentRequest request = new PaymentRequest(100.0, "USD", "ACC-VIP");

        PaymentResult infiniteResult = infinite.process(request);
        PaymentResult standardResult = standard.process(request);

        // Visa Infinite pays only the FX fee (2%), standard pays card fee + FX fee (3.5%)
        assertTrue(infiniteResult.getFinalAmount() < standardResult.getFinalAmount(),
                "Visa Infinite should be cheaper than standard Visa — no card fee");
    }

    @Test
    @DisplayName("DESPUES: AmEx international — added with zero new classes")
    void amexInternationalAddsNoNewClasses() {
        PaymentProcessor amex = PaymentProcessorFactory.amexInternational(logger);
        PaymentRequest request = new PaymentRequest(500.0, "EUR", "ACC-AMEX");

        PaymentResult result = amex.process(request);

        assertTrue(result.isApproved());
        assertTrue(result.getTransactionId().startsWith("AMEX-INTL-"));
    }

    @Test
    @DisplayName("DESPUES: Visa domestic — no currency conversion injected, only card fee applied")
    void visaDomesticSkipsCurrencyConversion() {
        PaymentProcessor domestic = PaymentProcessorFactory.visaDomestic(logger);
        PaymentRequest request = new PaymentRequest(100.0, "USD", "ACC-DOM");

        PaymentResult result = domestic.process(request);

        assertTrue(result.isApproved());
        // Only card fee 1.5% applied. No FX fee. No conversion.
        assertEquals(101.5, result.getFinalAmount(), 0.001);
    }

    @Test
    @DisplayName("DESPUES: fraud guard blocks large amounts across all processors")
    void fraudGuardBlocksLargeAmounts() {
        PaymentProcessor processor = PaymentProcessorFactory.visaInternational(logger);
        PaymentRequest request = new PaymentRequest(60_000.0, "USD", "ACC-FRAUD");

        assertThrows(IllegalStateException.class, () -> processor.process(request));
    }

    @Test
    @DisplayName("DESPUES: processor without fraud guard is assembled without changing existing code")
    void processorWithoutFraudGuardIsComposable() {
        // No factory method for this — built directly to show composability
        PaymentProcessor noFraud = new PaymentProcessor(
                CardNetworkGateway.visa(),
                FeeStrategy.none(),
                null,
                null,   // no fraud guard
                logger);

        PaymentRequest request = new PaymentRequest(100_000.0, "USD", "ACC-INTERNAL");

        // Would fail fraud check in the inheritance design — here it passes because
        // fraud check is simply not composed in
        PaymentResult result = noFraud.process(request);
        assertTrue(result.isApproved());
    }

    @Test
    @DisplayName("DESPUES: FeeStrategy.none() makes amount pass through unchanged")
    void feeStrategyNonePassesThroughAmount() {
        FeeStrategy none = FeeStrategy.none();
        assertEquals(100.0, none.apply(100.0));
        assertEquals(0.0, none.apply(0.0));
    }

    @Test
    @DisplayName("DESPUES: FeeStrategy.percentage() applies the correct multiplier")
    void feeStrategyPercentageAppliesCorrectly() {
        FeeStrategy twoPercent = FeeStrategy.percentage(0.02);
        assertEquals(102.0, twoPercent.apply(100.0), 0.001);
        assertEquals(204.0, twoPercent.apply(200.0), 0.001);
    }

    @Test
    @DisplayName("DESPUES: CurrencyConverter converts EUR to USD correctly")
    void currencyConverterEurToUsd() {
        CurrencyConverter converter = new CurrencyConverter();
        assertEquals(108.0, converter.toUsd(100.0, "EUR"), 0.001);
        assertEquals(127.0, converter.toUsd(100.0, "GBP"), 0.001);
    }

    @Test
    @DisplayName("DESPUES: CurrencyConverter returns unchanged amount for USD")
    void currencyConverterUsdIsIdentity() {
        CurrencyConverter converter = new CurrencyConverter();
        assertEquals(100.0, converter.toUsd(100.0, "USD"), 0.001);
    }

    @Test
    @DisplayName("DESPUES: FraudGuard throws for amounts over the threshold")
    void fraudGuardThrowsOverThreshold() {
        FraudGuard guard = new FraudGuard(1000.0);
        assertThrows(IllegalStateException.class, () -> guard.check("ACC-X", 1001.0));
    }

    @Test
    @DisplayName("DESPUES: FraudGuard passes for amounts at or below the threshold")
    void fraudGuardPassesAtThreshold() {
        FraudGuard guard = new FraudGuard(1000.0);
        assertDoesNotThrow(() -> guard.check("ACC-X", 1000.0));
        assertDoesNotThrow(() -> guard.check("ACC-X", 999.99));
    }

    @Test
    @DisplayName("DESPUES: invalid amount is rejected at the processor level")
    void negativeAmountIsRejected() {
        PaymentProcessor processor = PaymentProcessorFactory.visaInternational(logger);
        PaymentRequest request = new PaymentRequest(-50.0, "USD", "ACC-NEG");

        assertThrows(IllegalArgumentException.class, () -> processor.process(request));
    }
}
