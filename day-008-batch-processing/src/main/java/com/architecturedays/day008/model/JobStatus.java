package com.architecturedays.day008.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class JobStatus {

    @Id
    private String jobId;
    private String estado;
    private int totalRegistros;
    private int procesados;
    private int errores;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private String mensajeError;

    public JobStatus() {}

    public JobStatus(String jobId, int totalRegistros) {
        this.jobId = jobId;
        this.estado = "PENDIENTE";
        this.totalRegistros = totalRegistros;
        this.procesados = 0;
        this.errores = 0;
        this.inicio = LocalDateTime.now();
    }

    public String getJobId() { return jobId; }
    public String getEstado() { return estado; }
    public int getTotalRegistros() { return totalRegistros; }
    public int getProcesados() { return procesados; }
    public int getErrores() { return errores; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFin() { return fin; }
    public String getMensajeError() { return mensajeError; }

    public void setEstado(String estado) { this.estado = estado; }
    public void setProcesados(int procesados) { this.procesados = procesados; }
    public void setErrores(int errores) { this.errores = errores; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }
    public void setMensajeError(String mensajeError) { this.mensajeError = mensajeError; }

    public int getPorcentaje() {
        if (totalRegistros == 0) return 0;
        return (int) ((procesados * 100.0) / totalRegistros);
    }
}
