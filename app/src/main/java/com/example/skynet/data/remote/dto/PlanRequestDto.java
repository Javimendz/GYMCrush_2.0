package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class PlanRequestDto {
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("descripcion")
    private String descripcion;
    @SerializedName("objetivo")
    private String objetivo;
    @SerializedName("nivel")
    private String nivel;
    @SerializedName("esGlobal")
    private boolean esGlobal;

    public PlanRequestDto(String nombre, String descripcion, String objetivo, String nivel) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.objetivo = objetivo;
        this.nivel = nivel;
    }

    // Getters and Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public boolean isEsGlobal() { return esGlobal; }
    public void setEsGlobal(boolean esGlobal) { this.esGlobal = esGlobal; }
}
