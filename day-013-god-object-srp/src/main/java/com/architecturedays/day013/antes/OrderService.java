package com.architecturedays.day013.antes;

import java.util.List;

/**
 * El OrderService que sabia demasiado.
 *
 * 11 dependencias en el constructor. 50+ metodos. Una clase que cambia
 * cada vez que cambia cualquier cosa: el formato del email, el calculo
 * del descuento, el tipo de PDF, las metricas, la integracion con el
 * almacen... TODO pasa por aca.
 *
 * Sintoma: cada PR que toca esta clase rompe algo no relacionado.
 */
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailClient emailClient;
    private final PdfGenerator pdfGenerator;
    private final MetricsService metricsService;
    private final WarehouseClient warehouseClient;
    private final DiscountEngine discountEngine;
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;
    private final AuditLogger auditLogger;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            EmailClient emailClient,
            PdfGenerator pdfGenerator,
            MetricsService metricsService,
            WarehouseClient warehouseClient,
            DiscountEngine discountEngine,
            PaymentGateway paymentGateway,
            NotificationService notificationService,
            AuditLogger auditLogger) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.emailClient = emailClient;
        this.pdfGenerator = pdfGenerator;
        this.metricsService = metricsService;
        this.warehouseClient = warehouseClient;
        this.discountEngine = discountEngine;
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.auditLogger = auditLogger;
    }

    public Order createOrder(Long userId, List<Long> productIds) {
        throw new UnsupportedOperationException("god object: hace demasiado");
    }

    public Order updateOrder(Long orderId, List<Long> productIds) {
        throw new UnsupportedOperationException();
    }

    public void cancelOrder(Long orderId) {
        throw new UnsupportedOperationException();
    }

    public void processPayment(Long orderId) {
        throw new UnsupportedOperationException();
    }

    public void applyDiscount(Long orderId, String code) {
        throw new UnsupportedOperationException();
    }

    public byte[] generateInvoice(Long orderId) {
        throw new UnsupportedOperationException();
    }

    public void sendConfirmationEmail(Long orderId) {
        throw new UnsupportedOperationException();
    }

    public void notifyWarehouse(Long orderId) {
        throw new UnsupportedOperationException();
    }

    public void updateMetrics(Long orderId) {
        throw new UnsupportedOperationException();
    }

    public List<Order> findByUser(Long userId) {
        throw new UnsupportedOperationException();
    }

    public byte[] generateReport(Long userId) {
        throw new UnsupportedOperationException();
    }
}
