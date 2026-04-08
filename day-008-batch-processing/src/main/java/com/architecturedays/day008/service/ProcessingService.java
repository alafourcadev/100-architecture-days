package com.architecturedays.day008.service;

import java.util.Map;

public interface ProcessingService {

    Map<String, Object> procesarTodos();

    Map<String, Object> consultarEstado(String jobId);
}
