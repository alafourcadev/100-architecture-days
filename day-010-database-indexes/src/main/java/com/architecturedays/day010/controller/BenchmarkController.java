package com.architecturedays.day010.controller;

import com.architecturedays.day010.service.BenchmarkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/benchmark")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    public BenchmarkController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    /**
     * Inserta la misma cantidad de registros en ambas tablas y mide el tiempo.
     * Probalo con distintos valores: 1000, 10000, 50000.
     * Cuanto mas registros, mas diferencia vas a ver.
     */
    @PostMapping("/insert")
    public Map<String, Object> benchmarkInsert(@RequestParam(defaultValue = "5000") int cantidad) {
        return benchmarkService.benchmarkInsert(cantidad);
    }

    /**
     * Compara el tiempo de SELECT entre las dos tablas.
     * Los resultados son casi identicos porque los 8 indices extra
     * en la tabla navideña no se usan en esta query.
     */
    @GetMapping("/select/{clienteId}")
    public Map<String, Object> benchmarkSelect(@PathVariable Long clienteId) {
        return benchmarkService.benchmarkSelect(clienteId);
    }

    @GetMapping("/estadisticas")
    public Map<String, Object> estadisticas() {
        return benchmarkService.estadisticas();
    }
}
