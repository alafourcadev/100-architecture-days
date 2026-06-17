package dev.alafourca.day021.antes;

import java.util.UUID;

/**
 * Level 4 (duplicate branch) — Mastercard international.
 *
 * Identical structure to VisaInternationalProcessor. The only difference is
 * the transaction ID prefix and the network name. But to get here, this class
 * is forced to carry: audit logging, processedCount, fraud check, card fee,
 * and currency conversion — even though all that was already in the hierarchy.
 *
 * "Visa Infinite with no card fee" now needs Level 5. The tree keeps growing.
 */
public class MastercardInternationalProcessor extends InternationalCardProcessor {

    @Override
    protected PaymentResult doChargeInternational(double amountUsd, String accountId) {
        String transactionId = "MC-INTL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log("Mastercard international charge executed. Account: " + accountId + " Amount USD: " + amountUsd);
        return new PaymentResult(true, transactionId, amountUsd,
                "Mastercard international payment approved via Mastercard network");
    }
}
