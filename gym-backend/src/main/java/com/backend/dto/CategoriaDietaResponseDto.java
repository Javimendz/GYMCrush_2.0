package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para una categoría de dieta")
public class CategoriaDietaResponseDto {
    @Schema(description = "ID de la categoría")
    private Long id;
    @Schema(description = "Nombre de la categoría")
    private String nombre;
    @Schema(description = "Descripción de la categoría")
    private String descripcion;
}