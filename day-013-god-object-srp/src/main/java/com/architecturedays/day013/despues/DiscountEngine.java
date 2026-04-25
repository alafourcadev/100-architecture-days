package com.architecturedays.day013.despues;

public interface DiscountEngine {
    double apply(double subtotal, String discountCode);
}
