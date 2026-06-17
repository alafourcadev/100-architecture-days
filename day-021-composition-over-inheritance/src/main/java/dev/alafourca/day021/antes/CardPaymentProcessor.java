package dev.alafourca.day021.antes;

/**
 * Level 2 — Card payments: adds fraud check and card-specific fees.
 *
 * Problem: inherits BasePaymentProcessor.processedCount and all its logging
 * machinery, even if a subclass only needs fraud detection.
 * To add "card without fraud check", you'd need yet another branch.
 */
public abstract class CardPaymentProcessor extends BasePaymentProcessor {

    // Fee shared across all card types — but what if crypto cards have no fee?
    protected static final double CARD_PROCESSING_FEE_PERCENT = 0.015; // 1.5%

    @Override
    protected PaymentResult doProcess(double amount, String currency, String accountId) {
        runFraudCheck(accountId, amount);
        double amountWithFee = amount + (amount * CARD_PROCESSING_FEE_PERCENT);
        log("Card fee applied. New amount: " + amountWithFee);
        return chargeCard(amountWithFee, currency, accountId);
    }

    protected void runFraudCheck(String accountId, double amount) {
        // Simulated fraud check — in production this calls an external service
        if (amount > 50_000) {
            throw new IllegalStateException("Fraud check failed: amount exceeds threshold for account " + accountId);
        }
        log("Fraud check passed for account: " + accountId);
    }

    protected abstract PaymentResult chargeCard(double amount, String currency, String accountId);
}
