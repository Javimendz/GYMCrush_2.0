package com.backend.controller;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.HorarioRequestDto; // Nuevo DTO de entrada
import com.backend.dto.HorarioResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IHorarioService;

/**
 * Controlador REST para gestionar los horarios de actividades del gimnasio.
 * <p>
 * Este controlador proporciona endpoints para el CRUD de horarios:
 * <ul>
 * <li>Listar todos los horarios</li>
 * <li>Listar horarios por día de la semana</li>
 * <li>Obtener horario por ID</li>
 * <li>Crear nuevo horario (ADMIN)</li>
 * <li>Actualizar horario (ADMIN)</li>
 * <li>Eliminar horario (ADMIN)</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/horarios")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Horario", description = "Operaciones relacionadas con los horarios")
public class HorarioController {

    private final IHorarioService horarioService;

    /**
     * Lista todos los horarios disponibles en el sistema.
     *
     * @return ResponseEntity con la lista de todos los horarios
     */
    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todos los horarios")
    public ResponseEntity<ApiResponseDto<List<HorarioResponseDto>>> listarTodos() {
        log.info("Listando todos los horarios");
        List<HorarioResponseDto> horarios = horarioService.listarTodos();

        return ResponseEntity.ok(ApiResponseDto.<List<HorarioResponseDto>>builder()
                .mensaje("Listado de horarios recuperado con éxito")
                .success(true)
                .datos(horarios)
                .build());
    }

    /**
     * Lista los horarios disponibles para un día específico de la semana.
     *
     * @param dia el día de la semana (LUNES, MARTES, MIERCOLES, etc.)
     * @return ResponseEntity con la lista de horarios para ese día
     */
    @GetMapping("/dia/{dia}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar horarios por día", description = "Obtiene una lista de horarios disponibles para un día específico")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de horarios recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron horarios para el día especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<HorarioResponseDto>>> listarPorDia(@PathVariable("dia") String dia) {
        log.info("Listando horarios por día: {}", dia);
        List<HorarioResponseDto> horarios = horarioService.listarPorDia(dia);
        log.debug("Horarios encontrados para día '{}': {}", dia, horarios.size());
        return ResponseEntity.ok(ApiResponseDto.<List<HorarioResponseDto>>builder()
                .mensaje("Horarios del día " + dia + " recuperados")
                .success(true)
                .datos(horarios)
                .build());
    }

    /**
     * Obtiene los detalles de un horario específico por su ID.
     *
     * @param id el ID del horario a buscar
     * @return ResponseEntity con los datos del horario encontrado
     */
    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener horario por ID", description = "Obtiene los detalles de un horario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Horario recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Horario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<HorarioResponseDto>> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Obteniendo horario por ID: {}", id);
        HorarioResponseDto horario = horarioService.obtenerPorId(id);
        log.debug("Horario encontrado: {} - {}", horario.getDiaSemana(), horario.getHoraInicio());
        return ResponseEntity.ok(ApiResponseDto.<HorarioResponseDto>builder()
                .mensaje("Horario recuperado con éxito")
                .success(true)
                .datos(horario)
                .build());
    }

    /**
     * Crea un nuevo horario en el sistema.
     * <p>
     * Solo los usuarios con rol ADMIN pueden crear horarios.
     * </p>
     *
     * @param dto los datos del horario a crear
     * @return ResponseEntity con el horario creado y estado 201 (CREATED)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear horario", description = "Crea un nuevo horario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Horario creado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<HorarioResponseDto>> crear(@Valid @RequestBody HorarioRequestDto dto) {
        HorarioResponseDto nuevoHorario = horarioService.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.<HorarioResponseDto>builder()
                .mensaje("Horario creado exitosamente")
                .success(true)
                .datos(nuevoHorario)
                .build());
    }

    /**
     * Actualiza un horario existente.
     * <p>
     * Solo los usuarios con rol ADMIN pueden actualizar horarios.
     * </p>
     *
     * @param id  el ID del horario a actualizar
     * @param dto los nuevos datos del horario
     * @return ResponseEntity con el horario actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar horario", description = "Actualiza los detalles de un horario existente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Horario actualizado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Horario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<HorarioResponseDto>> actualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody HorarioRequestDto dto) {

        HorarioResponseDto horarioActualizado = horarioService.actualizar(id, dto);

        return ResponseEntity.ok(ApiResponseDto.<HorarioResponseDto>builder()
                .mensaje("Horario actualizado exitosamente")
                .success(true)
                .datos(horarioActualizado)
                .build());
    }

    /**
     * Elimina un horario del sistema.
     * <p>
     * Solo los usuarios con rol ADMIN pueden eliminar horarios.
     * </p>
     *
     * @param id el ID del horario a eliminar
     * @return ResponseEntity con confirmación de eliminación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar horario", description = "Elimina un horario existente por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Horario eliminado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Horario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable("id") Long id) {
        horarioService.eliminar(id);

        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("El horario ha sido eliminado correctamente")
                .success(true)
                .datos(null)
                .build());
    }
}