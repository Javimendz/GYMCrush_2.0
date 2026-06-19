package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de solicitud para una categoría de dieta")
public class CategoriaDietaRequestDto {
    @Schema(description = "Nombre de la categoría")
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 50)
    private String nombre;

    @Schema(description = "Descripción de la categoría")
    @Size(max = 255)
    private String descripcion;
}