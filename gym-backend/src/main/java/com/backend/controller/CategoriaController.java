package com.backend.controller;

import com.backend.dto.CategoriaRequestDto;
import com.backend.dto.CategoriaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ICategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Categorías de Actividad", description = "Gestión de familias de ejercicios (Cardio, Fuerza, etc.)")
public class CategoriaController {

    private final ICategoriaService categoriaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CategoriaResponseDto>> crear(@Valid @RequestBody CategoriaRequestDto dto) {
        log.info("Creando nueva categoría de actividad: {}", dto.getNombre());
        CategoriaResponseDto nueva = categoriaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Categoría '" + dto.getNombre() + "' creada con éxito", nueva));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponseDto<List<CategoriaResponseDto>>> listar() {
        List<CategoriaResponseDto> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(ApiResponseDto.success("Listado de categorías obtenido", categorias));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponseDto<CategoriaResponseDto>> obtenerPorId(@PathVariable Long id) {
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponseDto.success("Categoría encontrada", categoria));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok(ApiResponseDto.success("Categoría eliminada correctamente", null));
    }
}