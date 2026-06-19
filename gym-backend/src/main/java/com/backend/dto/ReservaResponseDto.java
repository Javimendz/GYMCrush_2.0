package com.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Data Transfer Object (DTO) para respuestas de reservas de horarios.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se solicita información sobre una reserva existente.
 * Incluye información completa de la reserva más datos "aplanados"
 * del usuario y la actividad para optimizar el consumo en el cliente.
 * </p>
 * 
 * <p>
 * <b>Características especiales:</b>
 * <ul>
 * <li>Campos "aplanados" para evitar consultas adicionales</li>
 * <li>Información optimizada para Android RecyclerView</li>
 * <li>Incluye estado actual de la reserva</li>
 * <li>Datos de actividad para mostrar directamente en UI</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Estados posibles de reserva:</b>
 * <ul>
 * <li><b>CONFIRMADA</b>: Reserva activa y confirmada</li>
 * <li><b>PENDIENTE</b>: Esperando confirmación o pago</li>
 * <li><b>CANCELADA</b>: Cancelada por el usuario</li>
 * <li><b>COMPLETADA</b>: Actividad realizada</li>
 * <li><b>NO_ASISTIO</b>: Usuario no asistió</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Confirmación de reservas realizadas</li>
 * <li>Listado de reservas del usuario</li>
 * <li>Gestión de cancelaciones</li>
 * <li>Historial de actividades</li>
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
@Tag(name = "ReservaResponseDto", description = "Respuesta de una reserva de un horario para un usuario")
public class ReservaResponseDto {

    /**
     * Identificador único de la reserva en la base de datos.
     * <p>
     * Es generado automáticamente cuando se crea la reserva
     * y sirve como referencia única para todas las operaciones
     * relacionadas con esta reserva específica.
     * </p>
     */
    @Schema(description = "Identificador único de la reserva")
    private Long id;

    /**
     * Fecha en que está programada la reserva.
     * <p>
     * Corresponde al día calendario en que se realizará
     * la actividad reservada. Puede ser la fecha actual
     * o una fecha futura según la solicitud.
     * </p>
     */
    @Schema(description = "Fecha de la reserva")
    private LocalDate fecha;

    /**
     * Estado actual de la reserva.
     * <p>
     * Indica el status actual de la reserva en el sistema.
     * Se utiliza para controlar el flujo de reservas
     * y mostrar información relevante al usuario.
     * </p>
     * 
     * <p>
     * <b>Transiciones comunes:</b>
     * PENDIENTE → CONFIRMADA → COMPLETADA/NO_ASISTIO
     * </p>
     */
    @Schema(description = "Estado de la reserva")
    private String estado;

    // Datos del Usuario (aplanados para optimización)

    /**
     * Nombre de usuario del usuario que realizó la reserva.
     * <p>
     * Campo aplanado que contiene el username del usuario.
     * Es fundamental para identificar quién realizó la reserva
     * sin necesidad de consultar la tabla de usuarios.
     * </p>
     * 
     * <p>
     * <b>Optimización:</b> Evita consultas adicionales
     * en el cliente, especialmente importante para RecyclerView.
     * </p>
     */
    @Schema(description = "Nombre de usuario del usuario que realizó la reserva")
    private String username;

    // CAMPOS NUEVOS PARA ANDROID (aplanados de la relación)

    /**
     * Nombre de la actividad que ha sido reservada.
     * <p>
     * Campo aplanado que contiene el nombre descriptivo
     * de la actividad. Permite mostrar directamente
     * el nombre en la interfaz sin consultas adicionales.
     * </p>
     * 
     * <p>
     * <b>Ejemplos:</b> "Clase de Yoga", "Spinning", "Entrenamiento Personal"
     * </p>
     * 
     * <p>
     * <b>Importante para Android:</b> Este campo optimiza
     * el rendimiento del RecyclerView al evitar llamadas
     * adicionales para obtener el nombre de la actividad.
     * </p>
     */
    @Schema(description = "Nombre de la actividad reservada")
    private String nombreActividad;

    /**
     * Día de la semana en que se realiza la actividad reservada.
     * <p>
     * Campo aplanado que indica el día calendario
     * de la semana. Facilita la organización
     * y visualización en calendarios semanales.
     * </p>
     * 
     * <p>
     * <b>Ejemplos:</b> "Lunes", "Martes", "Miércoles", etc.
     * </p>
     */
    @Schema(description = "Día de la semana de la reserva")
    private String diaSemana;

    @Schema(description = "Identificador único del usuario que realizó la reserva")
    private Long usuarioId;
    private Long plazasLibres;
     private String nombreSala;
    /**
     * Hora de inicio de la actividad reservada.
     * <p>
     * Campo aplanado que especifica la hora exacta
     * en que comienza la actividad. Formato: HH:mm (24 horas).
     * </p>
     * 
     * <p>
     * <b>Usos:</b>
     * <ul>
     * <li>Mostrar en calendarios y agendas</li>
     * <li>Calcular tiempo restante hasta la actividad</li>
     * <li>Organizar recordatorios y notificaciones</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Hora de inicio de la actividad reservada")
    private LocalTime horaInicio;

    private String nombreEntrenador; // Añadir esto
    private Long entrenadorId;
}
