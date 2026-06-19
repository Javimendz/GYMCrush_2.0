package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para solicitudes de creación de actividades.
 * <p>
 * Esta clase representa la estructura de datos necesaria para crear una nueva
 * actividad en el sistema GYM Crush. Incluye toda la información requerida
 * para definir una actividad física o clase que se ofrecerá en el gimnasio.
 * </p>
 * 
 * <p>
 * <b>Validaciones aplicadas:</b>
 * <ul>
 * <li>Nombre: obligatorio, entre 3 y 50 caracteres</li>
 * <li>Descripción: opcional, máximo 500 caracteres</li>
 * <li>Duración: obligatoria, mínimo 1 minuto</li>
 * <li>Lugar: obligatorio</li>
 * <li>Precio: obligatorio, no puede ser negativo</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera constructor sin parámetros
@AllArgsConstructor // Genera constructor con todos los parámetros
@Builder // Permite la construcción fluida de objetos
@Schema(description = "DTO para la solicitud de creación de una actividad")
public class ActividadRequestDto {

    /**
     * Nombre descriptivo de la actividad.
     * <p>
     * Es obligatorio y debe tener entre 3 y 50 caracteres.
     * Se utiliza para identificar la actividad en el sistema y mostrarla
     * a los usuarios. Ejemplos: "Clase de Fútbol", "Yoga Matutino", "Spinning".
     * </p>
     */
    @NotBlank(message = "El nombre de la actividad es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de la actividad", example = "Clase de Fútbol")
    private String nombre;

    /**
     * Descripción detallada de la actividad.
     * <p>
     * Es opcional y puede tener hasta 500 caracteres.
     * Proporciona información adicional sobre el contenido, requisitos,
     * nivel de dificultad o cualquier detalle relevante para los usuarios.
     * </p>
     */
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Schema(description = "Descripción detallada de la actividad", example = "Clase de fútbol en la sala A")
    private String descripcion;

    /**
     * Duración estimada de la actividad en minutos.
     * <p>
     * Es obligatoria y debe ser como mínimo 1 minuto.
     * Se utiliza para la planificación de horarios y cálculo de precios.
     * </p>
     */
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración mínima debe ser de 1 minuto")
    @Schema(description = "Duración de la actividad en minutos", example = "60")
    private Integer duracion;

    /**
     * Ubicación física donde se realizará la actividad.
     * <p>
     * Es obligatorio y debe especificar el lugar exacto.
     * Ejemplos: "Sala A", "Piscina", "Exterior", "Sala de Pesas".
     * Ayuda a los usuarios a localizar la actividad y evita conflictos de horario.
     * </p>
     */
    @NotBlank(message = "El lugar es obligatorio (ej: Sala A, Piscina, Exterior)")
    @Schema(description = "Lugar donde se realizará la actividad", example = "Sala A")
    private String sala;

    /**
     * Precio de la actividad en la moneda local.
     * <p>
     * Es obligatorio y no puede ser negativo.
     * Representa el costo que deben pagar los usuarios para participar
     * en esta actividad. Puede ser 0 para actividades gratuitas.
     * </p>
     */
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Schema(description = "Precio de la actividad", example = "50.00")
    private Integer precio;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoriaId;
}