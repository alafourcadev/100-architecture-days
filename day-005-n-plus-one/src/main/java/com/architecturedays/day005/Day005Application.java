package com.architecturedays.day005;

import com.architecturedays.day005.model.Pedido;
import com.architecturedays.day005.model.Usuario;
import com.architecturedays.day005.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Day 005 — N+1 Queries: el bug que tu DBA ya sabe que tenés.
 *
 * Perfiles disponibles:
 *   -Dspring.profiles.active=before  → problema N+1 (51+ queries)
 *   -Dspring.profiles.active=after   → solución JOIN FETCH (1 query)
 *
 * Observá la salida SQL en la consola para ver la diferencia.
 */
@SpringBootApplication
public class Day005Application {

    public static void main(String[] args) {
        SpringApplication.run(Day005Application.class, args);
    }

    @Bean
    CommandLineRunner seedData(UsuarioRepository usuarioRepository) {
        return args -> {
            if (usuarioRepository.count() > 0) {
                System.out.println(">>> Datos existentes. Saltando seed.");
                return;
            }

            System.out.println(">>> Creando 50 usuarios con 5 pedidos cada uno...");

            var random = new Random(42);
            var categorias = List.of("Electrónica", "Ropa", "Hogar", "Deportes", "Libros");
            var descripciones = List.of(
                    "Notebook 15 pulgadas", "Remera algodón", "Lámpara escritorio",
                    "Pelota fútbol", "Clean Code", "Teclado mecánico",
                    "Jean slim fit", "Cafetera italiana", "Mancuernas 10kg",
                    "Design Patterns", "Monitor 27 4K", "Campera impermeable",
                    "Organizador cables", "Cinta correr", "Refactoring"
            );

            List<Usuario> usuarios = new ArrayList<>();

            for (int i = 1; i <= 50; i++) {
                var usuario = new Usuario(
                        "Usuario " + i,
                        "usuario" + i + "@example.com"
                );

                for (int j = 0; j < 5; j++) {
                    var monto = BigDecimal.valueOf(1000 + random.nextInt(49000), 2);
                    var descripcion = descripciones.get(random.nextInt(descripciones.size()));
                    var categoria = categorias.get(random.nextInt(categorias.size()));
                    var fecha = LocalDateTime.now().minusDays(random.nextInt(90));

                    usuario.agregarPedido(new Pedido(
                            categoria + " — " + descripcion,
                            monto,
                            fecha
                    ));
                }

                usuarios.add(usuario);
            }

            usuarioRepository.saveAll(usuarios);
            System.out.println(">>> Seed completado: 50 usuarios, 250 pedidos.");
            System.out.println(">>> Llamá a GET /api/usuarios y mirá el log SQL.");
        };
    }
}
