package com.architecturedays.day010.repository;

import com.architecturedays.day010.model.PedidoNavideno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PedidoNavidenoRepository extends JpaRepository<PedidoNavideno, Long> {

    List<PedidoNavideno> findByClienteIdAndFechaBetween(Long clienteId, LocalDate inicio, LocalDate fin);

    List<PedidoNavideno> findByEstado(String estado);
}
