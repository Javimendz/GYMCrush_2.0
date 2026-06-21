package com.example.skynet.data.model;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    // SerializedName asegura que coincida con el nombre exacto que envía el JSON del Backend
    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    // No solemos traer el password por seguridad, pero si lo necesitas:
    @SerializedName("password")
    private String password;

    @SerializedName("qr_token")
    private String qrToken;

    @SerializedName("fecha_registro")
    private String fechaRegistro; // Lo recibimos como String para manejarlo más fácil

    // Relación 1:1 con Perfil (necesitarás crear también la clase Perfil)
    @SerializedName("perfil")
    private Perfil perfil;

    // Constructor vacío requerido por GSON
    public Usuario() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getCorreo() { return email; }
    public void setCorreo(String correo) { this.email = email; }

    public String getQrToken() { return qrToken; }
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}