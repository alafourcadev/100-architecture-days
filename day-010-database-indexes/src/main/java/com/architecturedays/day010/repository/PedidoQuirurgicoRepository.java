package com.architecturedays.day010.repository;

import com.architecturedays.day010.model.PedidoQuirurgico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PedidoQuirurgicoRepository extends JpaRepository<PedidoQuirurgico, Long> {

    List<PedidoQuirurgico> findByClienteIdAndFechaBetween(Long clienteId, LocalDate inicio, LocalDate fin);

    List<PedidoQuirurgico> findByEstado(String estado);
}
