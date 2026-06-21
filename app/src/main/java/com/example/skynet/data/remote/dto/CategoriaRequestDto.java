package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CategoriaRequestDto {
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("descripcion")
    private String descripcion;

    public CategoriaRequestDto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
