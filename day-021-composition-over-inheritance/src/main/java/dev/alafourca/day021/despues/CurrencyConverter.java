package dev.alafourca.day021.despues;

/**
 * Standalone collaborator: converts an amount from a source currency to USD.
 *
 * Not a base class. Not mixed into a processor hierarchy.
 * Any processor that needs currency conversion receives this via constructor.
 * Any processor that does NOT need it simply doesn't receive it.
 */
public class CurrencyConverter {

    public double toUsd(double amount, String fromCurrency) {
        if ("USD".equals(fromCurrency)) {
            return amount;
        }
        return amount * getRate(fromCurrency);
    }

    private double getRate(String fromCurrency) {
        return switch (fromCurrency) {
            case "EUR" -> 1.08;
            case "GBP" -> 1.27;
            case "BRL" -> 0.20;
            default -> 1.0;
        };
    }
}
