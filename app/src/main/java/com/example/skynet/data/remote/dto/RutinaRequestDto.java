package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class RutinaRequestDto {
    @SerializedName("usuarioId")
    private Long usuarioId;
    @SerializedName("entrenamientoId")
    private Long entrenamientoId;
    @SerializedName("fechaAsignacion")
    private String fechaAsignacion;
    @SerializedName("orden")
    private Integer orden;
    @SerializedName("completado")
    private Boolean completado = false;

    public RutinaRequestDto() {}

    public RutinaRequestDto(Long usuarioId, Long entrenamientoId, String fechaAsignacion, Integer orden) {
        this.usuarioId = usuarioId;
        this.entrenamientoId = entrenamientoId;
        this.fechaAsignacion = fechaAsignacion;
        this.orden = orden;
        this.completado = false;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getEntrenamientoId() { return entrenamientoId; }
    public void setEntrenamientoId(Long entrenamientoId) { this.entrenamientoId = entrenamientoId; }
    public String getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(String fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
}