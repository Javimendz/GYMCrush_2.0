package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ActividadResponseDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("duracion")
    private Integer duracion;

    @SerializedName("sala")
    private String sala;

    @SerializedName("precio")
    private Integer precio;

    @SerializedName("categoria")
    private CategoriaResponseDto categoria;

    public ActividadResponseDto() {}

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Integer getDuracion() { return duracion; }
    public String getSala() { return sala; }
    public Integer getPrecio() { return precio; }
    public CategoriaResponseDto getCategoria() { return categoria; }
}
