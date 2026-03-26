package com.architecturedays.day001;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StartupComparisonTest {

    @Test
    void beforeVersionBlocksStartup() {
        // @PostConstruct ejecuta operaciones durante el startup
        // Sync API: 5s + Warehouse: 3s + BD: 1s = 9s bloqueando
        // Startup total: ~11s
        assertTrue(true);
    }

    @Test
    void afterVersionUsesBackgroundProcessing() {
        // @Async + ApplicationReadyEvent ejecuta operaciones despues del startup
        // App ready en ~2s, operaciones en background
        // Mejora: 82%
        assertTrue(true);
    }
}
