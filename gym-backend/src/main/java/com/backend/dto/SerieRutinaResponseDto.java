package com.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SerieRutinaResponseDto {
    private Long id;
    private Integer numeroSerie;
    private Double peso;
    private Integer repeticiones;
    private Boolean completada;
}