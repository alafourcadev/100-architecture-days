package dev.alafourca.day021.despues;

/**
 * Standalone collaborator: fraud detection logic.
 *
 * Processors that need fraud checking receive this via constructor.
 * Bank transfer processors, crypto processors, or internal transfers
 * that bypass fraud checks simply don't receive it — zero inheritance involved.
 */
public class FraudGuard {

    private final double maxAllowedAmount;

    public FraudGuard(double maxAllowedAmount) {
        this.maxAllowedAmount = maxAllowedAmount;
    }

    public void check(String accountId, double amount) {
        if (amount > maxAllowedAmount) {
            throw new IllegalStateException(
                    "Fraud check failed: amount " + amount + " exceeds threshold " + maxAllowedAmount
                    + " for account " + accountId);
        }
    }
}
