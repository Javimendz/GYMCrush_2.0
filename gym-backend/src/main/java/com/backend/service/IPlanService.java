package com.backend.service;

import com.backend.dto.DetallePlanRequestDto;
import com.backend.dto.DetallePlanResponseDto;
import com.backend.dto.PlanRequestDto;
import com.backend.dto.PlanResponseDto;
import java.util.List;

public interface IPlanService {
    // Para el Administrador
  PlanResponseDto crearPlan(PlanRequestDto dto, String username);
    void eliminarPlan(Long id);
    List<PlanResponseDto> listarTodos(); // Para Admin
    List<PlanResponseDto> listarPlanesParaUsuario(Long usuarioId); // Para la App
    PlanResponseDto obtenerPorId(Long id);
    PlanResponseDto añadirEjercicioAlPlan(Long planId, List<DetallePlanRequestDto> ejerciciosDto);
    void suscribirUsuarioAPlan(Long usuarioId, Long planId, String usernameLogueado, boolean esAdmin);
}