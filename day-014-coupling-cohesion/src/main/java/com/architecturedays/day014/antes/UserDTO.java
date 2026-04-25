package com.architecturedays.day014.antes;

import java.time.Instant;
import java.util.List;

/**
 * El UserDTO unico que viaja por todas las capas.
 *
 * El controller lo recibe del cliente. El service lo persiste.
 * El repositorio lo consulta. El servicio de notificaciones lo lee.
 * El servicio de reportes lo serializa. Todos lo conocen entero.
 *
 * Resultado: agregar un campo aqui afecta a 5 capas. La password
 * viaja a clases que no deberian verla. El reporte mensual depende
 * del formato del request HTTP.
 */
public record UserDTO(
        Long id,
        String name,
        String email,
        String password,
        String role,
        Instant createdAt,
        List<OrderDTO> orders,
        AddressDTO address,
        String creditCardLast4
) {
}
