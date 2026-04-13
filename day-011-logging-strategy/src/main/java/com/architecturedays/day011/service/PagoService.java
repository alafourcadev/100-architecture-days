package com.architecturedays.day011.service;

import java.util.Map;

public interface PagoService {

    Map<String, Object> procesarPago(String clienteId, double monto);

    Map<String, Object> consultarPago(String txnId);
}
