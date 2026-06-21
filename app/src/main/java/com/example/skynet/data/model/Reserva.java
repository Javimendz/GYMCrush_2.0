package com.example.skynet.data.model;

import com.example.skynet.data.remote.dto.HorarioResponseDto;
import com.google.gson.annotations.SerializedName;

public class Reserva {

    @SerializedName("id")
    private Long id;

    @SerializedName("fecha")
    private String fecha; // Recibido como "yyyy-MM-dd"

    @SerializedName("estado")
    private String estado; // Ejemplo: "CONFIRMADA", "CANCELADA"

    @SerializedName("confirmado")
    private Boolean confirmado;

    // Relación con Horario (necesitarás este DTO para saber qué clase es: CrossFit, Yoga, etc.)
    @SerializedName("horario")
    private HorarioResponseDto horario;

    // Constructor vacío
    public Reserva() {}

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Boolean getConfirmado() { return confirmado; }
    public void setConfirmado(Boolean confirmado) { this.confirmado = confirmado; }

    public HorarioResponseDto getHorario() { return horario; }
    public void setHorario(HorarioResponseDto horario) { this.horario = horario; }
}