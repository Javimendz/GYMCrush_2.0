package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ReservaResponseDto {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("fecha")
    private String fecha;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("username")
    private String username;

    @SerializedName("nombreActividad")
    private String nombreActividad;
    
    @SerializedName("diaSemana")
    private String diaSemana;

    @SerializedName("horaInicio")
    private String horaInicio;

    @SerializedName("nombreSala")
    private String nombreSala;

    @SerializedName("usuarioId")
    private Long usuarioId;

    @SerializedName("horarioId")
    private Long horarioId;

    // Getters
    public Long getId() { return id; }
    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public String getUsername() { return username; }
    public String getNombreActividad() { return nombreActividad; }
    public String getDiaSemana() { return diaSemana; }
    public String getHoraInicio() { return horaInicio; }
    public String getNombreSala() { return nombreSala; }
    public Long getUsuarioId() { return usuarioId; }
    public Long getHorarioId() { return horarioId; }
}
