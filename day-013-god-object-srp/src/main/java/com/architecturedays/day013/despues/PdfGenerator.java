package com.architecturedays.day013.despues;

public interface PdfGenerator {
    byte[] generateInvoice(Long orderId);
}
