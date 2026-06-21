package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ReservaRequestDto {

    @SerializedName("usuarioId")
    private Long usuarioId;

    @SerializedName("horarioId")
    private Long horarioId;

    @SerializedName("fecha")
    private String fecha; // Usamos String para compatibilidad con Gson si no hay un adaptador de LocalDate

    public ReservaRequestDto() {}

    public ReservaRequestDto(Long usuarioId, Long horarioId, String fecha) {
        this.usuarioId = usuarioId;
        this.horarioId = horarioId;
        this.fecha = fecha;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getHorarioId() { return horarioId; }
    public void setHorarioId(Long horarioId) { this.horarioId = horarioId; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
