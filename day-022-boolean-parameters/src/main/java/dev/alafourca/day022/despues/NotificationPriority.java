package dev.alafourca.day022.despues;

/**
 * Technique 1: Replace boolean with an enum that has domain meaning.
 *
 * Before: boolean highPriority
 * After:  NotificationPriority.HIGH / NotificationPriority.NORMAL
 *
 * The call-site now reads like a sentence. There is no ambiguity about
 * which boolean controls priority — the type enforces it.
 * Adding a CRITICAL tier in the future does not require changing signatures.
 */
public enum NotificationPriority {
    NORMAL,
    HIGH
}
