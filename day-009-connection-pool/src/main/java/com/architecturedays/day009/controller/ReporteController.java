package com.architecturedays.day009.controller;

import com.architecturedays.day009.model.Reporte;
import com.architecturedays.day009.service.ReporteService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReporteController {

    private final ReporteService reporteService;
    private final DataSource dataSource;

    public ReporteController(ReporteService reporteService, DataSource dataSource) {
        this.reporteService = reporteService;
        this.dataSource = dataSource;
    }

    @GetMapping("/reportes")
    public List<Reporte> listarReportes() {
        return reporteService.listarTodos();
    }

    /**
     * Endpoint util para ver el estado del pool en tiempo real.
     * Probalo antes y despues de llamar a /api/reportes varias veces.
     */
    @GetMapping("/pool/estado")
    public Map<String, Object> estadoPool() {
        if (dataSource instanceof HikariDataSource hikari) {
            var pool = hikari.getHikariPoolMXBean();
            return Map.of(
                    "poolName", hikari.getPoolName(),
                    "totalConexiones", pool.getTotalConnections(),
                    "activas", pool.getActiveConnections(),
                    "idle", pool.getIdleConnections(),
                    "esperando", pool.getThreadsAwaitingConnection(),
                    "maximoConfigurado", hikari.getMaximumPoolSize()
            );
        }
        return Map.of("error", "No es un HikariDataSource");
    }
}
