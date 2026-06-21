package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class NotificacionResponseDto {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("titulo")
    private String titulo;
    
    @SerializedName("mensaje")
    private String mensaje;
    
    @SerializedName("fecha")
    private String fecha;
    
    @SerializedName("leido")
    private boolean leido;
    
    @SerializedName("tipo")
    private TipoNotificacion tipo;

    // Getters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public String getFecha() { return fecha; }
    public boolean isLeido() { return leido; }
    public TipoNotificacion getTipo() { return tipo; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setLeido(boolean leido) { this.leido = leido; }
    public void setTipo(TipoNotificacion tipo) { this.tipo = tipo; }
}
