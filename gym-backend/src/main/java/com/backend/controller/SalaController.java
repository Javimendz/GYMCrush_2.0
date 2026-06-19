package com.backend.controller;

import com.backend.dto.SalaRequestDto;
import com.backend.dto.SalaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ISalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de salas del gimnasio.
 * * @author Backend Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/salas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Salas", description = "Endpoints para la gestión de espacios físicos e instalaciones")
public class SalaController {

    private final ISalaService salaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @Operation(summary = "Listar todas las salas", description = "Retorna el listado completo incluyendo inactivas")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<ApiResponseDto<List<SalaResponseDto>>> listarTodas() {
        log.info("Solicitud para listar todas las salas");
        List<SalaResponseDto> salas = salaService.listarTodas();
        
        return ResponseEntity.ok(ApiResponseDto.<List<SalaResponseDto>>builder()
                .mensaje("Listado de todas las salas recuperado")
                .success(true)
                .datos(salas)
                .build());
    }

    @GetMapping("/activas")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @Operation(summary = "Listar salas operativas", description = "Retorna solo las salas disponibles para reservar clases")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<ApiResponseDto<List<SalaResponseDto>>> listarActivas() {
        log.info("Solicitud para listar solo las salas activas");
        List<SalaResponseDto> salas = salaService.listarActivas();
        
        return ResponseEntity.ok(ApiResponseDto.<List<SalaResponseDto>>builder()
                .mensaje("Listado de salas activas recuperado")
                .success(true)
                .datos(salas)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @Operation(summary = "Obtener detalle de una sala")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<ApiResponseDto<SalaResponseDto>> obtenerPorId(
            @PathVariable("id") Long id) { 
            
        log.info("Solicitud para obtener la sala con ID: {}", id);
        SalaResponseDto sala = salaService.obtenerPorId(id);
        
        return ResponseEntity.ok(ApiResponseDto.<SalaResponseDto>builder()
                .mensaje("Detalles de la sala recuperados")
                .success(true)
                .datos(sala)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar una nueva sala", description = "El nombre de la sala debe ser único")
    @ApiResponse(responseCode = "201", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<ApiResponseDto<SalaResponseDto>> crear(
            @Valid @RequestBody SalaRequestDto dto) {
            
        log.info("Solicitud para crear nueva sala: {}", dto.getNombre());
        SalaResponseDto nuevaSala = salaService.crear(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDto.<SalaResponseDto>builder()
                        .mensaje("Sala registrada exitosamente")
                        .success(true)
                        .datos(nuevaSala)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar datos de una sala")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<ApiResponseDto<SalaResponseDto>> actualizar(
            @PathVariable("id") Long id, 
            @Valid @RequestBody SalaRequestDto dto) {
            
        log.info("Solicitud para actualizar la sala con ID: {}", id);
        SalaResponseDto salaActualizada = salaService.actualizar(id, dto);
        
        return ResponseEntity.ok(ApiResponseDto.<SalaResponseDto>builder()
                .mensaje("Sala actualizada exitosamente")
                .success(true)
                .datos(salaActualizada)
                .build());
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar estado de la sala", description = "Activa o desactiva la sala (ej: por mantenimiento)")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<ApiResponseDto<Void>> cambiarEstado(
            @PathVariable("id") Long id, 
            @RequestParam("activa") Boolean activa) { 
            
        log.info("Solicitud para cambiar estado de la sala ID: {} a activa={}", id, activa);
        salaService.cambiarEstado(id, activa);
        
        String mensajeEstado = activa ? "activada" : "desactivada";
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("La sala ha sido " + mensajeEstado + " exitosamente")
                .success(true)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una sala físicamente", description = "Solo permitido si la sala no tiene horarios asignados")
    @ApiResponse(responseCode = "204", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(
            @PathVariable("id") Long id) { 
            
        log.info("Solicitud para eliminar la sala con ID: {}", id);
        salaService.eliminar(id);
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Sala eliminada correctamente")
                .success(true)
                .build());
    }
}