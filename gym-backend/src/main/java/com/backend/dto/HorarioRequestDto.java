package com.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para solicitudes de horarios.
 * <p>
 * Esta clase representa la estructura de datos necesaria para crear
 * o actualizar un horario de actividad en el sistema GYM Crush.
 * Define cuándo y dónde se realizará una actividad específica,
 * incluyendo capacidad máxima y asignación de sala.
 * </p>
 * 
 * <p>
 * <b>Validaciones aplicadas:</b>
 * <ul>
 * <li>Día de la semana: obligatorio</li>
 * <li>Horas de inicio y fin: obligatorias</li>
 * <li>Aforo máximo: obligatorio y positivo</li>
 * <li>Sala: obligatoria</li>
 * <li>ID de actividad: obligatorio</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Programación de clases y actividades</li>
 * <li>Configuración de disponibilidad de instalaciones</li>
 * <li>Gestión de capacidad y reservas</li>
 * <li>Organización del calendario semanal</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Schema(description = "DTO para la solicitud de horario")
public class HorarioRequestDto {

    /**
     * Día de la semana en que se realizará la actividad.
     * <p>
     * Es obligatorio y debe ser uno de los días válidos de la semana.
     * Se utiliza para organizar el calendario semanal de actividades.
     * </p>
     * 
     * <p>
     * <b>Valores aceptados:</b> "Lunes", "Martes", "Miércoles", "Jueves",
     * "Viernes", "Sábado", "Domingo" (en español, capitalizados).
     * </p>
     */
    @NotBlank(message = "El día de la semana es obligatorio")
    @Schema(description = "Día de la semana (lunes, martes, etc.)", example = "Lunes")
    private String diaSemana;

    /**
     * Hora de inicio de la actividad.
     * <p>
     * Es obligatoria y especifica el momento exacto en que comenzará
     * la sesión. Se utiliza para la planificación y para evitar
     * conflictos de horario en la misma sala.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> HH:mm (24 horas)
     * <b>Ejemplos:</b> "08:00", "14:30", "18:45"
     * </p>
     */
    @NotNull(message = "La hora de inicio es obligatoria")
    @Schema(description = "Hora de inicio del horario", example = "08:00")
    private LocalTime horaInicio;

    /**
     * Hora de fin de la actividad.
     * <p>
     * Es obligatoria y debe ser posterior a la hora de inicio.
     * Define cuándo termina la sesión y libera la sala para
     * la siguiente actividad.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> HH:mm (24 horas)
     * <b>Ejemplos:</b> "09:00", "16:00", "20:00"
     * </p>
     * 
     * <p>
     * <b>Validación:</b> Siempre debe ser posterior a {@code horaInicio}.
     * </p>
     */
    @NotNull(message = "La hora de fin es obligatoria")
    @Schema(description = "Hora de fin del horario", example = "12:00")
    private LocalTime horaFin;

    /**
     * Aforo máximo permitido para la actividad en este horario.
     * <p>
     * Es obligatorio y representa el número máximo de participantes
     * que pueden inscribirse en esta sesión. Se utiliza para controlar
     * la capacidad y evitar sobrecuposición.
     * </p>
     * 
     * <p>
     * <b>Consideraciones:</b>
     * <ul>
     * <li>Depende del tamaño de la sala</li>
     * <li>Varía según el tipo de actividad</li>
     * <li>Influye en la gestión de reservas</li>
     * </ul>
     * </p>
     */
    @NotNull(message = "El aforo máximo es obligatorio")
    @Schema(description = "Aforo máximo permitido en el horario", example = "30")
    private Integer aforoMax;

    /**
     * Sala o instalación donde se impartirá la actividad.
     * <p>
     * Es obligatoria y especifica la ubicación física exacta.
     * Se utiliza para asignar recursos y evitar conflictos
     * de espacio entre actividades simultáneas.
     * </p>
     * 
     * <p>
     * <b>Ejemplos:</b> "Sala 1", "Sala de Pesas", "Piscina",
     * "Sala de Spinning", "Exterior", "Sala de Yoga".
     * </p>
     */
    @NotNull(message = "El ID de la sala es obligatorio")
    @Schema(description = "ID de la sala física donde se impartirá la actividad", example = "1")
    private Long salaId;

    /**
     * Identificador único de la actividad asociada a este horario.
     * <p>
     * Es obligatorio y sirve como referencia foreign key a la entidad
     * Activity. Permite relacionar este horario específico con la
     * actividad correspondiente (nombre, descripción, precio, etc.).
     * </p>
     * 
     * <p>
     * <b>Nota:</b> Solo se envía el ID para optimizar el tamaño
     * del payload y evitar redundancia de datos.
     * </p>
     */
    @NotNull(message = "El ID de la actividad es obligatorio")
    @Schema(description = "ID de la actividad asociada al horario", example = "1")
    private Long actividadId; // Solo enviamos el ID

    @NotNull(message = "El ID del entrenador es obligatorio")
    @Schema(description = "ID del usuario con rol ENTRENADOR", example = "2")
    private Long entrenadorId;

    
}