package com.architecturedays.day001.service;

import com.architecturedays.day001.model.Product;
import com.architecturedays.day001.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Profile("after")
public class AsyncProductService implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(AsyncProductService.class);
    private final ProductRepository repository;

    public AsyncProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void initializeAsync() {
        log.info("[AFTER] Iniciando de forma ASINCRONA - NO bloquea el startup");
        long start = System.currentTimeMillis();

        try {
            log.info("[AFTER] Sync con proveedor API (background)...");
            Thread.sleep(5000);

            log.info("[AFTER] Validando warehouse (background)...");
            Thread.sleep(3000);

            log.info("[AFTER] Inicializando productos (background)...");
            if (repository.count() == 0) {
                for (int i = 1; i <= 10; i++) {
                    repository.save(new Product("Producto " + i, "Desc " + i,
                        new BigDecimal("99.99"), 50));
                }
            }
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("[AFTER] Inicializacion completa en {} ms - En background",
            System.currentTimeMillis() - start);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll();
    }
}
