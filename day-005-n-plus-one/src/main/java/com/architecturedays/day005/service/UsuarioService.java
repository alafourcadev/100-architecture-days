package com.architecturedays.day005.service;

import com.architecturedays.day005.dto.UsuarioConPedidosDto;

import java.util.List;

public interface UsuarioService {

    List<UsuarioConPedidosDto> obtenerUsuariosConPedidos();
}
