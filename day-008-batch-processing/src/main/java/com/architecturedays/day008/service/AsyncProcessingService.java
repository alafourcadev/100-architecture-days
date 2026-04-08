package com.architecturedays.day008.service;

import com.architecturedays.day008.model.JobStatus;
import com.architecturedays.day008.model.Registro;
import com.architecturedays.day008.repository.JobStatusRepository;
import com.architecturedays.day008.repository.RegistroRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AFTER: Recibe el CSV, responde en < 100ms con un jobId,
 * procesa en background por batches de 500, actualiza progreso.
 *
 * El usuario no espera. Consulta el progreso cuando quiere.
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
    public Map<String, Object> procesar(MultipartFile archivo) {
        String jobId = UUID.randomUUID().toString().substring(0, 8);

        // Contar lineas para el total (sin cargar todo en memoria)
        int totalLineas = 0;
        try (BufferedReader counter = new BufferedReader(
                new InputStreamReader(archivo.getInputStream()))) {
            counter.readLine(); // skip header
            while (counter.readLine() != null) totalLineas++;
        } catch (IOException e) {
            return Map.of("error", "No se pudo leer el archivo");
        }

        // Guardar archivo temporalmente para el procesamiento async
        Path tempFile;
        try {
            tempFile = Files.createTempFile("import-", ".csv");
            archivo.transferTo(tempFile);
        } catch (IOException e) {
            return Map.of("error", "No se pudo guardar el archivo temporal");
        }

        // Crear job y responder inmediatamente
        JobStatus job = new JobStatus(jobId, totalLineas);
        jobRepo.save(job);

        // Lanzar procesamiento en background
        procesarEnBackground(jobId, tempFile);

        System.out.println("AFTER: Job " + jobId + " creado (" + totalLineas
                + " registros). Respuesta al usuario en < 100ms");

        return Map.of(
                "jobId", jobId,
                "estado", "PENDIENTE",
                "totalRegistros", totalLineas,
                "urlEstado", "/api/importar/estado/" + jobId,
                "mensaje", "Procesamiento iniciado. Consulta el estado en la URL."
        );
    }

    @Async("batchExecutor")
    public void procesarEnBackground(String jobId, Path archivoPath) {
        JobStatus job = jobRepo.findById(jobId).orElseThrow();
        job.setEstado("EN_PROGRESO");
        jobRepo.save(job);

        int procesados = 0;
        int errores = 0;
        List<Registro> batch = new ArrayList<>(BATCH_SIZE);

        try (BufferedReader reader = Files.newBufferedReader(archivoPath)) {
            reader.readLine(); // skip header

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] campos = linea.split(",");
                try {
                    String codigo = campos[0].trim();
                    String descripcion = campos[1].trim();
                    BigDecimal monto = new BigDecimal(campos[2].trim());

                    Thread.sleep(1); // Simula procesamiento

                    monto = monto.multiply(BigDecimal.valueOf(1.21))
                            .setScale(2, RoundingMode.HALF_UP);

                    Registro r = new Registro(codigo, descripcion, monto);
                    r.setProcesado(true);
                    batch.add(r);
                    procesados++;
                } catch (Exception e) {
                    errores++;
                }

                // Guardar en batch
                if (batch.size() >= BATCH_SIZE) {
                    registroRepo.saveAll(batch);
                    batch.clear();

                    job.setProcesados(procesados);
                    job.setErrores(errores);
                    jobRepo.save(job);

                    System.out.println("AFTER: Batch guardado — " + procesados + "/"
                            + job.getTotalRegistros() + " (" + job.getPorcentaje() + "%)");
                }
            }

            // Ultimo batch parcial
            if (!batch.isEmpty()) {
                registroRepo.saveAll(batch);
            }

            job.setProcesados(procesados);
            job.setErrores(errores);
            job.setEstado("COMPLETADO");
            job.setFin(LocalDateTime.now());

        } catch (Exception e) {
            job.setEstado("ERROR");
            job.setMensajeError(e.getMessage());
        }

        jobRepo.save(job);

        // Limpiar archivo temporal
        try { Files.deleteIfExists(archivoPath); } catch (IOException ignored) {}

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
