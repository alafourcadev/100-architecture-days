package com.architecturedays.day008.config;

import com.architecturedays.day008.model.Producto;
import com.architecturedays.day008.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductoRepository repository;

    public DataLoader(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        repository.save(new Producto("MacBook Pro", new BigDecimal("2499.99"), 15, "Electronics"));
        repository.save(new Producto("iPhone 16", new BigDecimal("1199.99"), 50, "Electronics"));
        repository.save(new Producto("AirPods Pro", new BigDecimal("249.99"), 100, "Audio"));

        System.out.println("Datos iniciales: 3 productos cargados");
    }
}
