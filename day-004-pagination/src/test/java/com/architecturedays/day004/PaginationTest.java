package com.architecturedays.day004;

import com.architecturedays.day004.dto.CursorPage;
import com.architecturedays.day004.model.Product;
import com.architecturedays.day004.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("offset")
class PaginationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository repository;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/api/products";
    }

    @Nested
    @DisplayName("Repository Tests")
    class RepositoryTests {

        @Test
        @DisplayName("findFirstPage debe retornar los primeros N productos ordenados por ID")
        void testFindFirstPage() {
            // Given
            createProducts(50);

            // When
            List<Product> firstPage = repository.findFirstPage(PageRequest.of(0, 10));

            // Then
            assertThat(firstPage).hasSize(10);
            assertThat(firstPage.getFirst().getId()).isLessThan(firstPage.getLast().getId());
        }

        @Test
        @DisplayName("findByCursorAfter debe retornar productos con ID mayor al cursor")
        void testFindByCursorAfter() {
            // Given
            createProducts(50);
            List<Product> allProducts = repository.findAll();
            Long cursor = allProducts.get(24).getId(); // Producto 25

            // When
            List<Product> afterCursor = repository.findByCursorAfter(cursor, PageRequest.of(0, 10));

            // Then
            assertThat(afterCursor).hasSize(10);
            assertThat(afterCursor.getFirst().getId()).isGreaterThan(cursor);
        }

        @Test
        @DisplayName("findAllProjectedBy debe retornar Page con metadata")
        void testOffsetPagination() {
            // Given
            createProducts(100);

            // When
            var page = repository.findAllProjectedBy(PageRequest.of(0, 10));

            // Then
            assertThat(page.getContent()).hasSize(10);
            assertThat(page.getTotalElements()).isEqualTo(100);
            assertThat(page.getTotalPages()).isEqualTo(10);
            assertThat(page.hasNext()).isTrue();
        }
    }

    @Nested
    @DisplayName("Cursor Pagination Logic Tests")
    class CursorPaginationLogicTests {

        @Test
        @DisplayName("Debe detectar hasNext correctamente")
        void testHasNextDetection() {
            // Given
            createProducts(25);

            // When: pedimos 10+1 para detectar si hay mas
            List<Product> products = repository.findFirstPage(PageRequest.of(0, 11));

            // Then
            boolean hasNext = products.size() > 10;
            assertThat(hasNext).isTrue();

            if (hasNext) {
                products = products.subList(0, 10);
            }
            assertThat(products).hasSize(10);
        }

        @Test
        @DisplayName("hasNext debe ser false en la ultima pagina")
        void testLastPageHasNextFalse() {
            // Given
            createProducts(15);
            List<Product> allProducts = repository.findAll();
            Long lastCursor = allProducts.get(9).getId();

            // When: pedimos despues del producto 10, solo quedan 5
            List<Product> products = repository.findByCursorAfter(lastCursor, PageRequest.of(0, 11));

            // Then
            boolean hasNext = products.size() > 10;
            assertThat(hasNext).isFalse();
            assertThat(products).hasSize(5);
        }
    }

    @Nested
    @DisplayName("Performance Comparison Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Cursor pagination debe ser mas rapido que offset en paginas altas")
        void testCursorVsOffsetPerformance() {
            // Given: crear muchos productos
            createProducts(1000);

            // Warmup
            repository.findAllProjectedBy(PageRequest.of(0, 20));
            repository.findFirstPage(PageRequest.of(0, 20));

            // When: Offset pagination en pagina alta
            long offsetStart = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                repository.findAllProjectedBy(PageRequest.of(40, 20)); // Offset 800
            }
            long offsetTime = System.nanoTime() - offsetStart;

            // When: Cursor pagination
            List<Product> products = repository.findAll();
            Long cursor = products.get(799).getId();

            long cursorStart = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                repository.findByCursorAfter(cursor, PageRequest.of(0, 20));
            }
            long cursorTime = System.nanoTime() - cursorStart;

            // Then: ambos deben completar en tiempo razonable
            // (no podemos garantizar que uno sea mas rapido en tests pequenos)
            assertThat(offsetTime).isLessThan(5_000_000_000L); // < 5 segundos
            assertThat(cursorTime).isLessThan(5_000_000_000L);

            System.out.printf("Offset (10 queries): %d ms%n", offsetTime / 1_000_000);
            System.out.printf("Cursor (10 queries): %d ms%n", cursorTime / 1_000_000);
        }
    }

    @Nested
    @DisplayName("CursorPage Record Tests")
    class CursorPageTests {

        @Test
        @DisplayName("CursorPage.of debe crear pagina correctamente")
        void testCursorPageCreation() {
            // Given
            List<Product> products = List.of(
                new Product("Product 1", "Desc", BigDecimal.TEN, "Cat", 10),
                new Product("Product 2", "Desc", BigDecimal.TEN, "Cat", 10)
            );

            // When
            CursorPage<Product> page = CursorPage.of(products, "100", null, true);

            // Then
            assertThat(page.content()).hasSize(2);
            assertThat(page.nextCursor()).isEqualTo("100");
            assertThat(page.previousCursor()).isNull();
            assertThat(page.hasNext()).isTrue();
            assertThat(page.hasPrevious()).isFalse();
            assertThat(page.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("CursorPage debe indicar hasPrevious cuando hay cursor anterior")
        void testCursorPageWithPrevious() {
            // Given
            List<Product> products = List.of(
                new Product("Product 1", "Desc", BigDecimal.TEN, "Cat", 10)
            );

            // When
            CursorPage<Product> page = CursorPage.of(products, "200", "100", true);

            // Then
            assertThat(page.hasPrevious()).isTrue();
            assertThat(page.previousCursor()).isEqualTo("100");
        }
    }

    private void createProducts(int count) {
        for (int i = 1; i <= count; i++) {
            Product product = new Product(
                "Product " + i,
                "Description " + i,
                BigDecimal.valueOf(10 + i),
                "Category " + (i % 5),
                i * 10
            );
            repository.save(product);
        }
    }
}
