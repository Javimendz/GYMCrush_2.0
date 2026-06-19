package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de solicitud para una categoría de dieta")
public class CategoriaRequestDto {
    @Schema(description = "Nombre de la categoría")
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombre;

    @Schema(description = "Descripción de la categoría")
    @Size(max = 255, message = "La descripción es demasiado larga")
    private String descripcion;
}