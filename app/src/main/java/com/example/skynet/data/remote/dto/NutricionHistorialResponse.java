package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NutricionHistorialResponse {

    @SerializedName("content")
    private List<NutricionResponseDto> content; // Aquí es donde realmente están los planes

    private int totalPages;
    private long totalElements;

    // Getters y Setters
    public List<NutricionResponseDto> getContent() { return content; }
}
