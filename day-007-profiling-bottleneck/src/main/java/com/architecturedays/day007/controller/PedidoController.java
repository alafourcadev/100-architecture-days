package com.architecturedays.day007.controller;

import com.architecturedays.day007.service.PedidoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/{id}")
    public Map<String, Object> obtenerPedido(@PathVariable Long id) {
        long inicio = System.currentTimeMillis();
        Map<String, Object> resultado = pedidoService.obtenerDetalle(id);
        long duracion = System.currentTimeMillis() - inicio;
        System.out.println("Endpoint /api/pedidos/" + id + " tardo " + duracion + "ms");
        return resultado;
    }
}
