package com.architecturedays.day014.despues;

import java.time.Instant;

public record UserResponse(Long id, String name, String email, String role, Instant createdAt) {
}
