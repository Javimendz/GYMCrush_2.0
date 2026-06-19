package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta para una sala de video")
public class SalaResponseDto {
    @Schema(description = "Identificador único de la sala")
    private Long id;
    @Schema(description = "Nombre de la sala")
    private String nombre;
    @Schema(description = "Capacidad máxima de usuarios en la sala")
    private Integer capacidadMax;
    @Schema(description = "Descripción de la sala")
    private String descripcion;
    @Schema(description = "Ubicación de la sala")
    private String ubicacion;
    @Schema(description = "Equipamiento disponible en la sala")
    private String equipamiento;
    @Schema(description = "Indica si la sala está activa")
    private Boolean activa;
}