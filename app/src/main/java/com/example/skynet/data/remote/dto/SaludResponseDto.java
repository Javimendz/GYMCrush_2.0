package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class SaludResponseDto {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("peso")
    private Double peso;
    
    @SerializedName("estatura")
    private Double estatura;
    
    @SerializedName("imc")
    private Double imc;
    
    @SerializedName("nivelActividad")
    private String nivelActividad;

    @SerializedName("comentario")
    private String comentario;

    @SerializedName("fechaMedicion")
    private String fechaMedicion;

    // Getters
    public Long getId() { return id; }
    public Double getPeso() { return peso; }
    public Double getEstatura() { return estatura; }
    public Double getImc() { return imc; }
    public String getNivelActividad() { return nivelActividad; }
    public String getComentario() { return comentario; }
    public String getFechaMedicion() { return fechaMedicion; }
}
