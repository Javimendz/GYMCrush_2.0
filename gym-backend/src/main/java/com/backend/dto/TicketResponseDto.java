package com.backend.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

/**
 * Data Transfer Object (DTO) para respuestas de tickets de soporte.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se solicita información sobre un ticket existente.
 * Incluye información completa del ticket más datos del usuario
 * para facilitar el seguimiento y gestión.
 * </p>
 * 
 * <p>
 * <b>Información incluida:</b>
 * <ul>
 * <li><b>Datos del ticket:</b> ID, asunto, mensaje, estado</li>
 * <li><b>Timestamps:</b> creación y resolución</li>
 * <li><b>Datos del usuario:</b> ID, email, nombre completo</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Estados posibles del ticket:</b>
 * <ul>
 * <li><b>ABIERTO</b>: Ticket creado y pendiente de atención</li>
 * <li><b>EN_PROGRESO</b>: Siendo atendido por el equipo de soporte</li>
 * <li><b>ESPERA_RESPUESTA</b>: Esperando respuesta del usuario</li>
 * <li><b>RESUELTO</b>: Problema solucionado satisfactoriamente</li>
 * <li><b>CERRADO</b>: Cerrado sin resolución</li>
 * <li><b>REABIERTO</b>: Reabierto por nueva información</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Consulta de tickets creados por el usuario</li>
 * <li>Listado de tickets asignados al equipo de soporte</li>
 * <li>Seguimiento del estado de incidencias</li>
 * <li>Análisis de tiempos de respuesta y resolución</li>
 * <li>Exportación de historial de soporte</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Builder // Permite la construcción fluida de objetos
@Tag(name = "TicketResponseDto", description = "Respuesta de un ticket")
public class TicketResponseDto {

    /**
     * Identificador único del ticket en la base de datos.
     * <p>
     * Es generado automáticamente cuando se crea el ticket
     * y sirve como referencia única para todas las operaciones
     * relacionadas con esta incidencia específica.
     * </p>
     */
    @Schema(description = "Identificador único del ticket")
    private Long id;

    /**
     * Asunto o título del ticket.
     * <p>
     * Resumen del problema o consulta reportada por el usuario.
     * Facilita la clasificación y priorización del ticket.
     * </p>
     */
    @Schema(description = "Asunto del ticket")
    private String asunto;

    /**
     * Mensaje detallado del ticket.
     * <p>
     * Descripción completa del problema, consulta o sugerencia
     * proporcionada por el usuario. Contiene toda la información
     * necesaria para entender y resolver la incidencia.
     * </p>
     */
    @Schema(description = "Mensaje del ticket")
    private String mensaje;

    /**
     * Estado actual del ticket.
     * <p>
     * Indica el status actual en el flujo de gestión
     * de soporte. Se utiliza para seguimiento y para mostrar
     * información relevante al usuario y al equipo de soporte.
     * </p>
     * 
     * <p>
     * <b>Transiciones típicas:</b>
     * ABIERTO → EN_PROGRESO → RESUELTO/CERRADO
     * </p>
     */
    @Schema(description = "Estado del ticket (e.g., abierto, cerrado)")
    private String estado;

    /**
     * Fecha y hora en que se resolvió el ticket.
     * <p>
     * Timestamp que registra cuándo se solucionó el problema
     * o se completó la gestión del ticket. Es null si el ticket
     * todavía está abierto o en proceso.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> ISO 8601 (yyyy-MM-ddTHH:mm:ss)
     * <b>Uso:</b> Para cálculos de SLA y métricas de rendimiento
     * </p>
     */
    @Schema(description = "Fecha en la que se resolvió el ticket")
    private LocalDateTime fechaResolucion;

    /**
     * Fecha y hora en que se creó el ticket.
     * <p>
     * Timestamp automático que registra cuándo se generó la incidencia.
     * Es fundamental para seguimiento y cálculo de tiempos
     * de respuesta y resolución.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> ISO 8601 (yyyy-MM-ddTHH:mm:ss)
     * <b>Uso:</b> Para análisis de tendencias y métricas de soporte
     * </p>
     */
    @Schema(description = "Fecha en la que se creó el ticket")
    private LocalDateTime fechaCreacion;

    /**
     * Nombre de usuario del creador del ticket.
     * <p>
     * Identificador único del usuario que creó el ticket.
     * Se utiliza para comunicación y para identificar al responsable
     * de la incidencia en el sistema.
     * </p>
     */
    @Schema(description = "Nombre de usuario del creador del ticket")
    private String username;

    /**
     * Identificador único del usuario creador.
     * <p>
     * ID numérico del usuario en la base de datos.
     * Permite establecer relaciones foreign key y realizar
     * consultas adicionales sobre el usuario si es necesario.
     * </p>
     */
    @Schema(description = "Identificador único del usuario")
    private Long usuarioId;

    /**
     * Correo electrónico del usuario creador.
     * <p>
     * Email del usuario que creó el ticket.
     * Se utiliza para notificaciones y comunicación directa
     * con el usuario sobre actualizaciones del ticket.
     * </p>
     * 
     * <p>
     * <b>Privacidad:</b> Solo debe mostrarse a usuarios
     * con permisos apropiados (administradores, soporte).
     * </p>
     */
    @Schema(description = "Email del usuario")
    private String usuarioEmail;

    /**
     * Nombre completo del usuario creador.
     * <p>
     * Nombre y apellidos del usuario que creó el ticket.
     * Se utiliza para comunicación personalizada y para identificar
     * al responsable de forma más amigable que el username.
     * </p>
     */
    @Schema(description = "Nombre completo del usuario")
    private String nombreCompletoUsuario;

}
