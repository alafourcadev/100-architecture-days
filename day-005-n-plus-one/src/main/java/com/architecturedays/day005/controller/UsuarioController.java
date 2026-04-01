package com.architecturedays.day005.controller;

import com.architecturedays.day005.dto.UsuarioConPedidosDto;
import com.architecturedays.day005.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * GET /api/usuarios
     *
     * Con profile "before": genera N+1 queries.
     * Con profile "after": genera 1 sola query con JOIN FETCH.
     *
     * Activá show-sql y compará.
     */
    @GetMapping
    public ResponseEntity<List<UsuarioConPedidosDto>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerUsuariosConPedidos());
    }
}
