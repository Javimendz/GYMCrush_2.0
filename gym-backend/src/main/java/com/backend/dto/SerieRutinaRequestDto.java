package com.backend.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SerieRutinaRequestDto {

    @NotNull(message = "El número de serie es obligatorio")
    private Integer numeroSerie; // 1, 2, 3...

    @NotNull(message = "El peso es obligatorio")
    private Double peso;

    @NotNull(message = "Las repeticiones son obligatorias")
    private Integer repeticiones;
}