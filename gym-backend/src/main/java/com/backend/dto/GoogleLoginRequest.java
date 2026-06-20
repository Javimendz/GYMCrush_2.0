package com.backend.dto;


import jakarta.validation.constraints.NotBlank;

public class GoogleLoginRequest {
    
    @NotBlank
    private String idToken;

    // Getters y Setters
    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
}