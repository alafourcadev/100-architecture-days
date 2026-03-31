package com.architecturedays.day004.service;

import com.architecturedays.day004.dto.CursorPage;
import com.architecturedays.day004.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ProductService {

    /**
     * Obtiene productos - cada implementacion decide como
     */
    Object getProducts(int page, int size, Long cursor);

    /**
     * Estadisticas de la operacion
     */
    Map<String, Object> getStats();
}
