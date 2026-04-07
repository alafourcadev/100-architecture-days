package com.architecturedays.day007.service;

import com.architecturedays.day007.model.Cliente;
import com.architecturedays.day007.model.Pedido;
import com.architecturedays.day007.model.Producto;
import com.architecturedays.day007.repository.ClienteRepository;
import com.architecturedays.day007.repository.PedidoRepository;
import com.architecturedays.day007.repository.ProductoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * BEFORE: Sin instrumentacion. No sabes donde esta el cuello de botella.
 * Solo ves que el endpoint tarda ~3.3 segundos y no tenes idea por que.
 */
@Service
@Profile("before")
public class BlindService implements PedidoService {

    private final PedidoRepository pedidoRepo;
    private final ClienteRepository clienteRepo;
    private final ProductoRepository productoRepo;

    public BlindService(PedidoRepository pedidoRepo,
                        ClienteRepository clienteRepo,
                        ProductoRepository productoRepo) {
        this.pedidoRepo = pedidoRepo;
        this.clienteRepo = clienteRepo;
        this.productoRepo = productoRepo;
    }

    @Override
    public Map<String, Object> obtenerDetalle(Long pedidoId) {
        System.out.println("--- BEFORE: Sin instrumentacion ---");

        // Buscar pedido
        Pedido pedido = pedidoRepo.findById(pedidoId).orElseThrow();

        // Buscar cliente
        Cliente cliente = clienteRepo.findById(pedido.getClienteId()).orElseThrow();

        // Buscar productos
        List<Long> ids = Arrays.stream(pedido.getProductoIds().split(","))
                .map(String::trim).map(Long::parseLong).toList();
        List<Producto> productos = productoRepo.findByIdIn(ids);

        // Calcular descuento - AQUI ESTA EL CUELLO DE BOTELLA
        // Simula una llamada a API externa que tarda ~3 segundos
        BigDecimal descuento = calcularDescuento(cliente, productos);

        BigDecimal subtotal = productos.stream()
                .map(Producto::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalFinal = subtotal.subtract(descuento);

        System.out.println("--- Respuesta enviada (no sabes que tardo 3.2s en descuento) ---");

        return Map.of(
                "pedidoId", pedido.getId(),
                "cliente", cliente.getNombre(),
                "productos", productos.stream().map(Producto::getNombre).toList(),
                "subtotal", subtotal,
                "descuento", descuento,
                "total", totalFinal
        );
    }

    private BigDecimal calcularDescuento(Cliente cliente, List<Producto> productos) {
        // Simula API externa lenta — este es el bottleneck oculto
        try {
            Thread.sleep(3200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if ("VIP".equals(cliente.getNivel())) {
            return productos.stream()
                    .map(p -> p.getPrecio().multiply(BigDecimal.valueOf(0.15)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }
}
