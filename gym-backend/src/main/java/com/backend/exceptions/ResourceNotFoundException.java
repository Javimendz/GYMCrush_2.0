package com.backend.exceptions;

/**
 * Excepción personalizada lanzada cuando se solicita un recurso que no existe en la base de datos.
 * <p>
 * Esta excepción debe usarse cuando una entidad buscada por ID o cualquier otro criterio
 * no se encuentra, resultando en una respuesta HTTP 404 (NOT FOUND).
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public class ResourceNotFoundException extends RuntimeException{
    
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message el mensaje descriptivo del error
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}