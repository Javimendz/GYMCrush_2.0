package com.backend.controller;

import com.backend.dto.CategoriaDietaRequestDto;
import com.backend.dto.CategoriaDietaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ICategoriaDietaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias-dieta")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Categorías de Dieta", description = "Operaciones relacionadas con las categorías de dietas")
public class CategoriaDietaController {

    private final ICategoriaDietaService categoriaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear categoría")
    public ResponseEntity<ApiResponseDto<CategoriaDietaResponseDto>> crear(
            @Valid @RequestBody CategoriaDietaRequestDto dto) {

        CategoriaDietaResponseDto nuevaCategoria = categoriaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Categoría '" + dto.getNombre() + "' creada con éxito", nuevaCategoria));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todas las categorías")
    public ResponseEntity<ApiResponseDto<List<CategoriaDietaResponseDto>>> listar() {
        List<CategoriaDietaResponseDto> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(
                ApiResponseDto.success("Listado de categorías obtenido", categorias));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener categoría por ID")
    public ResponseEntity<ApiResponseDto<CategoriaDietaResponseDto>> obtenerPorId(
            @PathVariable("id") Long id) {

        CategoriaDietaResponseDto categoria = categoriaService.obtenerPorId(id);
        return ResponseEntity.ok(
                ApiResponseDto.success("Detalle de categoría encontrado", categoria));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar categoría")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(
            @PathVariable("id") Long id) {

        categoriaService.eliminar(id);
        return ResponseEntity.ok(
                ApiResponseDto.success("La categoría ha sido eliminada correctamente", null));
    }
}