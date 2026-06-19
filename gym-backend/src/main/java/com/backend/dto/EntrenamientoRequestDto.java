package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear un entrenamiento")
public class EntrenamientoRequestDto {

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

    @JsonProperty("categoriaId")
    private Long categoriaId;

    @JsonProperty("cantidadEjercicios")
    private Integer cantidadEjercicios;

    @JsonProperty("categoria") // Recibe "GENERAL"
    private String categoria;

    @JsonProperty("tutorialesIds")
    private List<Long> tutorialesIds;
}