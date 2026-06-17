package dev.alafourca.day021.antes;

import java.util.UUID;

/**
 * Level 4 — Visa international card processor: the concrete leaf.
 *
 * This class inherits FROM THREE LEVELS:
 *   BasePaymentProcessor (audit log, processedCount)
 *   └─ CardPaymentProcessor (fraud check, 1.5% card fee)
 *      └─ InternationalCardProcessor (currency conversion, 2% FX fee)
 *         └─ VisaInternationalProcessor (Visa-specific routing)
 *
 * To add "Mastercard international" you duplicate this level.
 * To add "Visa domestic" you branch at level 2 — but you still inherit
 * InternationalCardProcessor's currency machinery you don't need.
 *
 * New requirement from product: "Visa Infinite cards get no card fee".
 * Options:
 *   a) Add a flag to CardPaymentProcessor and sprinkle ifs — inheritance becomes a config object.
 *   b) Create VisaInfiniteInternationalProcessor extends VisaInternationalProcessor — level 5.
 *   c) Override doProcess() in this class and duplicate the fraud check — copy-paste.
 * All three options are wrong. The hierarchy is a trap.
 */
public class VisaInternationalProcessor extends InternationalCardProcessor {

    @Override
    protected PaymentResult doChargeInternational(double amountUsd, String accountId) {
        String transactionId = "VISA-INTL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log("Visa international charge executed. Account: " + accountId + " Amount USD: " + amountUsd);
        return new PaymentResult(true, transactionId, amountUsd,
                "Visa international payment approved via Visa network");
    }
}
