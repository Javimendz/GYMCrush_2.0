package com.example.skynet.data.remote.dto;

import java.util.List;

public class FaceLoginRequest {
    private String email;
    private List<Float> faceVector;

    public FaceLoginRequest(String email, List<Float> faceVector) {
        this.email = email;
        this.faceVector = faceVector;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Float> getFaceVector() {
        return faceVector;
    }

    public void setFaceVector(List<Float> faceVector) {
        this.faceVector = faceVector;
    }
}
