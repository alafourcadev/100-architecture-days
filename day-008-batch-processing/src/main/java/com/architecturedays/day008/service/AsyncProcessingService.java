package com.architecturedays.day008.service;

import com.architecturedays.day008.model.JobStatus;
import com.architecturedays.day008.model.Registro;
import com.architecturedays.day008.repository.JobStatusRepository;
import com.architecturedays.day008.repository.RegistroRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AFTER: Recibe, responde inmediatamente, procesa en background por batches.
 *
 * 1. Responde al usuario en < 100ms con un jobId
 * 2. Procesa en background en batches de 500
 * 3. Actualiza progreso para que el usuario pueda consultar
 * 4. Si falla un registro, sigue con el resto
 */
@Service
@Profile("after")
public class AsyncProcessingService implements ProcessingService {

    private static final int BATCH_SIZE = 500;

    private final RegistroRepository registroRepo;
    private final JobStatusRepository jobRepo;

    public AsyncProcessingService(RegistroRepository registroRepo,
                                  JobStatusRepository jobRepo) {
        this.registroRepo = registroRepo;
        this.jobRepo = jobRepo;
    }

    @Override
    public Map<String, Object> procesarTodos() {
        String jobId = UUID.randomUUID().toString().substring(0, 8);
        long total = registroRepo.countByProcesado(false);

        // Crear job y responder inmediatamente
        JobStatus job = new JobStatus(jobId, (int) total);
        jobRepo.save(job);

        // Lanzar procesamiento en background
        procesarEnBackground(jobId);

        System.out.println("AFTER: Job " + jobId + " creado. Respuesta al usuario en < 100ms");

        return Map.of(
                "jobId", jobId,
                "estado", "PENDIENTE",
                "totalRegistros", total,
                "urlEstado", "/api/procesamiento/estado/" + jobId,
                "mensaje", "Procesamiento iniciado. Consulta el estado en la URL."
        );
    }

    @Async("batchExecutor")
    public void procesarEnBackground(String jobId) {
        JobStatus job = jobRepo.findById(jobId).orElseThrow();
        job.setEstado("EN_PROGRESO");
        jobRepo.save(job);

        System.out.println("AFTER: Procesando en background — batches de " + BATCH_SIZE);

        List<Registro> pendientes = registroRepo.findByProcesado(false);
        int procesados = 0;
        int errores = 0;

        // Procesar en batches
        List<Registro> batch = new ArrayList<>(BATCH_SIZE);
        for (Registro r : pendientes) {
            try {
                Thread.sleep(1); // Misma simulacion que BEFORE
                r.setMonto(r.getMonto().multiply(BigDecimal.valueOf(1.21)).setScale(2, RoundingMode.HALF_UP));
                r.setProcesado(true);
                batch.add(r);
                procesados++;
            } catch (Exception e) {
                errores++;
            }

            // Guardar en batch cuando llegamos al tamano
            if (batch.size() >= BATCH_SIZE) {
                registroRepo.saveAll(batch); // UN saveAll cada 500 registros
                batch.clear();

                // Actualizar progreso
                job.setProcesados(procesados);
                job.setErrores(errores);
                jobRepo.save(job);
                System.out.println("AFTER: Batch guardado — " + procesados + "/" + job.getTotalRegistros()
                        + " (" + job.getPorcentaje() + "%)");
            }
        }

        // Guardar ultimo batch parcial
        if (!batch.isEmpty()) {
            registroRepo.saveAll(batch);
        }

        job.setProcesados(procesados);
        job.setErrores(errores);
        job.setEstado("COMPLETADO");
        job.setFin(LocalDateTime.now());
        jobRepo.save(job);

        System.out.println("AFTER: Job " + jobId + " completado — "
                + procesados + " procesados, " + errores + " errores");
    }

    @Override
    public Map<String, Object> consultarEstado(String jobId) {
        JobStatus job = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job no encontrado: " + jobId));

        return Map.of(
                "jobId", job.getJobId(),
                "estado", job.getEstado(),
                "totalRegistros", job.getTotalRegistros(),
                "procesados", job.getProcesados(),
                "errores", job.getErrores(),
                "porcentaje", job.getPorcentaje()
        );
    }
}
