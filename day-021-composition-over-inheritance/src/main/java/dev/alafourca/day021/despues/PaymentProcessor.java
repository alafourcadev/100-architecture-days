package dev.alafourca.day021.despues;

/**
 * Single payment processor assembled by composition.
 *
 * All behaviors are injected:
 *   - FeeStrategy:        optional, defaults to no fee
 *   - CurrencyConverter:  optional, null means no conversion needed
 *   - FraudGuard:         optional, null means no fraud check needed
 *   - AuditLogger:        optional, null means no audit logging
 *   - CardNetworkGateway: required, the actual routing
 *
 * To build a "Visa international with fraud check":
 *   new PaymentProcessor(gateway, feeStrategy, converter, fraudGuard, logger)
 *
 * To build a "Visa Infinite with no card fee" (the requirement that broke the hierarchy):
 *   new PaymentProcessor(gateway, FeeStrategy.percentage(CURRENCY_FEE_PERCENT), converter, fraudGuard, logger)
 *
 * To build a "bank transfer with no fraud check and no conversion":
 *   new PaymentProcessor(bankGateway, FeeStrategy.none(), null, null, logger)
 *
 * Zero new classes. Zero changes to existing code.
 */
public class PaymentProcessor {

    private final CardNetworkGateway gateway;
    private final FeeStrategy feeStrategy;
    private final CurrencyConverter currencyConverter;
    private final FraudGuard fraudGuard;
    private final AuditLogger auditLogger;

    public PaymentProcessor(
            CardNetworkGateway gateway,
            FeeStrategy feeStrategy,
            CurrencyConverter currencyConverter,
            FraudGuard fraudGuard,
            AuditLogger auditLogger) {
        this.gateway = gateway;
        this.feeStrategy = feeStrategy != null ? feeStrategy : FeeStrategy.none();
        this.currencyConverter = currencyConverter;
        this.fraudGuard = fraudGuard;
        this.auditLogger = auditLogger;
    }

    public PaymentResult process(PaymentRequest request) {
        log("Processing payment: amount=" + request.getAmount() + " currency=" + request.getCurrency());

        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive, got: " + request.getAmount());
        }

        if (fraudGuard != null) {
            fraudGuard.check(request.getAccountId(), request.getAmount());
            log("Fraud check passed for account: " + request.getAccountId());
        }

        double amount = feeStrategy.apply(request.getAmount());
        log("After fee strategy: " + amount);

        if (currencyConverter != null) {
            amount = currencyConverter.toUsd(amount, request.getCurrency());
            log("After currency conversion to USD: " + amount);
        }

        PaymentResult result = gateway.charge(amount, request.getAccountId());
        log("Payment complete. Transaction: " + result.getTransactionId());
        return result;
    }

    private void log(String message) {
        if (auditLogger != null) {
            auditLogger.log(gateway.getClass().getSimpleName(), message);
        }
    }
}
