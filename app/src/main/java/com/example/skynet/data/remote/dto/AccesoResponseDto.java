package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para la respuesta de historial de accesos.
 */
public class AccesoResponseDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("fechaHoraEntrada")
    private String fechaHoraEntrada;

    @SerializedName("fechaHoraSalida")
    private String fechaHoraSalida;

    @SerializedName("tipo")
    private String tipo; // ENTRADA o SALIDA

    @SerializedName("mensaje")
    private String mensaje;

    public AccesoResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFechaHoraEntrada() {
        return fechaHoraEntrada;
    }

    public void setFechaHoraEntrada(String fechaHoraEntrada) {
        this.fechaHoraEntrada = fechaHoraEntrada;
    }

    public String getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(String fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
