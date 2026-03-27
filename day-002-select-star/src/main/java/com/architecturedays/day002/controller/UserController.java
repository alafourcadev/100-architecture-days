package com.architecturedays.day002.controller;

import com.architecturedays.day002.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<?>> getUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        userService.findAllUsers();
        return ResponseEntity.ok(Map.of(
            "payloadBytes", userService.getPayloadSize(),
            "payloadKB", userService.getPayloadSize() / 1000,
            "payloadMB", userService.getPayloadSize() / 1_000_000
        ));
    }
}
