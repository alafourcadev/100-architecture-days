package com.architecturedays.day004.repository;

import com.architecturedays.day004.dto.ProductSummary;
import com.architecturedays.day004.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Offset Pagination con Spring Data Pageable
     * Retorna Page con metadata (totalElements, totalPages, etc)
     */
    Page<ProductSummary> findAllProjectedBy(Pageable pageable);

    /**
     * Cursor Pagination - busca productos con ID mayor al cursor
     * Mas eficiente para grandes datasets
     */
    @Query("SELECT p FROM Product p WHERE p.id > :cursor ORDER BY p.id ASC")
    List<Product> findByCursorAfter(@Param("cursor") Long cursor, Pageable pageable);

    /**
     * Cursor Pagination - busca productos con ID menor al cursor (para ir atras)
     */
    @Query("SELECT p FROM Product p WHERE p.id < :cursor ORDER BY p.id DESC")
    List<Product> findByCursorBefore(@Param("cursor") Long cursor, Pageable pageable);

    /**
     * Primer pagina para cursor pagination
     */
    @Query("SELECT p FROM Product p ORDER BY p.id ASC")
    List<Product> findFirstPage(Pageable pageable);
}
