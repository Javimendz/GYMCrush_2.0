package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta que detalla un ejercicio dentro de un plan")
public class DetallePlanResponseDto {

    @Schema(description = "ID interno de la relación (DetallePlan)")
    private Long id;

    @Schema(description = "ID del entrenamiento físico real")
    private Long entrenamientoId;

    @Schema(description = "Nombre del ejercicio", example = "Press de Banca")
    private String nombreEntrenamiento;

    @Schema(description = "Descripción de cómo hacer el ejercicio")
    private String descripcionEntrenamiento;

    @Schema(description = "Día en el que toca hacerlo")
    private Integer diaSemana;

    @Schema(description = "Orden de ejecución en la sesión")
    private Integer orden;
}