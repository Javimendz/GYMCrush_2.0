package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class EntrenamientoResponseDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("duracion")
    private Integer duracion;

    @SerializedName("intensidad")
    private String intensidad;

    @SerializedName("categoriaId")
    private Long categoriaId;
    @SerializedName("categoria")
    private String categoria;

    @SerializedName("urlVideo")
    private String urlVideo;

    @SerializedName("urlImagen")
    private String urlImagen;

    @SerializedName("esGlobal")
    private boolean esGlobal;

    @SerializedName("usuarioId")
    private Long usuarioId;

    public EntrenamientoResponseDto() {
    }

    // Getters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Integer getDuracion() { return duracion; }
    public String getIntensidad() { return intensidad; }
    public String getCategoria() { return categoria; }
    public String getUrlVideo() { return urlVideo; }
    public String getUrlImagen() { return urlImagen; }

    // Setters (Opcionales pero recomendados)
    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }
    public void setIntensidad(String intensidad) { this.intensidad = intensidad; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setUrlVideo(String urlVideo) { this.urlVideo = urlVideo; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public boolean isEsGlobal() { return esGlobal; }
    public void setEsGlobal(boolean esGlobal) { this.esGlobal = esGlobal; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}