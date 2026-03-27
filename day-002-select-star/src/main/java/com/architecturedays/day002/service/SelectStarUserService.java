package com.architecturedays.day002.service;

import com.architecturedays.day002.model.User;
import com.architecturedays.day002.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("before")
public class SelectStarUserService implements UserService {

    private static final Logger log = LoggerFactory.getLogger(SelectStarUserService.class);
    private final UserRepository repository;
    private long lastPayloadSize = 0;

    public SelectStarUserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<?> findAllUsers() {
        log.warn("[BEFORE] Ejecutando SELECT * - trayendo TODAS las columnas incluyendo fotos");
        long start = System.currentTimeMillis();

        List<User> users = repository.findAll();

        lastPayloadSize = users.stream()
            .mapToLong(u -> u.getPhotoBase64() != null ? u.getPhotoBase64().length() : 0)
            .sum();

        log.warn("[BEFORE] {} usuarios cargados, payload estimado: {} MB, tiempo: {} ms",
            users.size(),
            lastPayloadSize / 1_000_000,
            System.currentTimeMillis() - start);

        return users;
    }

    @Override
    public long getPayloadSize() {
        return lastPayloadSize;
    }
}
