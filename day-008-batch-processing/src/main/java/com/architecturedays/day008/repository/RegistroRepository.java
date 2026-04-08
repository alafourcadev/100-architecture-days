package com.architecturedays.day008.repository;

import com.architecturedays.day008.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

    List<Registro> findByProcesado(boolean procesado);

    long countByProcesado(boolean procesado);
}
