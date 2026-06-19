package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para asignar un entrenamiento existente a un Plan Maestro")
public class DetallePlanRequestDto {

    @Schema(description = "ID del entrenamiento (ejercicio físico) a añadir", example = "1")
    @NotNull(message = "El ID del entrenamiento es obligatorio")
    private Long entrenamientoId;

    @Schema(description = "Día de la semana (1=Lunes, 7=Domingo)", example = "1")
    @NotNull(message = "El día de la semana es obligatorio")
    @Min(value = 1, message = "El día mínimo es 1 (Lunes)")
    @Max(value = 7, message = "El día máximo es 7 (Domingo)")
    private Integer diaSemana;

    @Schema(description = "Posición del ejercicio en ese día", example = "1")
    @NotNull(message = "El orden es obligatorio")
    @Min(value = 1, message = "El orden debe ser al menos 1")
    private Integer orden;

    private Integer series;
    private Integer repeticiones;
}