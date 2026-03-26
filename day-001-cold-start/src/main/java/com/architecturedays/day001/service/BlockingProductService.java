package com.architecturedays.day001.service;

import com.architecturedays.day001.model.Product;
import com.architecturedays.day001.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Profile("before")
public class BlockingProductService implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(BlockingProductService.class);
    private final ProductRepository repository;

    public BlockingProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void initialize() {
        log.warn("[BEFORE] Iniciando de forma SINCRONA - BLOQUEA el startup");
        long start = System.currentTimeMillis();

        try {
            log.info("[BEFORE] Sync con proveedor API...");
            Thread.sleep(5000);

            log.info("[BEFORE] Validando warehouse...");
            Thread.sleep(3000);

            log.info("[BEFORE] Inicializando productos...");
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

        log.warn("[BEFORE] Inicializacion completa en {} ms - BLOQUEO todo este tiempo",
            System.currentTimeMillis() - start);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll();
    }
}
