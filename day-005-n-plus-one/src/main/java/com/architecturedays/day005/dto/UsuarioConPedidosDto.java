package com.architecturedays.day005.dto;

import java.math.BigDecimal;

/**
 * DTO inmutable: info del usuario + resumen de sus pedidos.
 */
public record UsuarioConPedidosDto(
        Long id,
        String nombre,
        String email,
        int cantidadPedidos,
        BigDecimal montoTotal
) {
}
