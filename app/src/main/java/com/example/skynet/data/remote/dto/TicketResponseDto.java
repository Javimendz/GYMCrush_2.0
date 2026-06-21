package com.example.skynet.data.remote.dto;

public class TicketResponseDto {
    private Long id;
    private String asunto;
    private String mensaje;
    private String estado;
    private String fechaCreacion;
    private String fechaResolucion;
    private String username;
    private Long usuarioId;
    private String usuarioEmail;
    private String nombreCompletoUsuario;

    // Getters
    public Long getId() { return id; }
    public String getAsunto() { return asunto; }
    public String getMensaje() { return mensaje; }
    public String getEstado() { return estado; }
    public String getFechaCreacion() { return fechaCreacion; }
    public String getFechaResolucion() { return fechaResolucion; }
    public String getUsername() { return username; }
    public Long getUsuarioId() { return usuarioId; }
    public String getUsuarioEmail() { return usuarioEmail; }
    public String getNombreCompletoUsuario() { return nombreCompletoUsuario; }
}
