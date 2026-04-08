package com.architecturedays.day008.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ProcessingService {

    Map<String, Object> procesar(MultipartFile archivo);

    Map<String, Object> consultarEstado(String jobId);
}
