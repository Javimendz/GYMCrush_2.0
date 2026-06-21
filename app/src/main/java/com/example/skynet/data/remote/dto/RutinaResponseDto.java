package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class RutinaResponseDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("fechaAsignacion")
    private String fechaAsignacion;
    @SerializedName("orden")
    private Integer orden;
    @SerializedName("completado")
    private Boolean completado;
    @SerializedName("entrenamientoId")
    private Long entrenamientoId;
    @SerializedName("nombreEntrenamiento")
    private String nombreEntrenamiento;
    @SerializedName("duracion")
    private Integer duracion;
    @SerializedName("intensidad")
    private String intensidad;
    @SerializedName("urlVideo")
    private String urlVideo;
    @SerializedName("nombrePlan")
    private String nombrePlan;
    @SerializedName("series")
    private String series;
    @SerializedName("repeticiones")
    private String repeticiones;
    @SerializedName("urlImagen")
    private String urlImagen;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(String fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
    public Long getEntrenamientoId() { return entrenamientoId; }
    public void setEntrenamientoId(Long entrenamientoId) { this.entrenamientoId = entrenamientoId; }
    public String getNombreEntrenamiento() { return nombreEntrenamiento; }
    public void setNombreEntrenamiento(String nombreEntrenamiento) { this.nombreEntrenamiento = nombreEntrenamiento; }
    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }
    public String getIntensidad() { return intensidad; }
    public void setIntensidad(String intensidad) { this.intensidad = intensidad; }
    public String getUrlVideo() { return urlVideo; }
    public void setUrlVideo(String urlVideo) { this.urlVideo = urlVideo; }
    public String getNombrePlan() { return nombrePlan; }
    public void setNombrePlan(String nombrePlan) { this.nombrePlan = nombrePlan; }
    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }
    public String getRepeticiones() { return repeticiones; }
    public void setRepeticiones(String repeticiones) { this.repeticiones = repeticiones; }
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}