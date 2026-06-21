package com.example.skynet.data.repository;

import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.DietaResponseDto;

import java.util.List;

import retrofit2.Callback;

public class DietaRepository {
    private final ApiService apiService;

    public DietaRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void listarDietas(Callback<ApiResponseDto<List<DietaResponseDto>>> callback) {
        apiService.listarDietas().enqueue(callback);
    }

    public void obtenerDietaPorId(Long id, Callback<ApiResponseDto<DietaResponseDto>> callback) {
        apiService.obtenerDietaPorId(id).enqueue(callback);
    }
}
