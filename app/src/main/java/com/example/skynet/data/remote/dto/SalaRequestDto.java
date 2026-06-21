package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class SalaRequestDto {
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("capacidadMax")
    private Integer capacidadMax;
    @SerializedName("descripcion")
    private String descripcion;
    @SerializedName("ubicacion")
    private String ubicacion;
    @SerializedName("equipamiento")
    private String equipamiento;
    @SerializedName("activa")
    private Boolean activa;

    public SalaRequestDto(String nombre, Integer capacidadMax, String descripcion, String ubicacion, String equipamiento, Boolean activa) {
        this.nombre = nombre;
        this.capacidadMax = capacidadMax;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.equipamiento = equipamiento;
        this.activa = activa;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getCapacidadMax() { return capacidadMax; }
    public void setCapacidadMax(Integer capacidadMax) { this.capacidadMax = capacidadMax; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getEquipamiento() { return equipamiento; }
    public void setEquipamiento(String equipamiento) { this.equipamiento = equipamiento; }
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}