package com.architecturedays.day007.config;

import com.architecturedays.day007.model.Cliente;
import com.architecturedays.day007.model.Pedido;
import com.architecturedays.day007.model.Producto;
import com.architecturedays.day007.repository.ClienteRepository;
import com.architecturedays.day007.repository.PedidoRepository;
import com.architecturedays.day007.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final ClienteRepository clienteRepo;
    private final ProductoRepository productoRepo;
    private final PedidoRepository pedidoRepo;

    public DataLoader(ClienteRepository clienteRepo,
                      ProductoRepository productoRepo,
                      PedidoRepository pedidoRepo) {
        this.clienteRepo = clienteRepo;
        this.productoRepo = productoRepo;
        this.pedidoRepo = pedidoRepo;
    }

    @Override
    public void run(String... args) {
        // Clientes
        clienteRepo.save(new Cliente("Maria Garcia", "maria@mail.com", "VIP"));
        clienteRepo.save(new Cliente("Juan Lopez", "juan@mail.com", "STANDARD"));

        // Productos
        productoRepo.save(new Producto("MacBook Pro", new BigDecimal("2499.99"), "Electronics"));
        productoRepo.save(new Producto("Monitor 4K", new BigDecimal("599.99"), "Electronics"));
        productoRepo.save(new Producto("Teclado Mecanico", new BigDecimal("149.99"), "Peripherals"));

        // Pedidos
        pedidoRepo.save(new Pedido(1L, "1,2,3", new BigDecimal("3249.97"), "CONFIRMADO"));
        pedidoRepo.save(new Pedido(2L, "1", new BigDecimal("2499.99"), "PENDIENTE"));

        System.out.println("Datos iniciales cargados: 2 clientes, 3 productos, 2 pedidos");
    }
}
