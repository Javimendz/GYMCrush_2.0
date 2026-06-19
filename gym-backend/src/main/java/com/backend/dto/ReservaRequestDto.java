package com.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para solicitudes de reserva de horarios.
 * <p>
 * Esta clase representa la estructura de datos necesaria para que un usuario
 * reserve un horario específico de una actividad. Es la interfaz
 * principal para el sistema de reservas del gimnasio.
 * </p>
 * 
 * <p>
 * <b>Flujo de reserva:</b>
 * <ol>
 * <li>Usuario selecciona un horario disponible</li>
 * <li>Se envía este DTO con los IDs necesarios</li>
 * <li>Sistema valida disponibilidad y crea reserva</li>
 * <li>Se devuelve confirmación con ReservaResponseDto</li>
 * </ol>
 * </p>
 * 
 * <p>
 * <b>Validaciones aplicadas:</b>
 * <ul>
 * <li>ID de usuario: obligatorio</li>
 * <li>ID de horario: obligatorio</li>
 * <li>Fecha: opcional, si no se especifica usa fecha actual</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Reserva inmediata de clases</li>
 * <li>Reserva programada para fechas futuras</li>
 * <li>Sistema de espera para actividades completas</li>
 * <li>Gestión de cancelaciones y modificaciones</li>
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
@Tag(name = "ReservaRequestDto", description = "Solicitud de reserva de un horario para un usuario")
public class ReservaRequestDto {

    /**
     * Identificador único del usuario que realiza la reserva.
     * <p>
     * Es obligatorio y corresponde al ID del usuario
     * autenticado que desea reservar el horario.
     * Se utiliza para asociar la reserva al usuario correcto.
     * </p>
     * 
     * <p>
     * <b>Validación:</b> El usuario debe existir y estar activo.
     * <b>Permisos:</b> El usuario debe tener permisos para reservar.
     * </p>
     */
    @Schema(description = "Identificador único del usuario")
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    /**
     * Identificador único del horario a reservar.
     * <p>
     * Es obligatorio y corresponde al ID del horario
     * específico de la actividad que se desea reservar.
     * Incluye día, hora, sala y capacidad.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>El horario debe existir</li>
     * <li>Debe tener disponibilidad</li>
     * <li>No debe estar en el pasado</li>
     * <li>El usuario no debe tener reserva conflictiva</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Identificador único del horario")
    @NotNull(message = "El ID del horario es obligatorio")
    private Long horarioId;

    /**
     * Fecha específica para la reserva.
     * <p>
     * Es opcional y permite reservar para fechas futuras.
     * Si no se especifica, el sistema utilizará la fecha actual
     * (LocalDate.now()) para crear la reserva.
     * </p>
     * 
     * <p>
     * <b>Casos de uso:</b>
     * <ul>
     * <li><b>Null/omitido:</b> Reserva para el día actual</li>
     * <li><b>Fecha futura:</b> Reserva programada</li>
     * <li><b>Fecha pasada:</b> Rechazado por validación</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Formato:</b> ISO 8601 (yyyy-MM-dd)
     * <b>Ejemplo:</b> 2024-12-25
     * </p>
     * 
     * <p>
     * <b>Nota de implementación:</b> En el servicio,
     * si este campo es null, se debe usar {@code LocalDate.now()}
     * para establecer la fecha automáticamente.
     * </p>
     */
    @Schema(description = "Fecha de la reserva (opcional)")
    private LocalDate fecha;
}