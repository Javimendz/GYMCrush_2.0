package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta para una categoría de dieta")
public class CategoriaResponseDto {
    @Schema(description = "ID de la categoría")
    private Long id;
    @Schema(description = "Nombre de la categoría")
    private String nombre;
    @Schema(description = "Descripción de la categoría")
    private String descripcion;
}