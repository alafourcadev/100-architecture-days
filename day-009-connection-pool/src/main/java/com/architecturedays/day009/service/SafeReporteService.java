package com.architecturedays.day009.service;

import com.architecturedays.day009.model.Reporte;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AFTER: Usa JdbcTemplate. Spring maneja el ciclo de vida de la conexion.
 *
 * La pide, la usa, la devuelve. Siempre. Incluso si hay excepciones.
 * No hay forma de leakear conexiones usando este patron.
 *
 * Llama al endpoint 1000 veces: el pool sigue igual de fresco.
 */
@Service
@Profile("after")
public class SafeReporteService implements ReporteService {

    private final JdbcTemplate jdbcTemplate;

    public SafeReporteService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Reporte> listarTodos() {
        System.out.println("AFTER: JdbcTemplate pide y devuelve la conexion solo");

        return jdbcTemplate.query(
                "SELECT id, nombre, total, fecha FROM reporte",
                (rs, rowNum) -> new Reporte(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("total"),
                        rs.getDate("fecha").toLocalDate()
                )
        );
    }
}
