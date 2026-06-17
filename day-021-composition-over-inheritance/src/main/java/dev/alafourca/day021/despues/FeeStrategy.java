package dev.alafourca.day021.despues;

/**
 * Strategy: decides how much fee to add to a payment amount.
 *
 * Any fee rule is a new class that implements this interface.
 * Existing processors never change.
 */
@FunctionalInterface
public interface FeeStrategy {

    double apply(double amount);

    /** Convenience: no fee at all. */
    static FeeStrategy none() {
        return amount -> amount;
    }

    /** Fixed percentage fee. */
    static FeeStrategy percentage(double percent) {
        return amount -> amount * (1 + percent);
    }
}
