package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class SalaResponseDto {
    @SerializedName("id")
    private Long id;
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

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public Integer getCapacidadMax() { return capacidadMax; }
    public String getDescripcion() { return descripcion; }
    public String getUbicacion() { return ubicacion; }
    public String getEquipamiento() { return equipamiento; }
    public Boolean getActiva() { return activa; }
}