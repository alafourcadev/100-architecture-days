package com.architecturedays.day005.service;

import com.architecturedays.day005.dto.UsuarioConPedidosDto;
import com.architecturedays.day005.repository.UsuarioRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación ingenua: findAll() + acceso lazy en un loop.
 *
 * Con 50 usuarios genera 51 queries (1 para usuarios + 50 para pedidos).
 * Si cada pedido tiene ítems, el número se multiplica.
 */
@Service
@Profile("before")
public class NPlusOneService implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public NPlusOneService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioConPedidosDto> obtenerUsuariosConPedidos() {
        // 1 SELECT para traer todos los usuarios
        var usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(u -> new UsuarioConPedidosDto(
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getPedidos().size(),     // dispara 1 SELECT por usuario
                        u.getPedidos().stream()
                                .map(p -> p.getMonto())
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .toList();
    }
}
