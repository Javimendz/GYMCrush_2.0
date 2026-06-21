package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Objeto para enviar las credenciales al endpoint /api/v1/auth/login
 */
public class LoginRequest {

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}