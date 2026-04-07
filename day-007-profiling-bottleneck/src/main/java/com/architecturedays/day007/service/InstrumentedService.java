package com.architecturedays.day007.service;

import com.architecturedays.day007.model.Cliente;
import com.architecturedays.day007.model.Pedido;
import com.architecturedays.day007.model.Producto;
import com.architecturedays.day007.repository.ClienteRepository;
import com.architecturedays.day007.repository.PedidoRepository;
import com.architecturedays.day007.repository.ProductoRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * AFTER: Con Micrometer. Cada operacion esta instrumentada.
 * Ahora ves exactamente donde estan los 3.2 segundos.
 */
@Service
@Profile("after")
public class InstrumentedService implements PedidoService {

    private final PedidoRepository pedidoRepo;
    private final ClienteRepository clienteRepo;
    private final ProductoRepository productoRepo;
    private final MeterRegistry registry;

    public InstrumentedService(PedidoRepository pedidoRepo,
                               ClienteRepository clienteRepo,
                               ProductoRepository productoRepo,
                               MeterRegistry registry) {
        this.pedidoRepo = pedidoRepo;
        this.clienteRepo = clienteRepo;
        this.productoRepo = productoRepo;
        this.registry = registry;
    }

    @Override
    public Map<String, Object> obtenerDetalle(Long pedidoId) {
        System.out.println("--- AFTER: Con instrumentacion Micrometer ---");

        // Cada operacion tiene su timer
        Pedido pedido = Timer.builder("pedido.buscar")
                .description("Tiempo de busqueda de pedido")
                .register(registry)
                .record(() -> pedidoRepo.findById(pedidoId).orElseThrow());

        Cliente cliente = Timer.builder("cliente.buscar")
                .description("Tiempo de busqueda de cliente")
                .register(registry)
                .record(() -> clienteRepo.findById(pedido.getClienteId()).orElseThrow());

        List<Long> ids = Arrays.stream(pedido.getProductoIds().split(","))
                .map(String::trim).map(Long::parseLong).toList();

        List<Producto> productos = Timer.builder("producto.buscarPorIds")
                .description("Tiempo de busqueda de productos")
                .register(registry)
                .record(() -> productoRepo.findByIdIn(ids));

        // Este timer va a mostrar el cuello de botella
        BigDecimal descuento = Timer.builder("descuento.calcular")
                .description("Tiempo de calculo de descuento")
                .register(registry)
                .record(() -> calcularDescuento(cliente, productos));

        BigDecimal subtotal = productos.stream()
                .map(Producto::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalFinal = subtotal.subtract(descuento);

        System.out.println("--- Consultá /actuator/metrics/descuento.calcular para ver el bottleneck ---");

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
        // Misma API externa lenta — pero ahora SABES que tarda 3.2s
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
