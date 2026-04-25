package com.architecturedays.day013.despues;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Reacciona al evento OrderCreated y se ocupa del fulfillment.
 * Cambia cuando cambia la integracion con el almacen. Y solo entonces.
 */
@Service
public class OrderFulfillmentService {

    private final WarehouseClient warehouseClient;

    public OrderFulfillmentService(WarehouseClient warehouseClient) {
        this.warehouseClient = warehouseClient;
    }

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        warehouseClient.reserveStock(event.orderId());
    }
}
