package com.architecturedays.day007.repository;

import com.architecturedays.day007.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
