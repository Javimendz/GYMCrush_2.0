package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class SaludRequestDto {
    @SerializedName("peso")
    private Double peso;
    
    @SerializedName("estatura")
    private Double estatura;
    
    @SerializedName("nivelActividad")
    private String nivelActividad;

    @SerializedName("comentario")
    private String comentario;

    // Getters
    public Double getPeso() { return peso; }
    public Double getEstatura() { return estatura; }
    public String getNivelActividad() { return nivelActividad; }
    public String getComentario() { return comentario; }

    // Setters
    public void setPeso(Double peso) { this.peso = peso; }
    public void setEstatura(Double estatura) { this.estatura = estatura; }
    public void setNivelActividad(String nivelActividad) { this.nivelActividad = nivelActividad; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
