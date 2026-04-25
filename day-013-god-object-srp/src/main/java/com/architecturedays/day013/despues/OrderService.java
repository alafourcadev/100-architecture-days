package com.architecturedays.day013.despues;

import org.springframework.stereotype.Service;

/**
 * Una unica responsabilidad: crear ordenes.
 *
 * No conoce emails, ni PDFs, ni metricas, ni almacenes.
 * Valida, guarda y publica un evento. Eso es todo.
 *
 * Si manana cambia el flujo de notificacion, esta clase no se entera.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;
    private final OrderEventPublisher eventPublisher;

    public OrderService(
            OrderRepository orderRepository,
            OrderValidator orderValidator,
            OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.orderValidator = orderValidator;
        this.eventPublisher = eventPublisher;
    }

    public Order createOrder(Order order) {
        orderValidator.validate(order);
        Order saved = orderRepository.save(order);
        eventPublisher.publish(new OrderCreatedEvent(saved.id(), saved.userId(), saved.total()));
        return saved;
    }
}
