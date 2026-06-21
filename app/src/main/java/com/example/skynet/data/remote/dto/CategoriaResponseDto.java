package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CategoriaResponseDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("descripcion")
    private String descripcion;

    public CategoriaResponseDto() {}

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return nombre;
    }
}
