package com.backend.dto;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para respuestas de categorías de tutoriales.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se solicita información sobre una categoría de tutoriales existente.
 * Incluye todos los datos de la categoría más el identificador único
 * y la cantidad de videos asociados.
 * </p>
 * 
 * <p>
 * <b>Características especiales:</b>
 * <ul>
 * <li>Incluye el campo {@code cantidadVideos} calculado dinámicamente</li>
 * <li>Utiliza el patrón Builder para construcción flexible</li>
 * <li>Se usa en listados y consultas individuales de categorías</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Builder // Permite la construcción fluida de objetos
@Schema(description = "DTO para la respuesta de una categoría de tutorial")
public class CategoriaTutorialResponseDto {

    /**
     * Identificador único de la categoría en la base de datos.
     * <p>
     * Es generado automáticamente cuando se crea la categoría
     * y sirve como referencia única para todas las operaciones.
     * </p>
     */
    @Schema(description = "ID único de la categoría de tutorial", example = "1")
    private Long id;

    /**
     * Nombre de la categoría de tutorial.
     * <p>
     * Nombre que identifica la categoría en el sistema.
     * Ejemplos: "Deportes", "Nutrición", "Ejercicios", "Yoga".
     * </p>
     */
    @Schema(description = "Nombre de la categoría de tutorial", example = "Deportes")
    private String nombre;

    /**
     * Descripción detallada de la categoría de tutorial.
     * <p>
     * Proporciona información adicional sobre el tipo de tutoriales
     * que se incluyen en esta categoría. Puede ser null si no se proporcionó.
     * </p>
     */
    @Schema(description = "Descripción detallada de la categoría de tutorial", example = "Categoría que contiene tutoriales sobre deportes")
    private String descripcion;

    /**
     * Cantidad de videos tutoriales asociados a esta categoría.
     * <p>
     * Este campo es calculado dinámicamente en el Mapper
     * y representa el número total de tutoriales que pertenecen
     * a esta categoría. Es útil para mostrar estadísticas en la UI.
     * </p>
     * 
     * <p>
     * <b>Nota:</b> No se almacena directamente en la base de datos,
     * se calcula al momento de la respuesta para optimizar el rendimiento.
     * </p>
     */
    @Schema(description = "Cantidad de videos en la categoría de tutorial", example = "10")
    private Integer cantidadVideos; // Calculado en el Mapper para la UI
}