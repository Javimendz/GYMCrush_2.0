package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class NutricionRequestDto implements Serializable {
    @SerializedName("usuarioId")
    private Long usuarioId;

    @SerializedName("peso")
    private Double peso;

    @SerializedName("altura")
    private Double altura;

    @SerializedName("edad")
    private Integer edad;

    @SerializedName("genero")
    private String genero;

    @SerializedName("objetivo")
    private String objetivo;

    @SerializedName("nivelActividad")
    private String nivelActividad;

    public NutricionRequestDto() {}

    // Getters and Setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public String getNivelActividad() { return nivelActividad; }
    public void setNivelActividad(String nivelActividad) { this.nivelActividad = nivelActividad; }
}
