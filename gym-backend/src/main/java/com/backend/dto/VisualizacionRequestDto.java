package com.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para registrar o actualizar el progreso de un video")
public class VisualizacionRequestDto {

    @NotNull(message = "El ID de usuario es obligatorio")
    @Schema(description = "ID del usuario que ve el video", example = "1")
    private Long usuarioId;

    @NotNull(message = "El ID del tutorial es obligatorio")
    @Schema(description = "ID del tutorial consultado", example = "5")
    private Long tutorialId;

    @NotNull(message = "El progreso en segundos es obligatorio")
    @PositiveOrZero(message = "El progreso no puede ser negativo")
    @Schema(description = "Segundo exacto donde se encuentra el usuario", example = "125")
    private Integer progresoSegundos;

    @Schema(description = "Indica si el usuario ha terminado de ver el video", example = "false")
    private Boolean completado;
}