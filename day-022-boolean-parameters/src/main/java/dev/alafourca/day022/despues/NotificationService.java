package dev.alafourca.day022.despues;

import java.util.ArrayList;
import java.util.List;

/**
 * DESPUES: Notification service refactored away from boolean parameters.
 *
 * Technique 1 — Split into methods with intent (boolean chose between two behaviors):
 *   notifyByEmail(order, options)
 *   notifyBySms(order, options)
 *   notifyByEmailAndSms(order, options)
 *
 * The caller selects the channel by choosing the METHOD NAME, not a boolean flag.
 * The call-site reads like a sentence: "notify by email" vs "notify by SMS".
 *
 * Technique 2 — Replace boolean with enum (NotificationPriority):
 *   NotificationPriority.HIGH instead of true
 *   NotificationPriority.NORMAL instead of false
 *
 * Technique 3 — Parameter object (NotificationOptions):
 *   Groups related options (priority + ccAdmin) so the call-site names each option
 *   and new options don't require changing every existing call.
 *
 * Call-sites that were opaque are now self-documenting:
 *
 *   // ANTES (what does each boolean mean?):
 *   service.dispatch(order, true, false, true, false);
 *
 *   // DESPUES (reads like a sentence):
 *   service.notifyByEmail(order, NotificationOptions.forConfirmation());
 *   service.notifyBySms(order, NotificationOptions.forShipment());
 *   service.notifyByEmailAndSms(order, NotificationOptions.forFraudAlert());
 */
public class NotificationService {

    private static final String ADMIN_ADDRESS = "admin@store.example.com";

    private final List<String> auditLog = new ArrayList<>();

    // --- Technique 1: methods named by intent, not a boolean flag ---

    /**
     * Notifies the customer by email only.
     * Channel selection is expressed through the method name, not a boolean.
     */
    public NotificationResult notifyByEmail(Order order, NotificationOptions options) {
        validateOrder(order);
        List<String> recipients = buildRecipients(order.getCustomerEmail(), options);
        return send("EMAIL", order, recipients, options.getPriority());
    }

    /**
     * Notifies the customer by SMS only.
     */
    public NotificationResult notifyBySms(Order order, NotificationOptions options) {
        validateOrder(order);
        List<String> recipients = buildRecipients(order.getCustomerPhone(), options);
        return send("SMS", order, recipients, options.getPriority());
    }

    /**
     * Notifies the customer through both email and SMS.
     * When both channels are needed, name it explicitly — not (true, true).
     */
    public NotificationResult notifyByEmailAndSms(Order order, NotificationOptions options) {
        validateOrder(order);
        List<String> recipients = new ArrayList<>();
        recipients.add(order.getCustomerEmail());
        recipients.add(order.getCustomerPhone());
        if (options.isIncludeAdmin()) {
            recipients.add(ADMIN_ADDRESS);
        }
        return send("EMAIL+SMS", order, recipients, options.getPriority());
    }

    // --- Internal helpers ---

    private List<String> buildRecipients(String primaryRecipient, NotificationOptions options) {
        List<String> recipients = new ArrayList<>();
        recipients.add(primaryRecipient);
        if (options.isIncludeAdmin()) {
            recipients.add(ADMIN_ADDRESS);
        }
        return recipients;
    }

    private NotificationResult send(String channel, Order order, List<String> recipients,
                                    NotificationPriority priority) {
        String recipientList = String.join(", ", recipients);
        String message = buildMessage(order, priority);
        String entry = "[" + priority.name() + "] " + channel + " -> " + recipientList + ": " + message;
        auditLog.add(entry);
        return new NotificationResult(true, channel, recipientList, entry);
    }

    private String buildMessage(Order order, NotificationPriority priority) {
        return "Order " + order.getId() + " (" + order.getStatus() + ") — priority: " + priority.name();
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("order must not be null");
        }
    }

    public List<String> getAuditLog() {
        return List.copyOf(auditLog);
    }
}
