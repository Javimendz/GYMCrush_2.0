package com.backend.controller;

import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IEntrenamientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entrenamientos")
@RequiredArgsConstructor
@Slf4j // Habilitamos los logs
@Tag(name = "Entrenamientos", description = "Gestión de sesiones de ejercicio programadas")
public class EntrenamientoController {

        private final IEntrenamientoService entrenamientoService;

       @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Crear una nueva sesión de entrenamiento")
    public ResponseEntity<ApiResponseDto<EntrenamientoResponseDto>> crear(
            @Valid @RequestBody EntrenamientoRequestDto dto,
            Authentication authentication) {
        
        log.info("Creando entrenamiento para: {}", authentication.getName());
        EntrenamientoResponseDto nuevo = entrenamientoService.crear(dto, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDto.<EntrenamientoResponseDto>builder()
                        .mensaje("Sesión creada con éxito")
                        .success(true)
                        .datos(nuevo)
                        .build());
    }

    // 2. LISTAR POR USUARIO (Este es el que tenías duplicado)
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Listar entrenamientos del usuario y globales")
    public ResponseEntity<ApiResponseDto<List<EntrenamientoResponseDto>>> listarPorUsuario(
            @PathVariable("usuarioId") Long usuarioId) {
            
        log.info("Listando entrenamientos filtrados para usuario: {}", usuarioId);
        List<EntrenamientoResponseDto> data = entrenamientoService.listarPorUsuario(usuarioId);

        return ResponseEntity.ok(
                ApiResponseDto.<List<EntrenamientoResponseDto>>builder()
                        .mensaje("Entrenamientos recuperados")
                        .success(true)
                        .datos(data)
                        .build());
    }

    // 3. LISTAR TODOS (Vista general, usualmente para el Admin)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponseDto<List<EntrenamientoResponseDto>>> listarTodos() {
        List<EntrenamientoResponseDto> data = entrenamientoService.listarTodos();
        return ResponseEntity.ok(ApiResponseDto.<List<EntrenamientoResponseDto>>builder()
                .mensaje("Catálogo completo").success(true).datos(data).build());
    }

    // 4. OBTENER POR ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponseDto<EntrenamientoResponseDto>> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponseDto.<EntrenamientoResponseDto>builder()
                .datos(entrenamientoService.obtenerPorId(id)).success(true).build());
    }

    // 5. ACTUALIZAR
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponseDto<EntrenamientoResponseDto>> actualizar(
            @PathVariable("id") Long id, @Valid @RequestBody EntrenamientoRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.<EntrenamientoResponseDto>builder()
                .datos(entrenamientoService.actualizar(id, dto)).success(true).build());
    }

    // 6. ELIMINAR
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable("id") Long id) {
        entrenamientoService.eliminar(id);
        return ResponseEntity.ok(ApiResponseDto.<Void>builder().mensaje("Eliminado").success(true).build());
    }
}