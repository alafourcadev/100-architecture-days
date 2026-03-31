package com.architecturedays.day004.dto;

import java.math.BigDecimal;

/**
 * Interface Projection para productos - solo campos necesarios
 */
public interface ProductSummary {
    Long getId();
    String getName();
    String getCategory();
    BigDecimal getPrice();
}
