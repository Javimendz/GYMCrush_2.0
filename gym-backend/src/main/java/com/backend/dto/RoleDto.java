package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para representación de roles del sistema.
 * <p>
 * Esta clase representa la estructura de datos básica para un rol
 * en el sistema de gestión de accesos GYM Crush. Los roles
 * definen los permisos y niveles de acceso que tienen los usuarios
 * dentro de la aplicación.
 * </p>
 * 
 * <p>
 * <b>Jerarquía de roles típicos:</b>
 * <ul>
 * <li><b>ROLE_USER</b>: Usuario básico con acceso limitado</li>
 * <li><b>ROLE_TRAINER</b>: Entrenador con permisos de gestión</li>
 * <li><b>ROLE_ADMIN</b>: Administrador con acceso completo</li>
 * <li><b>ROLE_SUPER_ADMIN</b>: Super administrador del sistema</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Asignación de roles a usuarios</li>
 * <li>Gestión de permisos del sistema</li>
 * <li>Validación de accesos en endpoints</li>
 * <li>Auditoría de privilegios</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Seguridad:</b>
 * Los roles son fundamentales para el control de acceso
 * y deben ser gestionados con cuidado para evitar privilegios
 * no autorizados.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@AllArgsConstructor // Genera constructor con todos los parámetros
@NoArgsConstructor // Genera constructor sin parámetros
@Schema(description = "DTO para representar un rol en el sistema")
public class RoleDto {

    /**
     * Identificador único del rol en la base de datos.
     * <p>
     * Es generado automáticamente cuando se crea el rol
     * y sirve como referencia única para todas las operaciones
     * relacionadas con este rol específico.
     * </p>
     * 
     * <p>
     * <b>Importancia:</b> Este ID se utiliza en las tablas
     * de usuario-rol para establecer las relaciones de asignación.
     * </p>
     */
    @Schema(description = "Identificador único del rol")
    private Long id;

    /**
     * Nombre descriptivo del rol.
     * <p>
     * Nombre único que identifica el rol en el sistema.
     * Generalmente sigue el formato ROLE_NOMBRE para mantener
     * consistencia con Spring Security.
     * </p>
     * 
     * <p>
     * <b>Convención de nomenclatura:</b>
     * <ul>
     * <li>Prefijo: ROLE_ (obligatorio para Spring Security)</li>
     * <li>Nombre: descriptivo y en mayúsculas</li>
     * <li>Ejemplos: ROLE_USER, ROLE_ADMIN, ROLE_TRAINER</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>Debe ser único en el sistema</li>
     * <li>No debe contener caracteres especiales</li>
     * <li>Longitud recomendada: 3-50 caracteres</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Nombre del rol")
    private String name;
}
