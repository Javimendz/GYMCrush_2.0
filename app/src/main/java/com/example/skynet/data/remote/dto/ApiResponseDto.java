package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper genérico para las respuestas de la API de Spring Boot.
 * @param <T> El tipo de dato que contiene el campo 'datos' (AuthResponse, Usuario, etc.)
 */
public class ApiResponseDto<T> {

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("datos")
    private T datos;

    @SerializedName("success")
    private boolean success;

    // Constructor vacío requerido por GSON
    public ApiResponseDto() {
    }

    // Getters
    public String getMensaje() {
        return mensaje;
    }

    public T getDatos() {
        return datos;
    }

    public boolean isSuccess() {
        return success;
    }

    // Setters (Opcionales, útiles si necesitas manipular la respuesta en el repositorio)
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setDatos(T datos) { this.datos = datos; }
    public void setSuccess(boolean success) { this.success = success; }
}