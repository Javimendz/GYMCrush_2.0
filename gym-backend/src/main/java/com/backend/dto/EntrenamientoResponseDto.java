package com.backend.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object (DTO) para respuestas de entrenamientos.
 */
@Data 
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la respuesta de entrenamiento")
public class EntrenamientoResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("urlVideo") 
    private String urlVideo;

    @JsonProperty("urlImagen")
    private String urlImagen;

    @JsonProperty("duracion")
    private Integer duracion;

    @JsonProperty("intensidad")
    private String intensidad;
    
    @JsonProperty("categoria")   
    private String categoria;

    private Integer cantidadEjercicios;
}