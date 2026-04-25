package com.architecturedays.day013.despues;

public record OrderCreatedEvent(Long orderId, Long userId, double total) {
}
