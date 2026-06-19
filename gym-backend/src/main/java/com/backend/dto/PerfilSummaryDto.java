package com.backend.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para resumen de perfil de usuario.
 * <p>
 * Esta clase representa una versión simplificada del perfil de usuario,
 * diseñada específicamente para operaciones donde solo se necesita
 * información básica para identificación y visualización.
 * Es ideal para listados, búsquedas y vistas previas.
 * </p>
 * 
 * <p>
 * <b>Características principales:</b>
 * <ul>
 * <li>Payload reducido para optimizar rendimiento</li>
 * <li>Campos esenciales para identificación visual</li>
 * <li>Ideal para RecyclerView y listados grandes</li>
 * <li>Evita exposición de datos sensibles</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos recomendados:</b>
 * <ul>
 * <li>Listados de usuarios en búsquedas</li>
 * <li>Vistas previas de perfiles</li>
 * <li>Selección de usuarios en formularios</li>
 * <li>Notificaciones y menciones</li>
 * <li>Componentes de UI con información limitada</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Seguridad:</b>
 * Este DTO está diseñado para no exponer información
 * sensible como DNI, teléfono, dirección completa o datos de salud.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@AllArgsConstructor // Genera constructor con todos los parámetros
@NoArgsConstructor // Genera constructor sin parámetros
@Tag(name = "PerfilSummaryDto", description = "Resumen de información del perfil de un usuario")
public class PerfilSummaryDto {

    /**
     * Identificador único del perfil en la base de datos.
     * <p>
     * Es generado automáticamente cuando se crea el usuario
     * y sirve como referencia única para operaciones detalladas.
     * </p>
     */
    @Schema(description = "Identificador único del perfil")
    private Long id;

    /**
     * Nombre de usuario para inicio de sesión.
     * <p>
     * Identificador único del usuario en el sistema,
     * utilizado para autenticación y menciones.
     * </p>
     */
    @Schema(description = "Nombre de usuario")
    private String username;

    /**
     * Nombre del usuario.
     * <p>
     * Primer nombre del usuario, utilizado para
     * identificación personal y visualización en UI.
     * </p>
     */
    @Schema(description = "Nombre del usuario")
    private String nombre;

    /**
     * Apellidos del usuario.
     * <p>
     * Apellidos que completan el nombre completo
     * del usuario para identificación en la interfaz.
     * </p>
     */
    @Schema(description = "Apellidos del usuario")
    private String apellidos;

    /**
     * URL de la fotografía de perfil del usuario.
     * <p>
     * Enlace a la imagen de perfil o avatar del usuario.
     * Se utiliza para identificación visual en listados
     * y componentes sociales de la aplicación.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> URL accesible públicamente
     * <b>Alternativa:</b> Puede ser null si no tiene foto
     * </p>
     */
    @Schema(description = "URL de la foto de perfil")
    private String foto;

    /**
     * Ubicación geográfica del usuario.
     * <p>
     * Información de ubicación simplificada del usuario.
     * Puede incluir ciudad, país o región según
     * la configuración de privacidad del usuario.
     * </p>
     * 
     * <p>
     * <b>Ejemplos:</b> "Madrid, España", "Barcelona", "Norte de España"
     * </p>
     * 
     * <p>
     * <b>Nota de privacidad:</b> Este campo debe respetar
     * la configuración de privacidad del usuario y puede
     * ser null si el usuario prefiere no compartir ubicación.
     * </p>
     */
    @Schema(description = "Ubicación del usuario")
    private String location;

}
