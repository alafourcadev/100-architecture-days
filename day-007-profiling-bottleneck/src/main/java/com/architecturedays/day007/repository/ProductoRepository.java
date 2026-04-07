package com.architecturedays.day007.repository;

import com.architecturedays.day007.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByIdIn(List<Long> ids);
}
