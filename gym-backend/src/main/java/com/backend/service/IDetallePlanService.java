package com.backend.service;

import java.util.List;

// Estos imports son los que salvaron el Build
import com.backend.dto.DetallePlanRequestDto;
import com.backend.dto.DetallePlanResponseDto;

public interface IDetallePlanService {

    /**
     * Añade un ejercicio específico a un día concreto de un plan maestro.
     */
    DetallePlanResponseDto guardarEjercicio(Long planId, DetallePlanRequestDto dto);

    /**
     * Obtiene todos los ejercicios de un plan maestro ordenados por día y orden.
     */
    List<DetallePlanResponseDto> listarPorPlan(Long planId);

    /**
     * Elimina un ejercicio de la composición del plan (borra el DetallePlan).
     */
    void eliminarEjercicio(Long detalleId);

    /**
     * Permite cambiar el orden o el día de un ejercicio ya asignado.
     */
    DetallePlanResponseDto actualizarOrden(Long detalleId, Integer nuevoDia, Integer nuevoOrden);
}