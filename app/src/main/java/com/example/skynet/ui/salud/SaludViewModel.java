package com.example.skynet.ui.salud;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.skynet.data.remote.dto.SaludRequestDto;
import com.example.skynet.data.remote.dto.SaludResponseDto;
import com.example.skynet.data.repository.PerfilRepository;

import java.util.List;

public class SaludViewModel extends ViewModel {

    private final PerfilRepository repository;
    private final MutableLiveData<SaludResponseDto> saludActual = new MutableLiveData<>();
    private final MutableLiveData<List<SaludResponseDto>> historialSalud = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public SaludViewModel() {
        this.repository = new PerfilRepository();
    }

    public LiveData<SaludResponseDto> getSaludActual() {
        return saludActual;
    }

    public LiveData<List<SaludResponseDto>> getHistorialSalud() {
        return historialSalud;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void cargarDatos(Long userId) {
        isLoading.setValue(true);
        
        // 1. Cargamos el historial de forma independiente
        cargarHistorial(userId);
        
        // 2. Cargamos el peso actual
        repository.getSaludActual(userId, new PerfilRepository.PerfilCallback<SaludResponseDto>() {
            @Override
            public void onSuccess(SaludResponseDto result) {
                saludActual.setValue(result);
            }

            @Override
            public void onError(String errorMessage) {
                // Silenciamos el 404 de "no encontrado" para datos actuales
                if (!errorMessage.contains("404") && !errorMessage.contains("No se encontraron")) {
                    error.setValue(errorMessage);
                }
                isLoading.setValue(false);
            }
        });
    }

    public void cargarHistorial(Long userId) {
        repository.getHistorialSalud(userId, new PerfilRepository.PerfilCallback<List<SaludResponseDto>>() {
            @Override
            public void onSuccess(List<SaludResponseDto> result) {
                historialSalud.setValue(result);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                // Silenciamos el 404 en el historial para evitar Toasts molestos al entrar
                if (!errorMessage.contains("404") && !errorMessage.contains("No se encontraron")) {
                    error.setValue(errorMessage);
                }
                isLoading.setValue(false);
            }
        });
    }

    public void registrarSalud(Long userId, SaludRequestDto request) {
        isLoading.setValue(true);
        repository.registrarSalud(userId, request, new PerfilRepository.PerfilCallback<SaludResponseDto>() {
            @Override
            public void onSuccess(SaludResponseDto result) {
                saludActual.setValue(result);
                cargarHistorial(userId);
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }
}
