package com.backend.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) para el inicio de sesión de usuarios.
 * Contiene las credenciales básicas necesarias para autenticar
 * a un usuario en el sistema mediante username y password.
 * 
 * @author Backend team
 * @version 1.0
 * @since 2026
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente con Lombok
public class LoginDto {
    /**
     * Nombre de usuario o email para el inicio de sesión.
     * Debe tener entre 3 y 50 caracteres y no puede estar vacío.
     */
    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;
    
    /**
     * Contraseña del usuario para autenticación.
     * Debe tener entre 6 y 30 caracteres y no puede estar vacía.
     */
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, max = 30, message = "La contraseña debe tener entre 6 y 30 caracteres")
    private String password;

}
