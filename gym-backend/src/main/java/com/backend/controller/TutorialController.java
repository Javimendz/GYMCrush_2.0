package com.backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.backend.domain.Usuario; 
import com.backend.repository.UsuarioRepository; 
import org.springframework.security.core.Authentication;
import com.backend.dto.TutorialResponseDto;
import com.backend.dto.TutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.dto.ExerciseApiRequestDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ITutorialService;
import com.backend.service.IExerciseProxyService; 
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * Controlador REST para gestionar los tutoriales de ejercicios.
 * Centraliza la búsqueda en biblioteca externa y el catálogo local.
 */
@RestController
@RequestMapping("/api/v1/tutoriales")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tutorial", description = "Operaciones relacionadas con los tutoriales")
public class TutorialController {

    private final ITutorialService tutorialService;
    private final UsuarioRepository usuarioRepository;
    
    // CORRECCIÓN: Ahora sí inyectamos la interfaz
    private final IExerciseProxyService exerciseProxyService; 

    // =========================================================================
    // CONSULTA DE CATÁLOGO (USUARIOS Y ADMIN)
    // =========================================================================

   @GetMapping
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> listarTodos(Authentication auth) {
        log.info("Recuperando catálogo combinado");
        List<TutorialResponseDto> data = tutorialService.listarParaUsuarioYGlobales(auth.getName()); 
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Biblioteca combinada recuperada")
                .datos(data)
                .success(true)
                .build());
    }

    @PutMapping("/{id}") 
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')") 
    public ResponseEntity<ApiResponseDto<TutorialResponseDto>> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody TutorialRequestDto dto) {
        
        TutorialResponseDto actualizado = tutorialService.actualizar(id, dto);
        
        return ResponseEntity.ok(ApiResponseDto.<TutorialResponseDto>builder()
                .mensaje("Tutorial actualizado correctamente")
                .datos(actualizado)
                .success(true)
                .build());
    }
    @GetMapping("/mis-tutoriales")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> listarMisTutoriales(
            org.springframework.security.core.Authentication auth) {
        Usuario user = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        List<TutorialResponseDto> data = tutorialService.listarParaUsuario(user.getId());
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Tu biblioteca personal recuperada")
                .datos(data)
                .success(true)
                .build());
    }

    @GetMapping("/categoria/{catId}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar por categoría", description = "Filtra los tutoriales según el ID de la categoría")
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> listarPorCategoria(@PathVariable("catId") Long catId) {
        log.info("Filtrando tutoriales por categoría ID: {}", catId);
        List<TutorialResponseDto> data = tutorialService.obtenerPorCategoria(catId);
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Tutoriales de la categoría recuperados con éxito")
                .datos(data)
                .success(true)
                .build());
    }

    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar categorías", description = "Lista las categorías disponibles para clasificar tutoriales")
    public ResponseEntity<ApiResponseDto<List<CategoriaTutorialResponseDto>>> listarCategorias() {
        log.info("Recuperando listado de categorías");
        List<CategoriaTutorialResponseDto> data = tutorialService.listarCategorias();
        
        return ResponseEntity.ok(ApiResponseDto.<List<CategoriaTutorialResponseDto>>builder()
                .mensaje("Listado de categorías recuperado")
                .datos(data)
                .success(true)
                .build());
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Buscar tutoriales locales", 
        description = "Busca tutoriales en tu base de datos por título"
    )
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> buscarLocal(@RequestParam String query) {
        log.info("Buscando tutoriales locales con filtro: {}", query);
        List<TutorialResponseDto> data = tutorialService.buscarPorTitulo(query);
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Resultados de búsqueda local recuperados")
                .datos(data)
                .success(true)
                .build());
    }

    // =========================================================================
    // ADMINISTRACIÓN Y BIBLIOTECA EXTERNA (SOLO ADMIN)
    // =========================================================================

    @GetMapping("/biblioteca/buscar")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Buscar en ExerciseDB", description = "Busca ejercicios en la API externa para crear nuevos tutoriales")
    public ResponseEntity<ApiResponseDto<List<ExerciseApiRequestDto>>> buscarEnBiblioteca(@RequestParam String nombre) {
        log.info("Admin buscando en biblioteca externa: {}", nombre);
        List<ExerciseApiRequestDto> data = exerciseProxyService.buscarPorNombre(nombre);
        
        return ResponseEntity.ok(ApiResponseDto.<List<ExerciseApiRequestDto>>builder()
                .mensaje("Resultados de la biblioteca externa recuperados")
                .datos(data)
                .success(true)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')") 
    public ResponseEntity<ApiResponseDto<TutorialResponseDto>> crear(
            @Valid @RequestBody TutorialRequestDto dto,
            org.springframework.security.core.Authentication auth) {
        
        TutorialResponseDto nuevo = tutorialService.crear(dto, auth.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.<TutorialResponseDto>builder()
                .mensaje("Ejercicio añadido a tu biblioteca")
                .datos(nuevo)
                .success(true)
                .build());
    }

    @GetMapping("/publicos")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> listarPublicos() {
        log.info("Recuperando catálogo puramente público");
        List<TutorialResponseDto> data = tutorialService.listarGlobales();
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Biblioteca pública recuperada")
                .datos(data)
                .success(true)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar tutorial", description = "Borra un tutorial del sistema")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable("id") Long id) {
        log.info("Eliminando tutorial con ID: {}", id);
        tutorialService.eliminar(id);
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Tutorial eliminado correctamente")
                .success(true)
                .build());
    }
}