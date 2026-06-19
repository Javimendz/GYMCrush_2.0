package com.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NutricionResponseDto {
    private Long id;
    private String nombreDieta;
    private String tipoDieta;
    private Integer caloriasObjetivo;

    // convertimos el JSON en un objeto real para el Frontend

    @JsonProperty("dietas")
    private List<ComidaDiariaResponseDto> comidas;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaGeneracion;
    private FatSecretResponseDto macronutrientes;

}