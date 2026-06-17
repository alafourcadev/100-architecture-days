package dev.alafourca.day021.despues;

/**
 * Factory that assembles processors from their parts.
 *
 * This is the composition root. The entire "hierarchy" that used to be 4 classes
 * is now a set of named factory methods. Adding a new combination is a new method,
 * not a new subclass.
 */
public class PaymentProcessorFactory {

    private static final double CARD_FEE_PERCENT = 0.015;       // 1.5%
    private static final double CURRENCY_FEE_PERCENT = 0.02;    // 2%
    private static final double FRAUD_THRESHOLD = 50_000.0;

    /** Replaces VisaInternationalProcessor (4-level hierarchy). */
    public static PaymentProcessor visaInternational(AuditLogger logger) {
        return new PaymentProcessor(
                CardNetworkGateway.visa(),
                FeeStrategy.percentage(CARD_FEE_PERCENT + CURRENCY_FEE_PERCENT),
                new CurrencyConverter(),
                new FraudGuard(FRAUD_THRESHOLD),
                logger);
    }

    /** Replaces MastercardInternationalProcessor (4-level hierarchy duplicate). */
    public static PaymentProcessor mastercardInternational(AuditLogger logger) {
        return new PaymentProcessor(
                CardNetworkGateway.mastercard(),
                FeeStrategy.percentage(CARD_FEE_PERCENT + CURRENCY_FEE_PERCENT),
                new CurrencyConverter(),
                new FraudGuard(FRAUD_THRESHOLD),
                logger);
    }

    /**
     * The new requirement that broke the hierarchy: Visa Infinite — no card fee.
     * In the inheritance design this needed Level 5 or copy-paste.
     * Here: one new factory method, zero new classes, zero existing code touched.
     */
    public static PaymentProcessor visaInfiniteInternational(AuditLogger logger) {
        return new PaymentProcessor(
                CardNetworkGateway.visa(),
                FeeStrategy.percentage(CURRENCY_FEE_PERCENT), // no card fee
                new CurrencyConverter(),
                new FraudGuard(FRAUD_THRESHOLD),
                logger);
    }

    /**
     * AmEx international — adding a third network in the old design required
     * another Level 4 class. Here: one factory method.
     */
    public static PaymentProcessor amexInternational(AuditLogger logger) {
        return new PaymentProcessor(
                CardNetworkGateway.amex(),
                FeeStrategy.percentage(CARD_FEE_PERCENT + CURRENCY_FEE_PERCENT),
                new CurrencyConverter(),
                new FraudGuard(FRAUD_THRESHOLD),
                logger);
    }

    /** Domestic card — no currency conversion needed. */
    public static PaymentProcessor visaDomestic(AuditLogger logger) {
        return new PaymentProcessor(
                CardNetworkGateway.visa(),
                FeeStrategy.percentage(CARD_FEE_PERCENT),
                null,   // no currency conversion
                new FraudGuard(FRAUD_THRESHOLD),
                logger);
    }
}
