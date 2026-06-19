package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Datos requeridos para crear un nuevo Plan Maestro")
public class PlanRequestDto {

    @Schema(description = "Nombre comercial del plan", example = "Operación Verano 2024")
    @NotBlank(message = "El nombre del plan es obligatorio")
    private String nombre;

    @Schema(description = "Descripción detallada", example = "Plan de 4 días a la semana enfocado en hipertrofia")
    private String descripcion;

    @Schema(description = "Objetivo principal", example = "GANAR_MUSCULO")
    @NotBlank(message = "El objetivo es obligatorio")
    private String objetivo;

    @Schema(description = "Nivel de dificultad", example = "INTERMEDIO")
    @NotBlank(message = "El nivel es obligatorio")
    private String nivel;
}