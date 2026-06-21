package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EntrenamientoRequestDto {

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
    @SerializedName("cantidadEjercicios")
    private Integer cantidadEjercicios;

    @SerializedName("urlImagen")
    private String urlImagen;

    @SerializedName("urlVideo")
    private String urlVideo;
    @SerializedName("categoria")
    private String categoria;   // Para el texto "GENERAL"

    @SerializedName("esGlobal")
    private boolean esGlobal;


    // ❌ OPCIONAL (solo si backend lo soporta)
    @SerializedName("tutorialesIds")
    private List<Long> tutorialesIds;

    public EntrenamientoRequestDto() {
    }


    public EntrenamientoRequestDto(
            String nombre,
            String descripcion,
            Integer duracion,
            String intensidad,
            Long categoriaId,
            Integer cantidadEjercicios,
            String urlVideo,
            String urlImagen
    ) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.intensidad = intensidad;
        this.categoriaId = categoriaId;
        this.cantidadEjercicios = cantidadEjercicios;
        this.urlVideo = urlVideo;
        this.urlImagen = urlImagen;
    }

    // Constructor para RutinasFragment y guardar desde externo
    public EntrenamientoRequestDto(
            String nombre,
            String descripcion,
            Integer duracion,
            String intensidad,
            String categoria,
            Integer cantidadEjercicios,
            String urlVideo,
            String urlImagen
    ) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.intensidad = intensidad;
        this.categoria = categoria;
        this.cantidadEjercicios = cantidadEjercicios;
        this.urlVideo = urlVideo;
        this.urlImagen = urlImagen;
    }

    // Constructor de 10 parámetros para CrearRutinaActivity
    public EntrenamientoRequestDto(
            String nombre,
            String descripcion,
            Integer duracion,
            String intensidad,
            String categoria,
            List<Long> tutorialesIds,
            Long categoriaId,
            String urlVideo,
            Integer cantidadEjercicios,
            String urlImagen
    ) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.intensidad = intensidad;
        this.categoria = categoria;
        this.tutorialesIds = tutorialesIds;
        this.categoriaId = categoriaId;
        this.urlVideo = urlVideo;
        this.cantidadEjercicios = cantidadEjercicios;
        this.urlImagen = urlImagen;
    }

    // GETTERS Y SETTERS

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }

    public String getIntensidad() { return intensidad; }
    public void setIntensidad(String intensidad) { this.intensidad = intensidad; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public Integer getCantidadEjercicios() { return cantidadEjercicios; }
    public void setCantidadEjercicios(Integer cantidadEjercicios) { this.cantidadEjercicios = cantidadEjercicios; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public String getUrlVideo() { return urlVideo; }
    public void setUrlVideo(String urlVideo) { this.urlVideo = urlVideo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public List<Long> getTutorialesIds() { return tutorialesIds; }
    public void setTutorialesIds(List<Long> tutorialesIds) { this.tutorialesIds = tutorialesIds; }

    public boolean isEsGlobal() { return esGlobal; }
    public void setEsGlobal(boolean esGlobal) { this.esGlobal = esGlobal; }
}