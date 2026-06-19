package com.backend.security.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para la respuesta de autenticación JWT.
 * Contiene el token de acceso, información del usuario y roles
 * después de un inicio de sesión exitoso.
 * 
 * @author Backend team
 * @version 1.0
 * @since 2026
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente con Lombok
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
@Builder // Permite construir objetos usando el patrón Builder
@JsonPropertyOrder({ "id", "nombre", "username", "roles", "accessToken", "tokenType" }) // Define el orden de los campos en el JSON
public class JwtAuthResponseDto {
    /**
     * Identificador único del usuario en la base de datos.
     */
    private Long id;
    
    /**
     * Lista de roles asignados al usuario (ej: ROLE_USER, ROLE_ADMIN).
     */
    private List<String> roles;
    
    /**
     * Token JWT de acceso para autenticar futuras peticiones.
     */
    private String accessToken;
    
    /**
     * Tipo de token, siempre será "Bearer" según estándar OAuth 2.0.
     */
    @Builder.Default // Para establecer valor por defecto sin necesidad de hacerlo manualmente
    private String tokenType = "Bearer";
    
    /**
     * Nombre de usuario utilizado para el login.
     */
    private String username;
    
    /**
     * Nombre completo del usuario para mostrar en la interfaz.
     */
    private String nombre;

    /**
     * Constructor simplificado que solo establece el token de acceso.
     * Útil para respuestas rápidas donde solo se necesita el token.
     * 
     * @param accessToken Token JWT generado para el usuario
     */
    public JwtAuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    
}
