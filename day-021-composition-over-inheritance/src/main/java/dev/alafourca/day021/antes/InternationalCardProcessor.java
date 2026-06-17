package dev.alafourca.day021.antes;

/**
 * Level 3 — International card payments: adds currency conversion on top of card processing.
 *
 * Problem: currency conversion is now fused with card processing through inheritance.
 * A bank transfer that also needs currency conversion cannot reuse this logic
 * without inheriting ALL of CardPaymentProcessor too — including fraud check,
 * card fees, and the processedCount counter from the base.
 *
 * New requirement: "add a 0.5% cross-border fee for EU cards only"
 * — you have to override doProcess(), call super.doProcess(), but super already
 *   called chargeCard(). The call order is now a minefield.
 */
public abstract class InternationalCardProcessor extends CardPaymentProcessor {

    protected static final double CURRENCY_CONVERSION_FEE_PERCENT = 0.02; // 2%

    @Override
    protected PaymentResult chargeCard(double amount, String currency, String accountId) {
        double convertedAmount = convertCurrency(amount, currency);
        log("Currency converted to USD. Original: " + amount + " " + currency + " → " + convertedAmount + " USD");
        return doChargeInternational(convertedAmount, accountId);
    }

    protected double convertCurrency(double amount, String fromCurrency) {
        if ("USD".equals(fromCurrency)) {
            return amount;
        }
        // Simulated conversion: fee is always applied for non-USD
        double withConversionFee = amount * (1 + CURRENCY_CONVERSION_FEE_PERCENT);
        double conversionRate = getConversionRate(fromCurrency);
        return withConversionFee * conversionRate;
    }

    protected double getConversionRate(String fromCurrency) {
        // Hardcoded rates — in production this would call an FX service
        return switch (fromCurrency) {
            case "EUR" -> 1.08;
            case "GBP" -> 1.27;
            case "BRL" -> 0.20;
            default -> 1.0;
        };
    }

    protected abstract PaymentResult doChargeInternational(double amountUsd, String accountId);
}
