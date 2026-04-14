package com.architecturedays.day012.config;

/**
 * Implementacion manual del algoritmo Token Bucket.
 *
 * El concepto es simple:
 * - Tienes un balde con N tokens
 * - Cada request consume 1 token
 * - Los tokens se regeneran a velocidad constante
 * - Balde vacio = request rechazada
 *
 * Este algoritmo lo usan: AWS API Gateway, Stripe, GitHub API,
 * Cloudflare, nginx, y practicamente cualquier API publica.
 */
public class TokenBucket {

    private final long capacity;
    private final double refillRatePerMs;
    private double tokens;
    private long lastRefillTimestamp;

    public TokenBucket(long capacity, long refillPerMinute) {
        this.capacity = capacity;
        this.refillRatePerMs = refillPerMinute / 60000.0;
        this.tokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean tryConsume() {
        refill();
        if (tokens >= 1) {
            tokens--;
            return true;
        }
        return false;
    }

    public synchronized long getAvailableTokens() {
        refill();
        return (long) tokens;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTimestamp;
        double newTokens = elapsed * refillRatePerMs;
        tokens = Math.min(capacity, tokens + newTokens);
        lastRefillTimestamp = now;
    }
}
