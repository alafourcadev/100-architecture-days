package com.architecturedays.day008.controller;

import com.architecturedays.day008.service.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/{id}")
    public Map<String, Object> obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    @GetMapping
    public Map<String, Object> listar() {
        return productoService.listar();
    }

    @PutMapping("/{id}/precio")
    public Map<String, Object> actualizarPrecio(@PathVariable Long id,
                                                 @RequestParam BigDecimal precio) {
        return productoService.actualizar(id, precio);
    }
}
