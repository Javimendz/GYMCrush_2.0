package com.backend.security.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.backend.domain.enums.EnumGenero;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para el registro de nuevos usuarios.
 * Contiene todos los campos necesarios para crear una cuenta de usuario
 * incluyendo información personal, contacto y roles de acceso.
 * 
 * @author Backend team
 * @version 1.0
 * @since 2026
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente con Lombok
@AllArgsConstructor // Genera constructor con todos los argumentos
@NoArgsConstructor // Genera constructor sin argumentos
public class RegisterDto {

    /**
     * Nombre de usuario único para el login.
     * Debe tener entre 3 y 20 caracteres y no puede estar vacío.
     */
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 20, message = "El username debe tener entre 3 y 20 caracteres")
    private String username;

    /**
     * Contraseña del usuario con mínimo 8 caracteres.
     * Se recomienda incluir mayúsculas, números y caracteres especiales.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    // Opcional: Podrías añadir un @Pattern para forzar mayúsculas/números
    private String password;

    /**
     * Correo electrónico del usuario, debe ser válido y único.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo no válido")
    private String email;

    /**
     * Nombre completo del usuario.
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /**
     * Apellidos del usuario para completar el perfil.
     */
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    /**
     * Número de teléfono móvil con formato español (9 dígitos).
     */
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    /**
     * DNI español con formato válido (8 dígitos + letra de control).
     */
    @Pattern(regexp = "^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$", message = "Formato de DNI no válido")
    private String dni;

    /**
     * Dirección postal completa del usuario (opcional).
     */
    private String direccion;
    
    /**
     * Ciudad de residencia del usuario (opcional).
     */
    private String ciudad;
    
    /**
     * País de residencia del usuario (opcional).
     */
    private String pais;

    /**
     * Código postal español con formato de 5 dígitos.
     */
    @Pattern(regexp = "^[0-9]{5}$", message = "El código postal debe tener 5 dígitos")
    private String codigoPostal;

    /**
     * Fecha de nacimiento del usuario, debe ser una fecha pasada.
     */
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    /**
     * Género del usuario según el enumerado definido.
     */
    @NotNull(message = "El género es obligatorio")
    private EnumGenero genero;

    /**
     * Conjunto de roles asignados al usuario (ej: ROLE_USER, ROLE_ADMIN).
     * Al menos debe tener un rol asignado.
     */
    @NotEmpty(message = "Al menos un rol es obligatorio")
    private Set<String> rol = new HashSet<>();


    private List<Float> faceVector;
public void setRoles(Set<String> roles) {
        if (roles != null) {
            this.rol = roles;
        }
    }

    public Set<String> getRoles() {
        return rol;
    }
}

