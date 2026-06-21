package com.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o asignar una rutina a un usuario")
public class RutinaRequestDto {

    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha en la que el usuario debe realizar el ejercicio", example = "2026-04-15")
    private LocalDate fechaAsignacion;

    @Positive(message = "El orden debe ser positivo")
    @Schema(description = "Posición del ejercicio en la sesión del día", example = "1")
    private Integer orden;

    @Schema(description = "Estado de realización", example = "false")
    private Boolean completado;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Schema(description = "ID del usuario que recibirá la rutina", example = "1")
    private Long usuarioId;

    @NotNull(message = "El ID de entrenamiento es obligatorio")
    @Schema(description = "ID del ejercicio del catálogo que se va a asignar", example = "5")
    private Long entrenamientoId;

   @NotNull(message = "Debe incluir al menos una serie")
    @Valid
    private List<SerieRutinaRequestDto> series;
}