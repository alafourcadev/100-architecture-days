package com.architecturedays.day008.controller;

import com.architecturedays.day008.service.ProcessingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/procesamiento")
public class ProcessingController {

    private final ProcessingService service;

    public ProcessingController(ProcessingService service) {
        this.service = service;
    }

    @PostMapping("/iniciar")
    public Map<String, Object> iniciar() {
        return service.procesarTodos();
    }

    @GetMapping("/estado/{jobId}")
    public Map<String, Object> estado(@PathVariable String jobId) {
        return service.consultarEstado(jobId);
    }
}
