package com.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.CategoriaTutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ICategoriaTutorialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para gestionar las categorías de tutoriales.
 * <p>
 * Este controlador proporciona endpoints para el CRUD de categorías de tutoriales:
 * <ul>
 *   <li>Listar todas las categorías</li>
 *   <li>Obtener categoría por ID</li>
 *   <li>Crear nueva categoría (ADMIN)</li>
 *   <li>Eliminar categoría (ADMIN)</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/categorias-tutorial")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "CategoriaTutorial", description = "Operaciones relacionadas con las categorías de tutoriales")
public class CategoriaTutorialController {

    private final ICategoriaTutorialService categoriaService;

    /**
     * Lista todas las categorías de tutoriales disponibles.
     *
     * @return ResponseEntity con la lista de categorías y mensaje de éxito
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todas las categorías de tutoriales", description = "Recupera una lista de todas las categorías de tutoriales disponibles")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de categorías recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para listar categorías de tutoriales") 
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<CategoriaTutorialResponseDto>>> listarTodas() {
        log.info("Listando todas las categorías de tutoriales");
        List<CategoriaTutorialResponseDto> data = categoriaService.listarTodas();
        log.debug("Total de categorías recuperadas: {}", data.size());
        
        return ResponseEntity.ok(ApiResponseDto.<List<CategoriaTutorialResponseDto>>builder()
                .mensaje("Listado de categorías de entrenamiento recuperado")
                .datos(data)
                .success(true)
                .build());
    }

    /**
     * Obtiene los detalles de una categoría específica por su ID.
     *
     * @param id el ID de la categoría a buscar
     * @return ResponseEntity con los datos de la categoría encontrada
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener categoría por ID", description = "Recupera los detalles de una categoría de tutorial por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Detalles de la categoría recuperados con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para obtener detalles de categorías de tutoriales") 
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<CategoriaTutorialResponseDto>> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Obteniendo categoría por ID: {}", id);
        CategoriaTutorialResponseDto data = categoriaService.buscarPorId(id);
        log.debug("Categoría encontrada: {} - {}", data.getId(), data.getNombre());
        
        return ResponseEntity.ok(ApiResponseDto.<CategoriaTutorialResponseDto>builder()
                .mensaje("Detalles de la categoría recuperados")
                .datos(data)
                .success(true)
                .build());
    }

    /**
     * Crea una nueva categoría de tutoriales.
     * <p>
     * Solo los usuarios con rol ADMIN pueden crear categorías.
     * </p>
     *
     * @param dto los datos de la categoría a crear
     * @return ResponseEntity con la categoría creada
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear nueva categoría", description = "Crea una nueva categoría de tutorial")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Categoría creada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida. Verifique los datos proporcionados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para crear categorías de tutoriales") 
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<CategoriaTutorialResponseDto>> crear(@Valid @RequestBody CategoriaTutorialRequestDto dto) {
        CategoriaTutorialResponseDto nueva = categoriaService.crear(dto);
        
       return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.<CategoriaTutorialResponseDto>builder()
                .mensaje("Categoría '" + nueva.getNombre() + "' creada con éxito")
                .datos(nueva)
                .success(true)
                .build());
    }

    /**
     * Elimina una categoría de tutoriales.
     * <p>
     * Solo los usuarios con rol ADMIN pueden eliminar categorías.
     * El servicio debe verificar si hay tutoriales asociados antes de eliminar.
     * </p>
     *
     * @param id el ID de la categoría a eliminar
     * @return ResponseEntity con confirmación de eliminación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar categoría", description = "Elimina una categoría de tutorial por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría eliminada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para eliminar categorías de tutoriales") 
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable("id") Long id) {
        categoriaService.eliminar(id);
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Categoría eliminada correctamente")
                .success(true)
                .build());
    }

    @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar categoría", description = "Actualiza los detalles de una categoría de tutorial existente")
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description  = "Categoría actualizada con éxito")
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description  = "Solicitud inválida. Verifique los datos proporcionados")
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description  = "Acceso denegado. El usuario no tiene permisos para actualizar categorías de tutoriales")
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description  = "Categoría no encontrada")
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description  = "Error interno del servidor")
public ResponseEntity<ApiResponseDto<CategoriaTutorialResponseDto>> actualizar(
        @PathVariable Long id, 
        @RequestBody CategoriaTutorialRequestDto dto) {
    
    CategoriaTutorialResponseDto actualizado = categoriaService.actualizar(id, dto);
    
    return ResponseEntity.ok(ApiResponseDto.<CategoriaTutorialResponseDto>builder()
            .mensaje("Categoría actualizada")
            .success(true)
            .datos(actualizado)
            .build());
}
}