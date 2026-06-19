package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de solicitud para una dieta de alimentación")
public class DietaRequestDto {

    @Schema(description = "Nombre de la dieta")
    @NotBlank(message = "El nombre de la dieta es obligatorio")
    @Size(max = 80)
    private String nombre;

    @Schema(description = "Tipo de la dieta")
    @Size(max = 50)
    private String tipo;

    @Schema(description = "Cantidad de proteínas en gramos")
    @PositiveOrZero(message = "Las proteínas no pueden ser negativas")
    private Double cantidadProteinas;

    @Schema(description = "Cantidad de carbohidratos en gramos")
    @PositiveOrZero(message = "Los carbohidratos no pueden ser negativos")
    private Double cantidadCarbohidratos;

    @Schema(description = "Cantidad de grasas en gramos")
    @PositiveOrZero(message = "Las grasas no pueden ser negativas")
    private Double cantidadGrasas;

    @NotNull(message = "El objetivo calórico es obligatorio")
    @Positive(message = "Las calorías deben ser mayores a cero")
    private Double objetivoCalorico;

    @Schema(description = "ID de la categoría de la dieta")
    @NotNull(message = "Debe asignar una categoría a la dieta")
    private Long categoriaDietaId;

    @Schema(description = "Descripción de la dieta")
    @Size(max = 255)
    private String descripcion;
}
