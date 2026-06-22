package dev.alafourca.day022.antes;

import java.util.ArrayList;
import java.util.List;

/**
 * ANTES: Notification service for an e-commerce order lifecycle.
 *
 * The problem: dispatch() takes four booleans. The call-site is completely opaque.
 * A reader has no idea what each true/false means without jumping to this definition.
 *
 * Real call-sites that exist in this codebase (see the tests):
 *
 *   service.dispatch(order, true,  false, true,  false);  // order confirmed, email, no priority, no cc
 *   service.dispatch(order, false, true,  true,  false);  // order shipped, SMS, priority, no cc
 *   service.dispatch(order, true,  false, false, true);   // order confirmed, email, no priority, cc admin
 *
 * The problems compound:
 *   1. Call-site is unreadable without opening this file.
 *   2. Booleans are positional — swap two and you get a silent wrong behavior.
 *   3. The method does at least three things: selects channel, sets priority, decides recipients.
 *   4. Not all combinations make sense: (false, false, *, *) sends to neither channel — silent no-op.
 *   5. Adding "send push notification" means a 5th boolean and every existing call-site changes.
 */
public class NotificationService {

    private final List<String> auditLog = new ArrayList<>();

    /**
     * Dispatches an order notification.
     *
     * @param order       the order that triggered this notification
     * @param useEmail    whether to send via email
     * @param useSms      whether to send via SMS
     * @param highPriority whether to mark as high priority (affects queue placement)
     * @param ccAdmin     whether to carbon-copy the admin address
     */
    public NotificationResult dispatch(Order order,
                                       boolean useEmail,
                                       boolean useSms,
                                       boolean highPriority,
                                       boolean ccAdmin) {
        if (order == null) {
            throw new IllegalArgumentException("order must not be null");
        }

        String priority = highPriority ? "HIGH" : "NORMAL";
        List<String> recipients = new ArrayList<>();

        if (useEmail) {
            recipients.add(order.getCustomerEmail());
        }
        if (useSms) {
            recipients.add(order.getCustomerPhone());
        }
        if (ccAdmin) {
            recipients.add("admin@store.example.com");
        }

        if (recipients.isEmpty()) {
            // Silent no-op: caller passed false, false — nobody gets notified
            return new NotificationResult(false, "NONE", "", "No channel selected");
        }

        String channel = (useEmail && useSms) ? "EMAIL+SMS" : (useEmail ? "EMAIL" : "SMS");
        String recipientList = String.join(", ", recipients);
        String message = buildMessage(order, priority);

        String entry = "[" + priority + "] " + channel + " -> " + recipientList + ": " + message;
        auditLog.add(entry);

        return new NotificationResult(true, channel, recipientList, entry);
    }

    private String buildMessage(Order order, String priority) {
        return "Order " + order.getId() + " (" + order.getStatus() + ") — priority: " + priority;
    }

    public List<String> getAuditLog() {
        return List.copyOf(auditLog);
    }
}
