package com.architecturedays.day005.repository;

import com.architecturedays.day005.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * JOIN FETCH: trae usuarios y pedidos en una sola query.
     * El DISTINCT evita duplicados por el JOIN con la colección.
     */
    @Query("SELECT DISTINCT u FROM Usuario u JOIN FETCH u.pedidos")
    List<Usuario> findAllConPedidos();

    /**
     * Alternativa declarativa con @EntityGraph — mismo resultado.
     */
    @EntityGraph(attributePaths = {"pedidos"})
    @Query("SELECT u FROM Usuario u")
    List<Usuario> findAllConPedidosEntityGraph();
}
