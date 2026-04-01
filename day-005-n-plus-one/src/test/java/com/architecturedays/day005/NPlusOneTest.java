package com.architecturedays.day005;

import com.architecturedays.day005.dto.UsuarioConPedidosDto;
import com.architecturedays.day005.model.Usuario;
import com.architecturedays.day005.repository.UsuarioRepository;
import com.architecturedays.day005.service.UsuarioService;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests que demuestran el problema N+1 y su solución con JOIN FETCH.
 *
 * Usa Hibernate Statistics para contar las cargas lazy de colecciones:
 *   - findAll() + acceso lazy → collectionFetchCount = 50
 *   - findAllConPedidos() (JOIN FETCH) → collectionFetchCount = 0
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("after")
class NPlusOneTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EntityManager entityManager;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        var session = entityManager.unwrap(Session.class);
        statistics = session.getSessionFactory().getStatistics();
        statistics.setStatisticsEnabled(true);
    }

    // --- Tests del servicio (profile "after" = JOIN FETCH) ---

    @Test
    @DisplayName("El servicio devuelve 50 usuarios con sus pedidos")
    void servicioDevuelveTodosLosUsuarios() {
        List<UsuarioConPedidosDto> resultado = usuarioService.obtenerUsuariosConPedidos();

        assertThat(resultado).hasSize(50);
    }

    @Test
    @DisplayName("Cada usuario tiene exactamente 5 pedidos y monto > 0")
    void cadaUsuarioTieneCincoPedidos() {
        List<UsuarioConPedidosDto> resultado = usuarioService.obtenerUsuariosConPedidos();

        assertThat(resultado).allSatisfy(dto -> {
            assertThat(dto.cantidadPedidos()).isEqualTo(5);
            assertThat(dto.montoTotal()).isGreaterThan(BigDecimal.ZERO);
            assertThat(dto.nombre()).isNotBlank();
            assertThat(dto.email()).contains("@");
        });
    }

    // --- Tests de query count con Hibernate Statistics ---

    @Test
    @DisplayName("findAll() sin fetch genera N cargas lazy (problema N+1)")
    @Transactional
    void findAllSinFetchGeneraCargasLazy() {
        entityManager.clear();
        statistics.clear();

        List<Usuario> usuarios = usuarioRepository.findAll();

        // Forzar acceso a la colección lazy — dispara 1 SELECT por usuario
        usuarios.forEach(u -> u.getPedidos().size());

        assertThat(statistics.getCollectionFetchCount())
                .as("findAll() debe disparar N cargas lazy de pedidos")
                .isEqualTo(50);
        assertThat(usuarios).hasSize(50);
    }

    @Test
    @DisplayName("JOIN FETCH carga todo en 1 query — cero cargas lazy")
    @Transactional
    void joinFetchNoCargaColeccionesLazy() {
        entityManager.clear();
        statistics.clear();

        List<Usuario> usuarios = usuarioRepository.findAllConPedidos();

        // Acceder a pedidos NO dispara queries adicionales
        usuarios.forEach(u -> u.getPedidos().size());

        assertThat(statistics.getCollectionFetchCount())
                .as("JOIN FETCH no debe disparar cargas lazy")
                .isZero();
        assertThat(usuarios).hasSize(50);
    }

    @Test
    @DisplayName("@EntityGraph también resuelve N+1 — cero cargas lazy")
    @Transactional
    void entityGraphTambienResuelveElProblema() {
        entityManager.clear();
        statistics.clear();

        List<Usuario> usuarios = usuarioRepository.findAllConPedidosEntityGraph();

        usuarios.forEach(u -> u.getPedidos().size());

        assertThat(statistics.getCollectionFetchCount())
                .as("@EntityGraph no debe disparar cargas lazy")
                .isZero();
        assertThat(usuarios).hasSize(50);
    }

    @Test
    @DisplayName("JOIN FETCH trae los 250 pedidos correctamente (50 × 5)")
    @Transactional
    void joinFetchTraeTodosLosPedidos() {
        entityManager.clear();

        List<Usuario> usuarios = usuarioRepository.findAllConPedidos();

        long totalPedidos = usuarios.stream()
                .mapToLong(u -> u.getPedidos().size())
                .sum();

        assertThat(totalPedidos).isEqualTo(250);
    }
}
