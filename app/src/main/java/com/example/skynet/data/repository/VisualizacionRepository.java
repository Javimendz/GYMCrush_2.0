package com.example.skynet.data.repository;

import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.TutorialResponseDto;
import com.example.skynet.data.remote.dto.VisualizacionRequestDto;
import com.example.skynet.data.remote.dto.VisualizacionResponseDto;

import java.util.List;

import retrofit2.Callback;

public class VisualizacionRepository {
    private final ApiService apiService;

    public VisualizacionRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void guardarProgreso(VisualizacionRequestDto request, Callback<ApiResponseDto<VisualizacionResponseDto>> callback) {
        apiService.guardarProgreso(request).enqueue(callback);
    }

    public void getHistorial(Long usuarioId, Callback<ApiResponseDto<List<VisualizacionResponseDto>>> callback) {
        apiService.getHistorialVisualizaciones(usuarioId).enqueue(callback);
    }

    public void getVideosPendientes(Long usuarioId, Callback<ApiResponseDto<List<VisualizacionResponseDto>>> callback) {
        apiService.getVideosPendientes(usuarioId).enqueue(callback);
    }

    public void getTutorialesPopulares(Callback<ApiResponseDto<List<TutorialResponseDto>>> callback) {
        apiService.getTutorialesPopulares().enqueue(callback);
    }

    public void eliminarVisualizacion(Long id, Callback<ApiResponseDto<Void>> callback) {
        apiService.eliminarVisualizacion(id).enqueue(callback);
    }
}
