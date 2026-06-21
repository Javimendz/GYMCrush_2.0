package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para recibir la respuesta de horarios desde el Backend.
 * Sincronizado con el backend para usar plazasLibres.
 */
public class HorarioResponseDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("diaSemana")
    private String diaSemana;

    @SerializedName("horaInicio")
    private String horaInicio; 

    @SerializedName("horaFin")
    private String horaFin;

    @SerializedName("plazasLibres")
    private Integer plazasLibres;

    @SerializedName("aforoMax")
    private Integer aforoMax;



    @SerializedName("nombreSala")
    private String nombreSala;

    @SerializedName("actividadId")
    private Long actividadId;

    @SerializedName("nombreActividad")
    private String nombreActividad;

    @SerializedName("salaId")
    private Long salaId;

    @SerializedName("nombreEntrenador")
    private String nombreEntrenador;

    public HorarioResponseDto() {}

    public Long getId() { return id; }
    public String getDiaSemana() { return diaSemana; }

    public String getHoraInicio() {
        if (horaInicio != null && horaInicio.length() > 5) return horaInicio.substring(0, 5);
        return horaInicio;
    }

    public String getHoraFin() {
        if (horaFin != null && horaFin.length() > 5) return horaFin.substring(0, 5);
        return horaFin;
    }
    public void setPlazasLibres(Integer plazasLibres) {
        this.plazasLibres = plazasLibres;
    }

    public Integer getOcupacion() {
        int aforo = (aforoMax != null) ? aforoMax : 0;
        return aforo - getPlazasLibres();
    }

    public Integer getAforoMax() { return aforoMax; }
    public Integer getPlazasLibres() {
        return (plazasLibres != null) ? plazasLibres : (aforoMax != null ? aforoMax : 0);
    }


    public String getNombreSala() { return nombreSala; }
    public Long getActividadId() { return actividadId; }
    public String getNombreActividad() { return nombreActividad; }
    public Long getSalaId() { return salaId; }
    public String getNombreEntrenador() { return nombreEntrenador; }
}
