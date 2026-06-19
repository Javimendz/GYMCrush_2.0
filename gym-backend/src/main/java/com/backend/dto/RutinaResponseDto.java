package com.backend.dto;

import lombok.*;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO que recibe la App con la información completa de la rutina")
public class RutinaResponseDto {

    @Schema(description = "ID de la rutina (usar para marcar como completado)")
    private Long id;

    @Schema(description = "Fecha programada")
    private LocalDate fechaAsignacion;

    @Schema(description = "Orden en la lista")
    private Integer orden;

    @Schema(description = "Si ya se ha realizado")
    private Boolean completado;

    
    @Schema(description = "ID del entrenamiento base")
    private Long entrenamientoId;

    @Schema(description = "Nombre del ejercicio", example = "Press Banca")
    private String nombreEntrenamiento; 

    @Schema(description = "Duración en minutos", example = "45")
    private Integer duracion;

    @Schema(description = "Nivel de esfuerzo", example = "ALTA")
    private String intensidad;
    
    @Schema(description = "URL de la imagen del ejercicio")
    private String urlImagen;
    @Schema
    private String nombrePlan;
}