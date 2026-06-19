package com.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class FaceLoginRequest {
    private String email;
    private List<Float> faceVector; // El vector numérico que viene desde Android
}