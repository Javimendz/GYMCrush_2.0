package com.backend.exceptions; 

/**
 * Excepción personalizada para errores de lógica de negocio.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}