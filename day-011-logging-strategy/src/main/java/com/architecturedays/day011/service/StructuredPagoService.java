package com.architecturedays.day011.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * AFTER: SLF4J con logging estructurado.
 *
 * Cada linea tiene: timestamp, nivel, thread, clase, datos clave-valor.
 * En 2 segundos sabes que fallo, cuando, y para quien.
 *
 * Los niveles permiten filtrar en produccion sin redeploy.
 * El formato permite buscar en Kibana/Datadog/CloudWatch.
 */
@Service
@Profile("after")
public class StructuredPagoService implements PagoService {

    private static final Logger log = LoggerFactory.getLogger(StructuredPagoService.class);

    @Override
    public Map<String, Object> procesarPago(String clienteId, double monto) {
        log.info("Procesando pago. clienteId={}, monto={}", clienteId, monto);

        try {
            Thread.sleep(100);

            if (monto > 9000) {
                throw new RuntimeException("Gateway timeout — servicio externo no responde");
            }

            String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
            log.info("Pago exitoso. txnId={}, clienteId={}, monto={}", txnId, clienteId, monto);

            return Map.of(
                    "status", "OK",
                    "txnId", txnId,
                    "clienteId", clienteId,
                    "monto", monto
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Fallo al procesar pago. clienteId={}, monto={}", clienteId, monto, e);
            return Map.of(
                    "status", "ERROR",
                    "error", e.getMessage(),
                    "clienteId", clienteId
            );
        }
    }

    @Override
    public Map<String, Object> consultarPago(String txnId) {
        log.debug("Consultando pago. txnId={}", txnId);
        return Map.of("txnId", txnId, "status", "NO_ENCONTRADO");
    }
}
