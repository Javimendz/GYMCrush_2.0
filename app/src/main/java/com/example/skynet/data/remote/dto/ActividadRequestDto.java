package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ActividadRequestDto {
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

    @SerializedName("categoriaId")
    private Long categoriaId;

    public ActividadRequestDto() {}

    public ActividadRequestDto(String nombre, String descripcion, Integer duracion, String sala, Integer precio, Long categoriaId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.sala = sala;
        this.precio = precio;
        this.categoriaId = categoriaId;
    }

    // Getters and Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
}
