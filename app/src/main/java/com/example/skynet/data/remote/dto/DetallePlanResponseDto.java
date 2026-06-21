package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class DetallePlanResponseDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("entrenamientoId")
    private Long entrenamientoId;
    @SerializedName("nombreEntrenamiento")
    private String nombreEntrenamiento;
    @SerializedName("diaSemana")
    private Integer diaSemana;
    @SerializedName("orden")
    private Integer orden;
    @SerializedName("series")
    private Integer series;
    @SerializedName("repeticiones")
    private Integer repeticiones;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEntrenamientoId() { return entrenamientoId; }
    public void setEntrenamientoId(Long entrenamientoId) { this.entrenamientoId = entrenamientoId; }
    public String getNombreEntrenamiento() { return nombreEntrenamiento; }
    public void setNombreEntrenamiento(String nombreEntrenamiento) { this.nombreEntrenamiento = nombreEntrenamiento; }
    public Integer getDiaSemana() { return diaSemana; }
    public void setDiaSemana(Integer diaSemana) { this.diaSemana = diaSemana; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }
    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }
}