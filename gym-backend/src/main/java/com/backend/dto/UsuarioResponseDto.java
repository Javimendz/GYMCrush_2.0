package com.backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Data Transfer Object (DTO) para la respuesta de información de usuario.
 * <p>
 * Este DTO se utiliza para enviar datos del usuario a la aplicación cliente,
 * excluyendo campos nulos y manteniendo un orden específico en el JSON.
 * Incluye información personal, contacto y roles de acceso.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente con
      // Lombok
@JsonInclude(JsonInclude.Include.NON_NULL) // Excluye campos con valor null del JSON
@JsonPropertyOrder({ // Define el orden exacto de los campos en la respuesta JSON
        "id",
        "username",
        "nombre",
        "apellidos",
        "dni",
        "correo",
        "telefono",
        "direccion",
        "ciudad",
        "codigoPostal",
        "pais",
        "rol"
})
@Schema(description = "DTO para representar la respuesta de un usuario")
public class UsuarioResponseDto {

    /**
     * Identificador único del usuario en la base de datos.
     */
    @Schema(description = "Identificador único del usuario")
    private Long id;

    /**
     * Nombre de usuario único utilizado para el inicio de sesión.
     */
    @Schema(description = "Nombre de usuario único para el inicio de sesión")
    private String username;

    /**
     * Lista de roles asignados al usuario (ej: ROLE_USER, ROLE_ADMIN).
     */
    @Schema(description = "Roles asignados al usuario (e.g., ADMIN, USER)")
    private List<RoleDto> rol;

    /**
     * Correo electrónico del usuario para notificaciones y contacto.
     */
    @Schema(description = "Correo electrónico del usuario")
    private String correo;

    /**
     * Número de teléfono móvil del usuario para contacto.
     */
    @Schema(description = "Número de teléfono del usuario")
    private String telefono;

    /**
     * Nombre completo del usuario para mostrar en la interfaz.
     */
    @Schema(description = "Nombre del usuario")
    private String nombre;

    /**
     * Apellidos del usuario para completar su nombre completo.
     */
    @Schema(description = "Apellidos del usuario")
    private String apellidos;

    /**
     * Número de identificación fiscal del usuario (DNI español).
     */
    @Schema(description = "Número de identificación del usuario")
    private String dni;

    /**
     * Dirección postal completa del usuario.
     */
    @Schema(description = "Dirección del usuario")
    private String direccion;

    /**
     * Ciudad de residencia del usuario.
     */
    @Schema(description = "Ciudad de residencia del usuario")
    private String ciudad;

    /**
     * País de residencia del usuario.
     */
    @Schema(description = "País de residencia del usuario")
    private String pais;

    /**
     * Código postal asociado a la dirección del usuario.
     */
    @Schema(description = "Código postal de la dirección del usuario")
    private String codigoPostal;

    /**
     * URL o base64 de la foto de perfil del usuario.
     */
    @Schema(description = "Foto de perfil del usuario")
    private String foto;

    /**
     * Biografía o descripción personal del usuario.
     */
    @Schema(description = "Biografía del usuario")
    private String bio;

}
