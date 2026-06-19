package com.backend.dto;

import java.time.LocalTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para respuestas de horarios.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se solicita información sobre un horario de actividad existente.
 * Incluye todos los datos del horario más información "aplanada"
 * de la actividad asociada para facilitar el consumo en el cliente.
 * </p>
 * 
 * <p>
 * <b>Características especiales:</b>
 * <ul>
 * <li>Campos "aplanados" de la relación con Actividad</li>
 * <li>Optimizado para RecyclerView en Android</li>
 * <li>Incluye nombre de actividad para mostrar directamente</li>
 * <li>Evita consultas adicionales en el cliente</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Listado de horarios disponibles</li>
 * <li>Confirmación de reservas</li>
 * <li>Calendario semanal de actividades</li>
 * <li>Gestión de disponibilidad</li>
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
@Schema(description = "DTO para la respuesta de horario")
public class HorarioResponseDto {

    /**
     * Identificador único del horario en la base de datos.
     * <p>
     * Es generado automáticamente cuando se crea el horario
     * y sirve como referencia única para todas las operaciones.
     * </p>
     */
    @Schema(description = "ID único del horario", example = "1")
    private Long id;

    private Integer plazasLibres;
    /**
     * Día de la semana en que se realiza la actividad.
     * <p>
     * Corresponde al día calendario de la semana.
     * Ejemplos: "Lunes", "Martes", "Miércoles", etc.
     * </p>
     */
    @Schema(description = "Día de la semana (lunes, martes, etc.)", example = "Lunes")
    private String diaSemana;

    /**
     * Hora de inicio de la actividad.
     * <p>
     * Especifica el momento exacto en que comienza la sesión.
     * Formato: HH:mm (24 horas).
     * </p>
     */
    @Schema(description = "Hora de inicio del horario", example = "08:00")
    private LocalTime horaInicio;

    /**
     * Hora de fin de la actividad.
     * <p>
     * Define cuándo termina la sesión y libera la sala.
     * Formato: HH:mm (24 horas).
     * </p>
     */
    @Schema(description = "Hora de fin del horario", example = "12:00")
    private LocalTime horaFin;

    /**
     * Aforo máximo permitido para la actividad.
     * <p>
     * Representa el número máximo de participantes
     * que pueden inscribirse en esta sesión.
     * </p>
     */
    @Schema(description = "Aforo máximo permitido en el horario", example = "30")
    private Integer aforoMax;
    /**
     * Sala o instalación donde se realiza la actividad.
     * <p>
     * Ubicación física específica donde se impartirá la sesión.
     * Ejemplos: "Sala 1", "Piscina", "Exterior".
     * </p>
     */
    @Schema(description = "Nombre de la sala", example = "Sala Zen")
    private String nombreSala;

    // Campos "aplanados" de la relación con Actividad
    // Esto es vital para que el RecyclerView en Android muestre el nombre
    // directamente sin necesidad de consultas adicionales

    /**
     * Identificador único de la actividad asociada a este horario.
     * <p>
     * Campo aplanado que permite referenciar la actividad completa
     * si se necesita información adicional. Es útil para navegación
     * y operaciones detalladas.
     * </p>
     */
    @Schema(description = "ID de la actividad asociada al horario", example = "1")
    private Long actividadId;

    /**
     * Nombre de la actividad asociada a este horario.
     * <p>
     * Campo aplanado que contiene el nombre descriptivo de la actividad.
     * Es fundamental para la UI ya que permite mostrar directamente
     * el nombre sin necesidad de consultas adicionales a la API.
     * </p>
     * 
     * <p>
     * <b>Importante para Android:</b> Este campo optimiza el rendimiento
     * del RecyclerView al evitar llamadas adicionales para obtener
     * el nombre de la actividad.
     * </p>
     */
    @Schema(description = "Nombre de la actividad asociada al horario", example = "Clase de Fútbol")
    private String nombreActividad;

    @Schema(description = "ID de la sala física", example = "1")
    private Long salaId;

    // En el Backend (HorarioResponseDto.java)
    @Schema(description = "Nombre del entrenador", example = "Juan Pérez")
    private String nombreEntrenador;

    @Schema(description = "ID del entrenador", example = "1")
    private Long entrenadorId;
}
