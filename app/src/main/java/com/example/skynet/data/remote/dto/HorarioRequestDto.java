package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para enviar solicitudes de creación/actualización de horarios al Backend.
 */
public class HorarioRequestDto {
    @SerializedName("diaSemana")
    private String diaSemana;

    @SerializedName("horaInicio")
    private String horaInicio;

    @SerializedName("horaFin")
    private String horaFin;

    @SerializedName("aforoMax")
    private Integer aforoMax;

    @SerializedName("actividadId")
    private Long actividadId;

    @SerializedName("salaId")
    private Long salaId;

    @SerializedName("entrenadorId")
    private Long entrenadorId;

    public HorarioRequestDto() {}

    public HorarioRequestDto(String diaSemana, String horaInicio, String horaFin, Integer aforoMax, Long actividadId, Long salaId, Long entrenadorId) {
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.aforoMax = aforoMax;
        this.actividadId = actividadId;
        this.salaId = salaId;
        this.entrenadorId = entrenadorId;
    }

    // Getters and Setters
    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public Integer getAforoMax() { return aforoMax; }
    public void setAforoMax(Integer aforoMax) { this.aforoMax = aforoMax; }

    public Long getActividadId() { return actividadId; }
    public void setActividadId(Long actividadId) { this.actividadId = actividadId; }

    public Long getSalaId() { return salaId; }
    public void setSalaId(Long salaId) { this.salaId = salaId; }

    public Long getEntrenadorId() { return entrenadorId; }
    public void setEntrenadorId(Long entrenadorId) { this.entrenadorId = entrenadorId; }
}
