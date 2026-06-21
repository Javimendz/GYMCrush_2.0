package com.example.skynet.data.repository;

import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.ComidaDiariaRequestDto;
import com.example.skynet.data.remote.dto.ComidaDiariaResponseDto;
import com.example.skynet.data.remote.dto.NutricionResponseDto;
import com.example.skynet.data.remote.dto.PageResponseDto;
import com.example.skynet.data.remote.dto.PerfilFisicoRequest;

import java.util.List;

import retrofit2.Callback;

public class NutricionRepository {
    private final ApiService apiService;

    public NutricionRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void generarPlan(PerfilFisicoRequest request, Callback<ApiResponseDto<NutricionResponseDto>> callback) {
        apiService.generarPlanNutricional(request).enqueue(callback);
    }

    public void obtenerUltimoPlan(Long usuarioId, Callback<ApiResponseDto<NutricionResponseDto>> callback) {
        apiService.obtenerUltimoPlan(usuarioId).enqueue(callback);
    }

    public void obtenerPlanPorId(Long planId, Callback<ApiResponseDto<NutricionResponseDto>> callback) {
        apiService.obtenerPlanPorId(planId).enqueue(callback);
    }

    public void obtenerHistorial(Long usuarioId, int page, int size, Callback<ApiResponseDto<PageResponseDto<NutricionResponseDto>>> callback) {
        apiService.obtenerHistorialNutricional(usuarioId, page, size).enqueue(callback);
    }

    public void añadirComida(ComidaDiariaRequestDto request, Callback<ApiResponseDto<ComidaDiariaResponseDto>> callback) {
        apiService.añadirComida(request).enqueue(callback);
    }

    public void eliminarComida(Long id, Callback<ApiResponseDto<Void>> callback) {
        apiService.eliminarComida(id).enqueue(callback);
    }

    public void guardarMuchasComidas(Long planId, List<ComidaDiariaRequestDto> comidas, Callback<ApiResponseDto<Void>> callback) {
        apiService.guardarMuchasComidas(planId, comidas).enqueue(callback);
    }

    public void buscarAlimentos(String query, Callback<ApiResponseDto<String>> callback) {
        apiService.buscarAlimentos(query).enqueue(callback);
    }

    public void actualizarNombrePlan(Long planId, String nombre, Callback<ApiResponseDto<NutricionResponseDto>> callback) {
        apiService.actualizarNombrePlan(planId, nombre).enqueue(callback);
    }

    public void guardarPlan(Long usuarioId, NutricionResponseDto plan, Callback<ApiResponseDto<NutricionResponseDto>> callback) {
        apiService.guardarPlan(usuarioId, plan).enqueue(callback);
    }
}