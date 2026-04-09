package com.architecturedays.day009.service;

import com.architecturedays.day009.model.Reporte;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * BEFORE: Pide la conexion directamente del DataSource y NO la cierra.
 *
 * Cada llamada a listarTodos() deja una conexion huerfana en el pool.
 * Con maximum-pool-size: 5, a la sexta llamada el pool esta vacio
 * y el siguiente request explota con connection-timeout.
 *
 * Probalo: llama al endpoint 5 veces y la sexta se cuelga / falla.
 */
@Service
@Profile("before")
public class LeakyReporteService implements ReporteService {

    private final DataSource dataSource;

    public LeakyReporteService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Reporte> listarTodos() {
        System.out.println("BEFORE: Pidiendo conexion directa al DataSource...");
        List<Reporte> reportes = new ArrayList<>();

        try {
            // ERROR: pide la conexion pero NUNCA la cierra
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, nombre, total, fecha FROM reporte");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reportes.add(new Reporte(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("total"),
                        rs.getDate("fecha").toLocalDate()
                ));
            }

            // NO hay conn.close(). NO hay try-with-resources.
            // La conexion queda colgada. Leak confirmado.
            System.out.println("BEFORE: Retornando datos. Conexion NUNCA devuelta al pool.");

        } catch (SQLException e) {
            System.err.println("BEFORE: " + e.getMessage());
            throw new RuntimeException("Pool agotado o error de conexion", e);
        }

        return reportes;
    }
}
