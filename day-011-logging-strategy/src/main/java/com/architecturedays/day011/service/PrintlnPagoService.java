package com.architecturedays.day011.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * BEFORE: System.out.println para todo.
 *
 * Sin timestamp, sin nivel, sin thread, sin contexto.
 * Cuando hay 200 requests concurrentes, los logs se mezclan
 * y no puedes saber que linea pertenece a que request.
 *
 * Es debugging de estudiante en un sistema de produccion.
 */
@Service
@Profile("before")
public class PrintlnPagoService implements PagoService {

    @Override
    public Map<String, Object> procesarPago(String clienteId, double monto) {
        System.out.println("Procesando pago...");
        System.out.println("Monto: " + monto);
        System.out.println("Cliente: " + clienteId);

        try {
            // Simula procesamiento
            Thread.sleep(100);

            if (monto > 9000) {
                throw new RuntimeException("Gateway timeout — servicio externo no responde");
            }

            String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
            System.out.println("Pago OK: " + txnId);

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
            System.out.println("Error en pago: " + e.getMessage());
            return Map.of(
                    "status", "ERROR",
                    "error", e.getMessage()
            );
        }
    }

    @Override
    public Map<String, Object> consultarPago(String txnId) {
        System.out.println("Consultando pago: " + txnId);
        return Map.of("txnId", txnId, "status", "NO_ENCONTRADO");
    }
}
