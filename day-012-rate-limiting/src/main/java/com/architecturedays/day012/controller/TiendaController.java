package com.architecturedays.day012.controller;

import com.architecturedays.day012.service.TiendaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/tienda")
public class TiendaController {

    private final TiendaService tiendaService;

    public TiendaController(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    @GetMapping("/productos")
    public Map<String, Object> listarProductos() {
        return tiendaService.listarProductos();
    }

    @PostMapping("/comprar/{productoId}")
    public Map<String, Object> comprar(@PathVariable Long productoId,
                                       @RequestParam(defaultValue = "anonymous") String cliente) {
        return tiendaService.comprar(productoId, cliente);
    }

    @GetMapping("/estadisticas")
    public Map<String, Object> estadisticas() {
        return tiendaService.estadisticas();
    }
}
