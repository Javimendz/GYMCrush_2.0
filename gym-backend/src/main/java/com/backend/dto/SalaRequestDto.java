package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de solicitud para crear una sala de video")
public class SalaRequestDto {
    @Schema(description = "Nombre de la sala")
    @NotBlank(message = "El nombre de la sala es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombre;

    @Schema(description = "Capacidad máxima de usuarios en la sala")
    @NotNull(message = "La capacidad máxima es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser de al menos 1 persona")
    private Integer capacidadMax;

    @Schema(description = "Descripción de la sala")
    @Size(max = 255)
    private String descripcion;

    @Schema(description = "Ubicación de la sala")
    @Size(max = 100)
    private String ubicacion;

    @Schema(description = "Equipamiento disponible en la sala")
    private String equipamiento;

    @Schema(description = "Indica si la sala está activa")
    @Builder.Default
    private Boolean activa = true; // Por defecto la sala se crea activa
}