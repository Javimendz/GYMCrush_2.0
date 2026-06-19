package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Data Transfer Object (DTO) para respuestas de actividades.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se solicita información sobre una actividad existente.
 * Incluye todos los datos de la actividad más el identificador único
 * generado por la base de datos.
 * </p>
 * 
 * <p>
 * <b>Usos típicos:</b>
 * <ul>
 * <li>Respuesta a consultas de actividades por ID</li>
 * <li>Listado de actividades disponibles</li>
 * <li>Confirmación después de crear una actividad</li>
 * <li>Respuesta a actualizaciones de actividades</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Schema(description = "DTO para la respuesta de una actividad")
public class ActividadResponseDto {

    /**
     * Identificador único de la actividad en la base de datos.
     * <p>
     * Es generado automáticamente cuando se crea la actividad
     * y sirve como referencia única para todas las operaciones.
     * </p>
     */
    @Schema(description = "ID único de la actividad", example = "1")
    private Long id;

    /**
     * Nombre descriptivo de la actividad.
     * <p>
     * Nombre que identifica la actividad en el sistema.
     * Ejemplos: "Clase de Fútbol", "Yoga Matutino", "Spinning".
     * </p>
     */
    @Schema(description = "Nombre de la actividad", example = "Clase de Fútbol")
    private String nombre;

    /**
     * Descripción detallada de la actividad.
     * <p>
     * Proporciona información adicional sobre el contenido,
     * requisitos, nivel de dificultad o cualquier detalle relevante.
     * Puede ser null si no se proporcionó descripción.
     * </p>
     */
    @Schema(description = "Descripción detallada de la actividad", example = "Clase de fútbol en la sala A")
    private String descripcion;

    /**
     * Duración de la actividad en minutos.
     * <p>
     * Tiempo estimado que dura la sesión de la actividad.
     * Se utiliza para la planificación y cálculo de horarios.
     * </p>
     */
    @Schema(description = "Duración de la actividad en minutos", example = "60")
    private Integer duracion;

    /**
     * Ubicación física donde se realiza la actividad.
     * <p>
     * Lugar específico dentro del gimnasio o instalación.
     * Ejemplos: "Sala A", "Piscina", "Exterior", "Sala de Pesas".
     * </p>
     */
    @Schema(description = "Lugar donde se realizará la actividad", example = "Sala A")
    private String sala;

    /**
     * Precio de la actividad en la moneda local.
     * <p>
     * Costo que deben pagar los usuarios para participar.
     * Puede ser 0 para actividades gratuitas.
     * </p>
     */
    @Schema(description = "Precio de la actividad", example = "50.00")
    private Integer precio;

    private CategoriaResponseDto categoria;
}
