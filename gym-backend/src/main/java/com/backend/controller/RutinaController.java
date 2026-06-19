package com.backend.controller;

import com.backend.dto.RutinaRequestDto;
import com.backend.dto.RutinaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IRutinaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rutinas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rutinas", description = "Endpoints para la gestión y seguimiento de rutinas de entrenamiento")
public class RutinaController {

    private final IRutinaService rutinaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asignar entrenamiento", description = "Un administrador asigna un ejercicio a la agenda de un usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Entrenamiento asignado correctamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Entrada inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<RutinaResponseDto>> asignar(@Valid @RequestBody RutinaRequestDto dto) {
        RutinaResponseDto response = rutinaService.asignarEntrenamiento(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Entrenamiento asignado correctamente", response));
    }


    @GetMapping("/usuario/{usuarioId}/rango")
public ResponseEntity<List<RutinaResponseDto>> obtenerRutinasPorRango(
        @PathVariable Long usuarioId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
    
    // Llama a tu service para que use el método 'Between' que acabamos de crear
    List<RutinaResponseDto> rutinas = rutinaService.obtenerPorRango(usuarioId, inicio, fin);
    return ResponseEntity.ok(rutinas);
}
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Obtener rutina diaria", description = "Obtiene los ejercicios programados para un usuario en una fecha específica")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rutina obtenida con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<RutinaResponseDto>>> obtenerRutina(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        LocalDate fechaBusqueda = (fecha != null) ? fecha : LocalDate.now();
        List<RutinaResponseDto> rutina = rutinaService.obtenerRutinaDiaria(usuarioId, fechaBusqueda);
        
        return ResponseEntity.ok(ApiResponseDto.success("Rutina obtenida con éxito", rutina));
    }

    @PatchMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Marcar como completada", description = "El usuario marca un ejercicio de su rutina como realizado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ejercicio marcado como completado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<RutinaResponseDto>> completar(@PathVariable Long id) {
        RutinaResponseDto response = rutinaService.marcarComoCompletada(id);
        return ResponseEntity.ok(ApiResponseDto.success("¡Buen trabajo! Ejercicio completado", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar asignación", description = "Elimina un ejercicio programado de la rutina")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Asignación eliminada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        rutinaService.eliminarAsignacion(id);
        return ResponseEntity.ok(ApiResponseDto.success("Asignación eliminada", null));
    }
}