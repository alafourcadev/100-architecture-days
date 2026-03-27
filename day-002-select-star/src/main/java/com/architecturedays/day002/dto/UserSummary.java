package com.architecturedays.day002.dto;

/**
 * Interface Projection - Spring Data JPA genera el proxy automáticamente.
 * Solo trae id, email y name de la base de datos.
 */
public interface UserSummary {
    Long getId();
    String getEmail();
    String getName();
}
