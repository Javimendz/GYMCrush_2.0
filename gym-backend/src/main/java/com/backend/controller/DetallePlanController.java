package com.backend.controller;

import java.util.List;



import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid; 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.backend.dto.DetallePlanRequestDto;
import com.backend.dto.DetallePlanResponseDto;
import com.backend.service.IDetallePlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import com.backend.security.dto.ApiResponseDto;

@RestController
@RequestMapping("/api/v1/planes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Edición de Detalles de Plan", description = "Operaciones secundarias para modificar la composición de un plan")
public class DetallePlanController {

    private final IDetallePlanService detalleService;

    @GetMapping("/{planId}/ejercicios")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Listar ejercicios del plan", description = "Obtiene la lista completa de ejercicios ordenados")
    public ResponseEntity<ApiResponseDto<List<DetallePlanResponseDto>>> listarPorPlan(@PathVariable Long planId) {
        List<DetallePlanResponseDto> detalles = detalleService.listarPorPlan(planId);
        return ResponseEntity.ok(ApiResponseDto.success("Composición del plan obtenida", detalles));
    }
    @PostMapping("/plan/{planId}")
    public ResponseEntity<DetallePlanResponseDto> agregarEjercicio(
            @PathVariable Long planId,
            @Valid @RequestBody DetallePlanRequestDto dto) {
        
        DetallePlanResponseDto nuevoDetalle = detalleService.guardarEjercicio(planId, dto);
        return new ResponseEntity<>(nuevoDetalle, HttpStatus.CREATED);
    }
    @DeleteMapping("/ejercicios-detalle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Quitar ejercicio del plan", description = "Elimina un ejercicio específico usando el ID del DetallePlan")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        detalleService.eliminarEjercicio(id);
        return ResponseEntity.ok(ApiResponseDto.success("Ejercicio quitado del plan maestro", null));
    }

    @PatchMapping("/ejercicios-detalle/{id}/reordenar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar orden/día", description = "Permite mover un ejercicio a otro día o cambiar su posición")
    public ResponseEntity<ApiResponseDto<DetallePlanResponseDto>> actualizarOrden(
            @PathVariable Long id,
            @RequestParam Integer nuevoDia,
            @RequestParam Integer nuevoOrden) {

        DetallePlanResponseDto response = detalleService.actualizarOrden(id, nuevoDia, nuevoOrden);
        return ResponseEntity.ok(ApiResponseDto.success("Orden actualizado correctamente", response));
    }
}