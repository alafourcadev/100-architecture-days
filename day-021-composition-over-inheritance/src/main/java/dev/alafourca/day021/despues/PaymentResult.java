package dev.alafourca.day021.despues;

/**
 * Result of a payment processing operation.
 */
public class PaymentResult {

    private final boolean approved;
    private final String transactionId;
    private final double finalAmount;
    private final String message;

    public PaymentResult(boolean approved, String transactionId, double finalAmount, String message) {
        this.approved = approved;
        this.transactionId = transactionId;
        this.finalAmount = finalAmount;
        this.message = message;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PaymentResult{approved=" + approved
                + ", transactionId='" + transactionId + "'"
                + ", finalAmount=" + finalAmount
                + ", message='" + message + "'}";
    }
}
