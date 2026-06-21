package com.example.skynet.data.remote.dto;

public class GoogleLoginRequest {
    private String idToken;

    public GoogleLoginRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
