package com.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.enums.EnumEstadoTicket;
import com.backend.dto.TicketRequestDto;
import com.backend.dto.TicketResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ITicketService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para gestionar los tickets de soporte al usuario.
 * <p>
 * Este controlador proporciona endpoints para:
 * <ul>
 *   <li>Crear tickets de soporte</li>
 *   <li>Ver historial de tickets de un usuario</li>
 *   <li>Ver todos los tickets (ADMIN)</li>
 *   <li>Actualizar estado de tickets (ADMIN)</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Ticket", description = "Operaciones relacionadas con los tickets de soporte")
public class TicketController {
    
    private final ITicketService ticketService;

    /**
     * Crea un nuevo ticket de soporte para un usuario.
     * <p>
     * Cualquier usuario autenticado (USUARIO o ADMIN) puede crear tickets.
     * </p>
     *
     * @param usuarioId el ID del usuario que crea el ticket
     * @param dto los datos del ticket a crear
     * @return ResponseEntity con el ticket creado y mensaje de confirmación
     */
    @PostMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear ticket", description = "Permite a un usuario crear un nuevo ticket de soporte")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket creado exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<TicketResponseDto>> crear(
            @PathVariable("usuarioId") Long usuarioId,
            @Valid @RequestBody TicketRequestDto dto) {
            
        log.info("Creando ticket para usuario ID: {}", usuarioId);
        TicketResponseDto nuevoTicket = ticketService.crearTicket(dto, usuarioId);
        log.info("Ticket creado exitosamente con ID: {}", nuevoTicket.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.<TicketResponseDto>builder()
                .mensaje("Ticket creado exitosamente. Revisaremos tu mensaje pronto.")
                .success(true)
                .datos(nuevoTicket) 
                .build());
    }

    /**
     * Obtiene el historial de tickets de un usuario específico.
     *
     * @param usuarioId el ID del usuario cuyos tickets se quieren consultar
     * @return ResponseEntity con la lista de tickets del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Ver mis tickets", description = "Permite a un usuario ver su historial de tickets de soporte")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial de tickets recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró historial para el usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<TicketResponseDto>>> verMisTickets(
            @PathVariable("usuarioId") Long usuarioId) {
            
        List<TicketResponseDto> lista = ticketService.obtenerPorUsuario(usuarioId);

        return ResponseEntity.ok(ApiResponseDto.<List<TicketResponseDto>>builder()
                .mensaje("Historial de tickets recuperado")
                .success(true)
                .datos(lista)
                .build());
    }

    /**
     * Obtiene todos los tickets del sistema (solo administradores).
     * <p>
     * Endpoint exclusivo para usuarios con rol ADMIN para ver el panel de administración.
     * </p>
     *
     * @return ResponseEntity con la lista de todos los tickets
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Ver todos los tickets", description = "Permite al administrador ver todos los tickets de soporte")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Todos los tickets recuperados con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<TicketResponseDto>>> verTodos() {
        List<TicketResponseDto> todos = ticketService.obtenerTodos();

        return ResponseEntity.ok(ApiResponseDto.<List<TicketResponseDto>>builder()
                .mensaje("Panel de administración: Todos los tickets")
                .success(true)
                .datos(todos)
                .build());
    }

    /**
     * Actualiza el estado de un ticket (solo administradores).
     * <p>
     * Permite al administrador cambiar el estado de un ticket (PENDIENTE, EN_PROCESO, RESUELTO, CERRADO).
     * </p>
     *
     * @param id el ID del ticket a actualizar
     * @param nuevoEstado el nuevo estado del ticket
     * @return ResponseEntity con el ticket actualizado
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Resolver ticket", description = "Permite al administrador resolver un ticket de soporte")       
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket resuelto exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<TicketResponseDto>> resolverTicket(
        @PathVariable("id") Long id,
        @RequestParam EnumEstadoTicket nuevoEstado
    ){
        TicketResponseDto actualizado = ticketService.actualizarEstado(id, nuevoEstado);

        return ResponseEntity.ok(ApiResponseDto.<TicketResponseDto>builder()
            .mensaje("Estado del ticket actualizado a: " + nuevoEstado)
            .success(true)
            .datos(actualizado)
            .build()
    );
    }
}
