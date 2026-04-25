package com.architecturedays.day013.despues;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Reacciona al evento OrderCreated y se ocupa de la comunicacion al cliente.
 * Cambia cuando cambia la logica de notificacion. Y solo entonces.
 */
@Service
public class OrderNotificationService {

    private final EmailClient emailClient;
    private final PdfGenerator pdfGenerator;

    public OrderNotificationService(EmailClient emailClient, PdfGenerator pdfGenerator) {
        this.emailClient = emailClient;
        this.pdfGenerator = pdfGenerator;
    }

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        byte[] invoice = pdfGenerator.generateInvoice(event.orderId());
        emailClient.send("user-" + event.userId() + "@example.com", "Tu orden", invoice);
    }
}
