package com.architecturedays.day008.service;

import com.architecturedays.day008.model.Registro;
import com.architecturedays.day008.repository.RegistroRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BEFORE: Lee todo el CSV en memoria, procesa uno por uno,
 * guarda uno por uno, bloquea el thread HTTP hasta terminar.
 *
 * Con 5.000 registros: ~5-8 segundos bloqueando.
 * Con 500.000: timeout del load balancer. OOM probable.
 */
@Service
@Profile("before")
public class SyncProcessingService implements ProcessingService {

    private final RegistroRepository repository;

    public SyncProcessingService(RegistroRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, Object> procesar(MultipartFile archivo) {
        long inicio = System.currentTimeMillis();
        System.out.println("BEFORE: Leyendo TODO el CSV en memoria...");

        List<Registro> registros = new ArrayList<>();
        int errores = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream()))) {

            String linea = reader.readLine(); // Saltar header

            while ((linea = reader.readLine()) != null) {
                String[] campos = linea.split(",");
                try {
                    String codigo = campos[0].trim();
                    String descripcion = campos[1].trim();
                    BigDecimal monto = new BigDecimal(campos[2].trim());

                    // Simula validacion y transformacion pesada
                    Thread.sleep(1);

                    monto = monto.multiply(BigDecimal.valueOf(1.21))
                            .setScale(2, RoundingMode.HALF_UP);

                    registros.add(new Registro(codigo, descripcion, monto));
                } catch (Exception e) {
                    errores++;
                }
            }
        } catch (Exception e) {
            return Map.of("error", "Fallo al leer el archivo: " + e.getMessage());
        }

        System.out.println("BEFORE: " + registros.size() + " registros en memoria. Guardando uno por uno...");

        // Guarda UNO POR UNO — el horror
        int guardados = 0;
        for (Registro r : registros) {
            r.setProcesado(true);
            repository.save(r);
            guardados++;
        }

        long duracion = System.currentTimeMillis() - inicio;
        System.out.println("BEFORE: Terminado en " + duracion + "ms");

        return Map.of(
                "procesados", guardados,
                "errores", errores,
                "duracionMs", duracion,
                "modo", "SINCRONO — el usuario espero " + (duracion / 1000) + " segundos",
                "problema", "Todo en memoria, save uno por uno, cero feedback"
        );
    }

    @Override
    public Map<String, Object> consultarEstado(String jobId) {
        return Map.of("error", "No hay tracking de jobs en modo sincrono");
    }
}
