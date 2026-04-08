package com.architecturedays.day008.config;

import com.architecturedays.day008.model.Registro;
import com.architecturedays.day008.repository.RegistroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataGenerator implements CommandLineRunner {

    private static final int TOTAL_REGISTROS = 5000;
    private final RegistroRepository repository;

    public DataGenerator(RegistroRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        System.out.println("Generando " + TOTAL_REGISTROS + " registros de prueba...");

        List<Registro> batch = new ArrayList<>(500);
        for (int i = 1; i <= TOTAL_REGISTROS; i++) {
            batch.add(new Registro(
                    "REG-" + String.format("%06d", i),
                    "Registro de prueba #" + i,
                    BigDecimal.valueOf(Math.random() * 10000).setScale(2, BigDecimal.ROUND_HALF_UP)
            ));
            if (batch.size() >= 500) {
                repository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            repository.saveAll(batch);
        }

        System.out.println(TOTAL_REGISTROS + " registros generados. Listo para procesar.");
    }
}
