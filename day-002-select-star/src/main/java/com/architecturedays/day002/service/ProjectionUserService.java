package com.architecturedays.day002.service;

import com.architecturedays.day002.dto.UserSummary;
import com.architecturedays.day002.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("after")
public class ProjectionUserService implements UserService {

    private static final Logger log = LoggerFactory.getLogger(ProjectionUserService.class);
    private final UserRepository repository;
    private long lastPayloadSize = 0;

    public ProjectionUserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<?> findAllUsers() {
        log.info("[AFTER] Ejecutando Interface Projection - solo id, email, name");
        long start = System.currentTimeMillis();

        List<UserSummary> users = repository.findAllProjectedBy();

        lastPayloadSize = users.stream()
            .mapToLong(u -> (u.getEmail() + u.getName()).length() * 2L)
            .sum();
    
        log.info("[AFTER] {} usuarios cargados, payload estimado: {} KB, tiempo: {} ms",
            users.size(),
            lastPayloadSize / 1_000,
            System.currentTimeMillis() - start);

        return users;
    }

    @Override
    public long getPayloadSize() {
        return lastPayloadSize;
    }
}
