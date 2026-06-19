package com.backend.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) genérico para respuestas de la API.
 * Proporciona una estructura estandarizada para todas las respuestas
 * del sistema, incluyendo mensaje, datos y estado de éxito.
 * 
 * @param <T> Tipo de dato genérico para el campo datos
 *      
 * @version 1.0
 * @since 2026
 * @author Backend team
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente con Lombok
@Builder // Permite construir objetos usando el patrón Builder
@AllArgsConstructor // Genera constructor con todos los argumentos
@NoArgsConstructor // Genera constructor sin argumentos
public class ApiResponseDto<T> {
    /**
     * Mensaje descriptivo de la respuesta (éxito, error, información, etc.).
     */
    private String mensaje;
    
    /**
     * Datos genéricos que pueden contener cualquier tipo de información.
     * Puede ser un objeto, lista, mapa, etc., dependiendo del endpoint.
     */
    private T datos;
    
    /**
     * Indica si la operación se realizó exitosamente.
     * true = operación exitosa, false = operación fallida.
     */
    private boolean success;

    /**
     * Método estático para crear una respuesta de error.
     * Utiliza el patrón Builder para crear una respuesta con success = false.
     * 
     * @param <T> Tipo genérico de los datos (será null en caso de error)
     * @param mensaje Mensaje de error a mostrar
     * @return ApiResponseDto configurado como error
     */
    public static <T> ApiResponseDto<T> error(String mensaje) {
        return ApiResponseDto.<T>builder()
                .mensaje(mensaje)
                .success(false)
                .datos(null)
                .build();
    }
    
    /**
     * Método estático para crear una respuesta de éxito.
     * Utiliza el patrón Builder para crear una respuesta con success = true.
     * 
     * @param <T> Tipo genérico de los datos a devolver
     * @param mensaje Mensaje de éxito a mostrar
     * @param datos Datos a incluir en la respuesta
     * @return ApiResponseDto configurado como éxito
     */
    public static <T> ApiResponseDto<T> success(String mensaje, T datos) {
        return ApiResponseDto.<T>builder()
                .mensaje(mensaje)
                .success(true)
                .datos(datos)
                .build();
    }
}