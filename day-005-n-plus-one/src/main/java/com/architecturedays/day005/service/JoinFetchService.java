package com.architecturedays.day005.service;

import com.architecturedays.day005.dto.UsuarioConPedidosDto;
import com.architecturedays.day005.repository.UsuarioRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación optimizada: JOIN FETCH trae todo en una sola query.
 *
 * SELECT u.*, p.* FROM usuarios u LEFT JOIN pedidos p ON u.id = p.usuario_id
 *
 * Sin importar cuántos usuarios haya: siempre 1 query.
 */
@Service
@Profile("after")
public class JoinFetchService implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public JoinFetchService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioConPedidosDto> obtenerUsuariosConPedidos() {
        var usuarios = usuarioRepository.findAllConPedidos();

        return usuarios.stream()
                .map(u -> new UsuarioConPedidosDto(
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getPedidos().size(),
                        u.getPedidos().stream()
                                .map(p -> p.getMonto())
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .toList();
    }
}
