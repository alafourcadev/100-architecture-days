package dev.alafourca.day021.despues;

/**
 * Value object carrying all the data a payment processor needs.
 * No inheritance required to extend with new fields.
 */
public class PaymentRequest {

    private final double amount;
    private final String currency;
    private final String accountId;

    public PaymentRequest(double amount, String currency, String accountId) {
        this.amount = amount;
        this.currency = currency;
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAccountId() {
        return accountId;
    }
}
