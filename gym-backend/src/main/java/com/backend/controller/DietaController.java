package com.backend.controller;

import com.backend.dto.DietaRequestDto;
import com.backend.dto.DietaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IDietaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dietas")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Dietas", description = "Operaciones relacionadas con el catálogo maestro de dietas")
public class DietaController {

    private final IDietaService dietaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<DietaResponseDto>> crear(@Valid @RequestBody DietaRequestDto dto) {
        DietaResponseDto nuevaDieta = dietaService.crearDieta(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Dieta creada exitosamente en el catálogo", nuevaDieta));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponseDto<List<DietaResponseDto>>> listar() {
        log.info("Solicitud para listar todas las dietas del catálogo");
        List<DietaResponseDto> dietas = dietaService.listarTodas();
        return ResponseEntity.ok(
                ApiResponseDto.success("Listado de dietas obtenido correctamente", dietas));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponseDto<DietaResponseDto>> obtenerPorId(@PathVariable("id") Long id) {
        log.info("Solicitud para obtener la dieta con ID: {}", id);
        DietaResponseDto dieta = dietaService.obtenerPorId(id);
        return ResponseEntity.ok(
                ApiResponseDto.success("Detalle de la dieta encontrado", dieta));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable("id") Long id) {
        log.info("Solicitud para eliminar la dieta con ID: {}", id);
        dietaService.eliminar(id);

        return ResponseEntity.ok(
                ApiResponseDto.success("La dieta ha sido eliminada del catálogo", null));
    }
}
