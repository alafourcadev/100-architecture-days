package com.architecturedays.day002;

import com.architecturedays.day002.model.User;
import com.architecturedays.day002.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Base64;

@SpringBootApplication
public class Day002Application {

    private static final Logger log = LoggerFactory.getLogger(Day002Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Day002Application.class, args);
    }

    @Bean
    CommandLineRunner initData(UserRepository repository) {
        return args -> {
            log.info("Inicializando datos de prueba...");

            for (int i = 1; i <= 50; i++) {
                byte[] fakeImage = new byte[500_000];
                java.util.Arrays.fill(fakeImage, (byte) i);
                String photoBase64 = Base64.getEncoder().encodeToString(fakeImage);

                User user = new User(
                    "usuario" + i + "@email.com",
                    "Usuario " + i,
                    photoBase64,
                    "Bio extensa del usuario " + i + " con mucho texto innecesario..."
                );
                repository.save(user);
            }

            log.info("50 usuarios creados, cada uno con foto de ~500KB en base64");
        };
    }
}
