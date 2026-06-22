package dev.alafourca.day022.despues;

/**
 * Technique 2: Parameter object — groups related options so they travel together
 * and can be built with a readable builder.
 *
 * Before: dispatch(order, true, false, true, false)
 * After:  dispatch(order, NotificationOptions.forConfirmation())
 *
 * Benefits over raw booleans:
 *   - Each option is named at the call-site.
 *   - Adding a new option (e.g. includePdf) does not change existing call-sites.
 *   - Invalid combinations can be caught in the builder, not scattered in callers.
 *   - The factory methods encode domain intent: forConfirmation() vs forShipment().
 */
public class NotificationOptions {

    private final boolean includeAdmin;
    private final NotificationPriority priority;

    private NotificationOptions(Builder builder) {
        this.includeAdmin = builder.includeAdmin;
        this.priority = builder.priority;
    }

    public boolean isIncludeAdmin() {
        return includeAdmin;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    // --- Domain-named factory methods (Technique 1 + 2 combined) ---

    /** Standard order confirmation: normal priority, customer only. */
    public static NotificationOptions forConfirmation() {
        return new Builder().build();
    }

    /** Shipment notification: high priority, customer only. */
    public static NotificationOptions forShipment() {
        return new Builder().priority(NotificationPriority.HIGH).build();
    }

    /** Fraud alert: high priority, always cc admin. */
    public static NotificationOptions forFraudAlert() {
        return new Builder().priority(NotificationPriority.HIGH).includeAdmin(true).build();
    }

    // --- Builder for call-sites that need custom combinations ---

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean includeAdmin = false;
        private NotificationPriority priority = NotificationPriority.NORMAL;

        public Builder includeAdmin(boolean value) {
            this.includeAdmin = value;
            return this;
        }

        public Builder priority(NotificationPriority value) {
            this.priority = value;
            return this;
        }

        public NotificationOptions build() {
            return new NotificationOptions(this);
        }
    }
}
