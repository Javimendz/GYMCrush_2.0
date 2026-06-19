package com.backend.service;

import com.backend.dto.NutricionRequestDto;
import com.backend.dto.NutricionResponseDto;
import org.springframework.data.domain.Page;

public interface INutricionService {
    NutricionResponseDto generarPlanAutomatico(NutricionRequestDto request);

    NutricionResponseDto generarPlanSemanal(NutricionRequestDto request);

    NutricionResponseDto actualizarNombrePlan(Long id, String nuevoNombre);

    NutricionResponseDto obtenerUltimoPlanPorUsuario(Long usuarioId);

    Page<NutricionResponseDto> obtenerHistorial(Long usuarioId, int page, int size);

    NutricionResponseDto obtenerPlanPorId(Long id);

    NutricionResponseDto guardarPlan(Long usuarioId, NutricionResponseDto planDto);
}