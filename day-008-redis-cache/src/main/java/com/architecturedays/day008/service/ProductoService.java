package com.architecturedays.day008.service;

import java.math.BigDecimal;
import java.util.Map;

public interface ProductoService {

    Map<String, Object> obtener(Long id);

    Map<String, Object> actualizar(Long id, BigDecimal nuevoPrecio);

    Map<String, Object> listar();
}
