package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CategoriaTutorialRequestDto {
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("esGlobal")
    private boolean esGlobal;

    public CategoriaTutorialRequestDto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isEsGlobal() { return esGlobal; }
    public void setEsGlobal(boolean esGlobal) { this.esGlobal = esGlobal; }
}
