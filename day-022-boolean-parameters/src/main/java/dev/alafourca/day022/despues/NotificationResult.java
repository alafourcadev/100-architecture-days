package dev.alafourca.day022.despues;

/**
 * Shared result type for the DESPUES design.
 */
public class NotificationResult {

    private final boolean sent;
    private final String channel;
    private final String recipient;
    private final String summary;

    public NotificationResult(boolean sent, String channel, String recipient, String summary) {
        this.sent = sent;
        this.channel = channel;
        this.recipient = recipient;
        this.summary = summary;
    }

    public boolean isSent() {
        return sent;
    }

    public String getChannel() {
        return channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return "NotificationResult{sent=" + sent + ", channel='" + channel + "', recipient='" + recipient + "', summary='" + summary + "'}";
    }
}
