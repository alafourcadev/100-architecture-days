package com.architecturedays.day011.controller;

import com.architecturedays.day011.service.PagoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public Map<String, Object> procesarPago(@RequestParam String clienteId,
                                            @RequestParam double monto) {
        return pagoService.procesarPago(clienteId, monto);
    }

    @GetMapping("/{txnId}")
    public Map<String, Object> consultarPago(@PathVariable String txnId) {
        return pagoService.consultarPago(txnId);
    }
}
