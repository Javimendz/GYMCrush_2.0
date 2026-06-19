package com.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.backend.dto.SaludRequestDto;
import com.backend.dto.SaludResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ISaludService;

import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar el progreso de salud de los usuarios.
 * <p>
 * Este controlador proporciona endpoints para:
 * <ul>
 *   <li>Registrar progreso de salud (peso, estatura, IMC, etc.)</li>
 *   <li>Obtener el progreso más reciente de un usuario</li>
 *   <li>Consultar el historial completo de progreso de salud</li>
 *   <li>Eliminar registros de progreso</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/salud")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Salud", description = "Operaciones relacionadas con la salud de los usuarios")
public class SaludController {

    private final ISaludService saludService;

    /**
     * Registra un nuevo progreso de salud para un usuario.
     * <p>
     * Permite a usuarios ADMIN o USUARIO registrar métricas de salud como peso,
     * estatura, IMC y nivel de actividad física.
     * </p>
     *
     * @param id el ID del usuario
     * @param dto los datos de salud a registrar
     * @return ResponseEntity con el registro de salud creado
     */
    @PostMapping("/usuario/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Registrar progreso de salud", description = "Permite a un usuario registrar su progreso de salud")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Progreso registrado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida, posiblemente datos incompletos")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<SaludResponseDto>> crearRegistro(
            @PathVariable("id") Long id, 
            @Valid @RequestBody SaludRequestDto dto) {
            
        log.info("Registrando progreso de salud para usuario ID: {}", id);
        SaludResponseDto resultado = saludService.registrarProgreso(dto, id);
        log.info("Progreso de salud registrado exitosamente con ID: {}", resultado.getId());
        
        // CORRECCIÓN: Envuelto en ApiResponseDto y cambiado a 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDto.<SaludResponseDto>builder()
                        .mensaje("Progreso de salud registrado exitosamente")
                        .success(true)
                        .datos(resultado)
                        .build()
        );
    }

    /**
     * Obtiene el registro de progreso de salud más reciente de un usuario.
     *
     * @param id el ID del usuario
     * @return ResponseEntity con el último registro de salud del usuario
     */
    @GetMapping("/usuario/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener progreso actual", description = "Permite a un usuario obtener su progreso de salud más reciente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Progreso actual recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró progreso para el usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<SaludResponseDto>> obtenerProgresoActual(
            @PathVariable("id") Long id) { 
            
        log.info("Obteniendo progreso actual para usuario ID: {}", id);
        SaludResponseDto progreso = saludService.obtenerProgresoActual(id);
        log.debug("Progreso encontrado para usuario {}: IMC={}, Peso={}", id, progreso.getImc(), progreso.getPeso());
        
        
        return ResponseEntity.ok(
                ApiResponseDto.<SaludResponseDto>builder()
                        .mensaje("Progreso de salud actual recuperado")
                        .success(true)
                        .datos(progreso)
                        .build()
        );
    }

    /**
     * Obtiene el historial completo de progreso de salud de un usuario.
     *
     * @param id el ID del usuario
     * @return ResponseEntity con la lista completa de registros de salud
     */
    @GetMapping("/usuario/{id}/historial")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener historial de salud", description = "Permite a un usuario obtener su historial de progreso de salud")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró historial para el usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<SaludResponseDto>>> getHistorial(@PathVariable("id") Long id) {
        List<SaludResponseDto> historial = saludService.obtenerHistorialPorUsuario(id);

        ApiResponseDto<List<SaludResponseDto>> respuesta = ApiResponseDto.<List<SaludResponseDto>>builder()
                .mensaje("Historial de salud recuperado con éxito")
                .datos(historial)
                .success(true)
                .build();

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Elimina el registro de progreso de salud más reciente de un usuario.
     *
     * @param id el ID del usuario cuyo último registro de salud será eliminado
     * @return ResponseEntity con confirmación de eliminación
     */
    @DeleteMapping("/usuario/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar progreso de salud", description = "Permite a un usuario eliminar su progreso de salud más reciente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Progreso eliminado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró progreso para el usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable("id") Long id) {
       saludService.eliminarRegistro(id);

    ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
            .mensaje("Registro de salud eliminado correctamente")
            .datos(null)
            .success(true)
            .build();

    return ResponseEntity.ok(respuesta);
    }
}
