package com.backend.service;

import java.util.List;
import com.backend.domain.enums.EnumEstadoTicket;
import com.backend.dto.TicketRequestDto;
import com.backend.dto.TicketResponseDto;

/**
 * Interfaz del servicio para la gestión de tickets de soporte técnico.
 * <p>
 * Define los contratos para las operaciones de tickets, incluyendo
 * creación, consulta, actualización de estados y eliminación.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public interface ITicketService {

    /**
     * Crea un nuevo ticket de soporte para un usuario.
     * <p>
     * Registra una incidencia o duda con estado inicial ABIERTO
     * y la asocia al usuario que reporta el problema.
     * </p>
     *
     * @param dto       DTO con los datos del ticket (asunto, mensaje)
     * @param usuarioId ID del usuario que crea el ticket
     * @return DTO con los datos del ticket creado incluyendo su ID
     * @throws ResourceNotFoundException si el usuario no existe
     */
    TicketResponseDto crearTicket(TicketRequestDto dto, Long usuarioId);

    /**
     * Obtiene el listado de tickets de un usuario específico.
     * <p>
     * Retorna todos los tickets creados por un usuario,
     * ordenados por fecha de creación descendente.
     * </p>
     *
     * @param usuarioId ID del usuario del cual se desean obtener los tickets
     * @return Lista de DTOs con los tickets del usuario
     */
    List<TicketResponseDto> obtenerPorUsuario(Long usuarioId);

    /**
     * Obtiene todos los tickets del sistema.
     * <p>
     * Retorna una lista completa de todos los tickets registrados,
     * útil para fines administrativos y estadísticas.
     * </p>
     *
     * @return Lista de DTOs con todos los tickets del sistema
     */
    List<TicketResponseDto> obtenerTodos();

    /**
     * Busca un ticket específico por su identificador único.
     * <p>
     * Retorna el ticket con el ID especificado incluyendo su estado
     * actual e historial de cambios.
     * </p>
     *
     * @param ticketId Identificador único del ticket a buscar
     * @return DTO con los datos completos del ticket
     * @throws ResourceNotFoundException si el ticket no existe
     */
    TicketResponseDto obtenerPorId(Long ticketId);

    /**
     * Actualiza el estado de un ticket existente.
     * <p>
     * Cambia el estado del ticket (ej: ABIERTO → EN_PROCESO → RESUELTO),
     * útil para seguimiento del ciclo de vida del ticket.
     * </p>
     *
     * @param ticketId    Identificador único del ticket a actualizar
     * @param nuevoEstado Nuevo estado a asignar al ticket
     * @return DTO con los datos actualizados del ticket
     * @throws ResourceNotFoundException si el ticket no existe
     */
    TicketResponseDto actualizarEstado(Long ticketId, EnumEstadoTicket nuevoEstado);

    /**
     * Elimina un ticket del sistema por su identificador.
     * <p>
     * Verifica que el ticket exista antes de proceder a su eliminación.
     * </p>
     *
     * @param ticketId Identificador único del ticket a eliminar
     * @throws ResourceNotFoundException si el ticket no existe
     */
    void eliminarTicket(Long ticketId);

}
