package com.architecturedays.day008.repository;

import com.architecturedays.day008.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
