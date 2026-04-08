package com.architecturedays.day008.controller;

import com.architecturedays.day008.service.ProcessingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/importar")
public class ProcessingController {

    private final ProcessingService service;

    public ProcessingController(ProcessingService service) {
        this.service = service;
    }

    @PostMapping
    public Map<String, Object> importar(@RequestParam("archivo") MultipartFile archivo) {
        if (archivo.isEmpty()) {
            return Map.of("error", "El archivo esta vacio");
        }
        return service.procesar(archivo);
    }

    @GetMapping("/estado/{jobId}")
    public Map<String, Object> estado(@PathVariable String jobId) {
        return service.consultarEstado(jobId);
    }
}
