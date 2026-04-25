package com.architecturedays.day013.despues;

public interface OrderEventPublisher {
    void publish(OrderCreatedEvent event);
}
