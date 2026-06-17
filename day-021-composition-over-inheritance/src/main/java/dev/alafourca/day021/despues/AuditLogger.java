package dev.alafourca.day021.despues;

/**
 * Standalone collaborator: audit logging.
 *
 * Not tied to any processor hierarchy. Passed as a dependency
 * to whoever needs it. Testable in isolation.
 */
public class AuditLogger {

    private int loggedEvents = 0;

    public void log(String processorName, String message) {
        loggedEvents++;
        System.out.println("[AUDIT] " + processorName + " | " + message);
    }

    public int getLoggedEvents() {
        return loggedEvents;
    }
}
