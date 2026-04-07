package com.architecturedays.day007.repository;

import com.architecturedays.day007.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
