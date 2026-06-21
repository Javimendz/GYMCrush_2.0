package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class PerfilFisicoRequest {
    @SerializedName("usuarioId")
    private Long usuarioId;
    
    @SerializedName("peso")
    private Double peso;
    
    @SerializedName("altura")
    private Double altura;
    
    @SerializedName("edad")
    private Integer edad;
    
    @SerializedName("nivelActividad")
    private String nivelActividad;
    
    @SerializedName("objetivo")
    private String objetivo;

    @SerializedName("genero")
    private String genero;

    // Getters y Setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public String getNivelActividad() { return nivelActividad; }
    public void setNivelActividad(String nivelActividad) { this.nivelActividad = nivelActividad; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
}
