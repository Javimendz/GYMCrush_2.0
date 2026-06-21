package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class DetallePlanRequestDto {
    @SerializedName("entrenamientoId")
    private Long entrenamientoId;
    @SerializedName("diaSemana")
    private Integer diaSemana;
    @SerializedName("orden")
    private Integer orden;
    @SerializedName("series")
    private Integer series;
    @SerializedName("repeticiones")
    private Integer repeticiones;

    public DetallePlanRequestDto(Long entrenamientoId, Integer diaSemana, Integer orden) {
        this.entrenamientoId = entrenamientoId;
        this.diaSemana = diaSemana;
        this.orden = orden;
        this.series = 3; // Valores por defecto para evitar nulos en el backend
        this.repeticiones = 12;
    }

    public DetallePlanRequestDto(Long entrenamientoId, Integer diaSemana, Integer orden, Integer series, Integer repeticiones) {
        this.entrenamientoId = entrenamientoId;
        this.diaSemana = diaSemana;
        this.orden = orden;
        this.series = series;
        this.repeticiones = repeticiones;
    }

    // Getters and Setters
    public Long getEntrenamientoId() { return entrenamientoId; }
    public void setEntrenamientoId(Long entrenamientoId) { this.entrenamientoId = entrenamientoId; }
    public Integer getDiaSemana() { return diaSemana; }
    public void setDiaSemana(Integer diaSemana) { this.diaSemana = diaSemana; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }
    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }
}
