package dev.alafourca.day021.antes;

/**
 * Level 1 — Base class: common audit logging and basic validation.
 *
 * Problem: every specialization forces subclassing here, dragging
 * ALL inherited state and behavior regardless of need.
 */
public abstract class BasePaymentProcessor {

    // Subclasses inherit this — even when they don't need audit logging
    private int processedCount = 0;

    public PaymentResult process(double amount, String currency, String accountId) {
        log("Processing payment: amount=" + amount + " currency=" + currency);
        validateAmount(amount);
        PaymentResult result = doProcess(amount, currency, accountId);
        processedCount++;
        log("Done. Total processed: " + processedCount);
        return result;
    }

    protected abstract PaymentResult doProcess(double amount, String currency, String accountId);

    protected void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive, got: " + amount);
        }
    }

    protected void log(String message) {
        System.out.println("[AUDIT] " + getClass().getSimpleName() + " | " + message);
    }

    public int getProcessedCount() {
        return processedCount;
    }
}
