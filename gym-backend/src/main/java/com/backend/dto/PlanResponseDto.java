package com.backend.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con los datos completos de un Plan Maestro y sus ejercicios")
public class PlanResponseDto {

    private Long id;
    private String nombre;
    private String objetivo;
    private String nivel;
    @Schema(description = "ID del usuario creador (null si es global)")
    private Long usuarioId;
    @Schema(description = "Indica si es un plan oficial del gimnasio")
    private boolean esGlobal;
    private String descripcion;
    @Schema(description = "Lista de ejercicios asignados a este plan, ordenados por día y orden")
    private List<DetallePlanResponseDto> ejercicios;
}