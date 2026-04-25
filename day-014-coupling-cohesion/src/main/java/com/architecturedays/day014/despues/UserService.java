package com.architecturedays.day014.despues;

import org.springframework.stereotype.Service;

/**
 * Cohesion alta: todo lo relacionado con el ciclo de vida del usuario,
 * en una sola clase. Sin emails, sin reportes.
 */
@Service
public class UserService {

    public UserResponse create(CreateUserRequest request) {
        throw new UnsupportedOperationException();
    }

    public UserResponse findById(Long id) {
        throw new UnsupportedOperationException();
    }

    public UserResponse update(Long id, CreateUserRequest request) {
        throw new UnsupportedOperationException();
    }

    public void deactivate(Long id) {
        throw new UnsupportedOperationException();
    }
}
