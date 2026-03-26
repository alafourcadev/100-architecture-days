package com.architecturedays.day001.repository;

import com.architecturedays.day001.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
