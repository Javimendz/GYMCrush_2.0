package com.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.backend.repository.NotificacionRepository; 
import com.backend.service.NotificacionService;
import com.backend.dto.NotificacionRequestDto;

import com.backend.domain.enums.EnumEstadoTicket;
import com.backend.dto.TicketRequestDto;
import com.backend.dto.TicketResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.TicketMapper;
import com.backend.repository.TicketRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.domain.Ticket;
import com.backend.domain.Usuario;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.backend.domain.enums.TipoNotificacion;
/**
 * Implementación del servicio para la gestión de tickets de soporte técnico.
 * <p>
 * Esta clase maneja la creación, consulta y resolución de tickets
 * de soporte para incidencias y dudas de los usuarios del gimnasio.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Service
@Data
@Slf4j
public class TicketServiceImp implements ITicketService {

    /** Repositorio para el acceso a datos de tickets */
    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final TicketMapper ticketMapper;

   private final NotificacionRepository notificacionRepository; 
    private final NotificacionService notificacionService;
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
    @Override
    public TicketResponseDto crearTicket(TicketRequestDto dto, Long usuarioId) {
        log.info("Creando ticket para usuario ID: {}", usuarioId);

        // Buscar al usuario o lanzar excepción si no existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        log.debug("Usuario encontrado: {}", usuario.getUsername());

        // Crear el ticket con estado ABIERTO
        Ticket ticket = Ticket.builder()
                .asunto(dto.getAsunto())
                .mensaje(dto.getMensaje())
                .usuario(usuario)
                .estado(EnumEstadoTicket.ABIERTO)
                .build();

        // Guardar el ticket y convertir a DTO para respuesta
        TicketResponseDto resultado = ticketMapper.toDto(ticketRepository.save(ticket));
        log.info("Ticket creado exitosamente con ID: {}", resultado.getId());
        return resultado;
    }

    /**
     * Obtiene todos los tickets creados por un usuario específico.
     * <p>
     * Retorna una lista de tickets ordenados por fecha de creación
     * de más reciente a más antigua.
     * </p>
     *
     * @param usuarioId ID del usuario del cual se desean obtener los tickets
     * @return Lista de DTOs con los tickets del usuario
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Override
    public List<TicketResponseDto> obtenerPorUsuario(Long usuarioId) {
        log.info("Obteniendo tickets para usuario ID: {}", usuarioId);
        List<TicketResponseDto> tickets = ticketRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Total de tickets encontrados para usuario {}: {}", usuarioId, tickets.size());
        return tickets;
    }

    /**
     * Obtiene todos los tickets existentes en el sistema.
     * <p>
     * Retorna una lista de todos los tickets ordenados por fecha de creación
     * de más reciente a más antigua.
     * </p>
     *
     * @return Lista de DTOs con todos los tickets
     */
    @Override
    public List<TicketResponseDto> obtenerTodos() {
        log.info("Obteniendo todos los tickets");
        List<TicketResponseDto> tickets = ticketRepository.findAll()
                .stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Total de tickets encontrados: {}", tickets.size());
        return tickets;
    }

    /**
     * Obtiene un ticket específico por su ID.
     * <p>
     * Retorna el ticket con todos sus datos incluyendo el usuario que lo creó.
     * </p>
     *
     * @param ticketId ID del ticket a obtener
     * @return DTO con los datos del ticket
     * @throws ResourceNotFoundException si el ticket no existe
     */
    @Override
    public TicketResponseDto obtenerPorId(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + ticketId));
        return mapToDto(ticket);

    }

    /**
     * Actualiza el estado de un ticket existente.
     * <p>
     * Permite cambiar el estado de un ticket a ABIERTO, EN_PROCESO o CERRADO.
     * Cuando se cierra un ticket, se registra la fecha de resolución.
     * </p>
     *
     * @param ticketId    ID del ticket a actualizar
     * @param nuevoEstado Nuevo estado del ticket
     * @return DTO con los datos actualizados del ticket
     * @throws ResourceNotFoundException si el ticket no existe
     */
    @Override
    public TicketResponseDto actualizarEstado(Long ticketId, EnumEstadoTicket nuevoEstado) {

    Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado"));

    //  Evitar procesar si el estado ya es el mismo
    if (ticket.getEstado().equals(nuevoEstado)) {
        return ticketMapper.toDto(ticket); 
    }

    ticket.setEstado(nuevoEstado);
   if (EnumEstadoTicket.RESUELTO.equals(nuevoEstado)) {
        ticket.setFechaResolucion(LocalDateTime.now());
        
boolean existeNotif = notificacionRepository.existsByTicket_IdAndTipo(ticketId, TipoNotificacion.RESOLUCION);
        if (!existeNotif) {
            notificacionService.crear(NotificacionRequestDto.builder()
                .usuarioId(ticket.getUsuario().getId())
                .titulo("Ticket Resuelto")
                .mensaje("Tu ticket #" + ticketId + " ha sido marcado como resuelto.")
                .build());
        }
    }

    return ticketMapper.toDto(ticketRepository.save(ticket));
}
    /**
     * Elimina un ticket existente del sistema.
     * <p>
     * Verifica que el ticket exista antes de eliminarlo.
     * </p>
     *
     * @param ticketId ID del ticket a eliminar
     * @throws ResourceNotFoundException si el ticket no existe
     */
    @Override
    public void eliminarTicket(Long ticketId) {

        if (!ticketRepository.existsById(ticketId))
            throw new ResourceNotFoundException("No se puede eliminar el ticket, no existe");

        ticketRepository.deleteById(ticketId);

    }

    /**
     * Metodo privado para mapear a dto
     * <p>
     * Convierte un objeto Ticket a su representación DTO.
     * </p>
     *
     * @param t Objeto Ticket a mapear
     * @return DTO con los datos del ticket
     */
    private TicketResponseDto mapToDto(Ticket t) {

        return TicketResponseDto.builder()
                .id(t.getId())
                .asunto(t.getAsunto())
                .mensaje(t.getMensaje())
                .estado(t.getEstado().name())
                .fechaCreacion(t.getFechaCreacion())
                .fechaResolucion(t.getFechaResolucion())
                .username(t.getUsuario().getUsername())
                .build();

    }

}
