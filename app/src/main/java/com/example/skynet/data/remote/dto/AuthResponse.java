package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * DTO que recibe la respuesta del servidor tras un login exitoso.
 * Contiene el Token JWT necesario para las demás peticiones.
 */
public class AuthResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("roles")
    private List<String> roles;

    @SerializedName("accessToken")
    private String accessToken; // El Token JWT

    @SerializedName("tokenType")
    private String tokenType; // "Bearer"

    // Constructor vacío para GSON
    public AuthResponse() {}

    // --- GETTERS ---

    public Long getId() { return id; }

    public String getUsername() { return username; }

    public String getNombre() { return nombre; }

    public List<String> getRoles() { return roles; }

    public String getAccessToken() { return accessToken; }

    public String getTokenType() { return tokenType; }

    // Método de utilidad para obtener el Header completo de autorización
    public String getFullToken() {
        return tokenType + " " + accessToken;
    }
}