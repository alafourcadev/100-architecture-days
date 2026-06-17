package dev.alafourca.day021.despues;

import java.util.UUID;

/**
 * Standalone collaborator: routes the charge to the appropriate card network.
 *
 * "Visa vs Mastercard" is a routing decision, not a reason to have two classes
 * in a 4-level hierarchy. A single gateway parameterized by network name
 * covers all card networks. Adding AmEx = passing "AMEX" as the network name.
 */
public class CardNetworkGateway {

    private final String networkName;
    private final String transactionPrefix;

    public CardNetworkGateway(String networkName, String transactionPrefix) {
        this.networkName = networkName;
        this.transactionPrefix = transactionPrefix;
    }

    public PaymentResult charge(double amountUsd, String accountId) {
        String transactionId = transactionPrefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("[" + networkName + "] Charging account " + accountId + " for USD " + amountUsd);
        return new PaymentResult(true, transactionId, amountUsd,
                networkName + " payment approved");
    }

    public static CardNetworkGateway visa() {
        return new CardNetworkGateway("VISA", "VISA-INTL");
    }

    public static CardNetworkGateway mastercard() {
        return new CardNetworkGateway("MASTERCARD", "MC-INTL");
    }

    public static CardNetworkGateway amex() {
        return new CardNetworkGateway("AMEX", "AMEX-INTL");
    }
}
