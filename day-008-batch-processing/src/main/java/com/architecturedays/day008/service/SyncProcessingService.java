package com.architecturedays.day008.service;

import com.architecturedays.day008.model.Registro;
import com.architecturedays.day008.repository.RegistroRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * BEFORE: Todo sincrono, todo en memoria, todo en un solo thread.
 *
 * Carga TODOS los registros en memoria, los procesa uno por uno,
 * guarda uno por uno, y bloquea el thread HTTP hasta terminar.
 *
 * Con 50.000 registros: ~15 segundos bloqueando.
 * Con 500.000: timeout del load balancer. OOM si hay poca memoria.
 */
@Service
@Profile("before")
public class SyncProcessingService implements ProcessingService {

    private final RegistroRepository repository;

    public SyncProcessingService(RegistroRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, Object> procesarTodos() {
        long inicio = System.currentTimeMillis();
        System.out.println("BEFORE: Cargando TODOS los registros en memoria...");

        // Carga todo en memoria de golpe
        List<Registro> registros = repository.findByProcesado(false);
        System.out.println("BEFORE: " + registros.size() + " registros en memoria");

        int procesados = 0;
        int errores = 0;

        // Procesa uno por uno, guarda uno por uno
        for (Registro r : registros) {
            try {
                // Simula procesamiento (validacion, transformacion, calculo)
                Thread.sleep(1); // 1ms por registro — parece poco, con 50K son 50 segundos
                r.setMonto(r.getMonto().multiply(BigDecimal.valueOf(1.21)).setScale(2, RoundingMode.HALF_UP));
                r.setProcesado(true);
                repository.save(r); // Un INSERT/UPDATE por registro
                procesados++;
            } catch (Exception e) {
                errores++;
            }
        }

        long duracion = System.currentTimeMillis() - inicio;
        System.out.println("BEFORE: Terminado en " + duracion + "ms — el usuario espero todo este tiempo");

        return Map.of(
                "procesados", procesados,
                "errores", errores,
                "duracionMs", duracion,
                "modo", "SINCRONO — el usuario espero " + duracion + "ms",
                "problema", "Todo en memoria, todo secuencial, cero feedback"
        );
    }

    @Override
    public Map<String, Object> consultarEstado(String jobId) {
        return Map.of("error", "No hay tracking de jobs en modo sincrono");
    }
}
