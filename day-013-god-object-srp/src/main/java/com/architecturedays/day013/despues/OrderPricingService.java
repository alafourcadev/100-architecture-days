package com.architecturedays.day013.despues;

import org.springframework.stereotype.Service;

/**
 * Calcula totales aplicando descuentos.
 * Una sola razon para cambiar: cambia la logica de pricing.
 */
@Service
public class OrderPricingService {

    private final DiscountEngine discountEngine;

    public OrderPricingService(DiscountEngine discountEngine) {
        this.discountEngine = discountEngine;
    }

    public double calculateTotal(double subtotal, String discountCode) {
        return discountEngine.apply(subtotal, discountCode);
    }
}
