package com.backend.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.ActividadRequestDto;
import com.backend.dto.ActividadResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IActividadService;

import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar las actividades del gimnasio.
 * <p>
 * Este controlador proporciona endpoints para el CRUD de actividades y búsquedas específicas:
 * <ul>
 *   <li>Listar todas las actividades</li>
 *   <li>Obtener actividad por ID</li>
 *   <li>Crear nueva actividad (ADMIN)</li>
 *   <li>Actualizar actividad (ADMIN)</li>
 *   <li>Eliminar actividad (ADMIN)</li>
 *   <li>Buscar actividades por nombre</li>
 *   <li>Listar actividades por día</li>
 *   <li>Filtrar actividades por precio máximo</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/actividades")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Actividad", description = "Operaciones relacionadas con las actividades")
public class ActividadController {

    private final IActividadService actividadService;
    /**
     * Lista todas las actividades disponibles en el sistema.
     *
     * @return ResponseEntity con la lista de todas las actividades
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todas las actividades", description = "Obtiene una lista de todas las actividades disponibles")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de actividades recuperada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para ver las actividades")
    public ResponseEntity<ApiResponseDto<List<ActividadResponseDto>>> listarTodas() {
        log.info("Solicitud de listar todas las actividades");
        List<ActividadResponseDto> actividades = actividadService.listarTodos();
        
        return ResponseEntity.ok(ApiResponseDto.<List<ActividadResponseDto>>builder()
                .mensaje("Lista de actividades recuperada con éxito")
                .success(true)
                .datos(actividades)
                .build());
    }

    /**
     * Obtiene los detalles de una actividad específica por su ID.
     *
     * @param id el ID de la actividad a buscar
     * @return ResponseEntity con los datos de la actividad encontrada
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener actividad por ID", description = "Obtiene los detalles de una actividad específica por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actividad recuperada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró actividad con el ID especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para ver la actividad") 
   public ResponseEntity<ApiResponseDto<ActividadResponseDto>> obtenerPorId(@PathVariable("id") Long id) { // <-- CORREGIDO
        log.info("Solicitud de obtener actividad por ID: {}", id);
        ActividadResponseDto actividad = actividadService.buscarPorId(id);
        
        return ResponseEntity.ok(ApiResponseDto.<ActividadResponseDto>builder()
                .mensaje("Actividad recuperada con éxito")
                .success(true)
                .datos(actividad)
                .build());
    }

    /**
     * Crea una nueva actividad en el sistema.
     * <p>
     * Solo los usuarios con rol ADMIN pueden crear actividades.
     * </p>
     *
     * @param dto los datos de la actividad a crear
     * @return ResponseEntity con la actividad creada y estado 201 (CREATED)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear actividad", description = "Crea una nueva actividad")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Actividad creada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida. Verifique los datos proporcionados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para crear una actividad") 
   public ResponseEntity<ApiResponseDto<ActividadResponseDto>> crear(@Valid @RequestBody ActividadRequestDto dto) {
        log.info("Solicitud de crear actividad: {}", dto.getNombre());
        ActividadResponseDto nuevaActividad = actividadService.crear(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.<ActividadResponseDto>builder()
                .mensaje("Actividad creada con éxito")
                .success(true)
                .datos(nuevaActividad)
                .build());
    }

    /**
     * Actualiza una actividad existente.
     * <p>
     * Solo los usuarios con rol ADMIN pueden actualizar actividades.
     * </p>
     *
     * @param id el ID de la actividad a actualizar
     * @param dto los nuevos datos de la actividad
     * @return ResponseEntity con la actividad actualizada
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar actividad", description = "Actualiza los detalles de una actividad existente por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actividad actualizada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida. Verifique los datos proporcionados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró actividad con el ID especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para actualizar la actividad") 
    public ResponseEntity<ApiResponseDto<ActividadResponseDto>> actualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody ActividadRequestDto dto) {
        log.info("Solicitud de actualizar actividad por ID: {}", id);
        ActividadResponseDto actividadActualizada = actividadService.actualizar(id, dto);
        
        return ResponseEntity.ok(ApiResponseDto.<ActividadResponseDto>builder()
                .mensaje("Actividad actualizada con éxito")
                .success(true)
                .datos(actividadActualizada)
                .build());
    }

    /**
     * Elimina una actividad del sistema.
     * <p>
     * Solo los usuarios con rol ADMIN pueden eliminar actividades.
     * Retorna estado 204 (No Content) al eliminar exitosamente.
     * </p>
     *
     * @param id el ID de la actividad a eliminar
     * @return ResponseEntity con estado 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar actividad", description = "Elimina una actividad existente por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Actividad eliminada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró actividad con el ID especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para eliminar la actividad") 
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable("id") Long id) { 
        log.info("Solicitud de eliminar actividad por ID: {}", id);
        actividadService.eliminar(id);
        
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Actividad eliminada con éxito")
                .success(true)
                .datos(null)
                .build());
    }

    /**
     * Busca actividades por nombre.
     * <p>
     * Endpoint de búsqueda que filtra actividades por su nombre.
     * Ejemplo: /api/v1/actividades/buscar?nombre=zumba
     * </p>
     *
     * @param nombre el nombre de la actividad a buscar
     * @return ResponseEntity con la lista de actividades que coinciden con el nombre
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Buscar actividad por nombre", description = "Busca actividades por su nombre")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actividades recuperadas con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron actividades con el nombre especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para buscar actividades por nombre") 
    public ResponseEntity<ApiResponseDto<List<ActividadResponseDto>>> buscarPorNombre(
            @RequestParam("nombre") String nombre) { 
        log.info("Solicitud de buscar actividad por nombre: {}", nombre);
        List<ActividadResponseDto> actividades = actividadService.buscarPorNombre(nombre);
        
        return ResponseEntity.ok(ApiResponseDto.<List<ActividadResponseDto>>builder()
                .mensaje("Resultados de la búsqueda recuperados")
                .success(true)
                .datos(actividades)
                .build());
    }

    /**
     * Lista actividades disponibles para un día específico de la semana.
     * <p>
     * Ejemplo: /api/v1/actividades/dia/LUNES
     * </p>
     *
     * @param dia el día de la semana (LUNES, MARTES, MIERCOLES, etc.)
     * @return ResponseEntity con la lista de actividades para ese día
     */
    @GetMapping("/dia/{dia}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar actividades por día", description = "Lista todas las actividades disponibles para un día específico")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actividades recuperadas con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron actividades para el día especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para listar actividades por día") 
    public ResponseEntity<ApiResponseDto<List<ActividadResponseDto>>> listarPorDia(
            @PathVariable("dia") String dia) { 
        log.info("Solicitud de listar actividades para el día: {}", dia);
        List<ActividadResponseDto> actividades = actividadService.listarPorDia(dia);
        
        return ResponseEntity.ok(ApiResponseDto.<List<ActividadResponseDto>>builder()
                .mensaje("Actividades del día " + dia + " recuperadas")
                .success(true)
                .datos(actividades)
                .build());
    }

    /**
     * Filtra actividades por precio máximo.
     * <p>
     * Retorna todas las actividades cuyo precio sea menor o igual al especificado.
     * Ejemplo: /api/v1/actividades/precio-max?precio=15
     * </p>
     *
     * @param precio el precio máximo para filtrar
     * @return ResponseEntity con la lista de actividades filtradas por precio
     */
    @GetMapping("/precio-max")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Filtrar actividades por precio máximo", description = "Filtra todas las actividades disponibles por su precio máximo")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actividades recuperadas con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron actividades con el precio máximo especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para filtrar actividades por precio máximo") 
    public ResponseEntity<ApiResponseDto<List<ActividadResponseDto>>> filtrarPorPrecio(
            @RequestParam("precio") Integer precio) { 
        log.info("Solicitud de filtrar actividades por precio máximo: {}", precio);
        List<ActividadResponseDto> actividades = actividadService.filtrarPorPrecioMaximo(precio);
        
        return ResponseEntity.ok(ApiResponseDto.<List<ActividadResponseDto>>builder()
                .mensaje("Actividades filtradas por precio recuperadas")
                .success(true)
                .datos(actividades)
                .build());
    }
}
