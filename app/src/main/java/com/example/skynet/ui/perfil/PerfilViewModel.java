package com.example.skynet.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.skynet.data.remote.dto.PerfilRequestDto;
import com.example.skynet.data.remote.dto.PerfilResponseDto;
import com.example.skynet.data.remote.dto.SaludRequestDto;
import com.example.skynet.data.remote.dto.SaludResponseDto;
import com.example.skynet.data.repository.PerfilRepository;

public class PerfilViewModel extends ViewModel {

    private final PerfilRepository perfilRepository;
    private final MutableLiveData<PerfilResponseDto> perfil = new MutableLiveData<>();
    private final MutableLiveData<SaludResponseDto> salud = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public PerfilViewModel() {
        this.perfilRepository = new PerfilRepository();
    }

    public LiveData<PerfilResponseDto> getPerfil() {
        return perfil;
    }

    public LiveData<SaludResponseDto> getSalud() {
        return salud;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void cargarDatosCompletos(Long id) {
        isLoading.setValue(true);
        // Cargar Perfil
        perfilRepository.getPerfil(id, new PerfilRepository.PerfilCallback<PerfilResponseDto>() {
            @Override
            public void onSuccess(PerfilResponseDto result) {
                perfil.setValue(result);
                // Una vez cargado el perfil, cargar salud
                cargarSalud(id);
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }

    private void cargarSalud(Long id) {
        perfilRepository.getSaludActual(id, new PerfilRepository.PerfilCallback<SaludResponseDto>() {
            @Override
            public void onSuccess(SaludResponseDto result) {
                salud.setValue(result);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                // No bloqueamos el flujo si no hay datos de salud
                isLoading.setValue(false);
            }
        });
    }

    public void actualizarPerfil(Long id, PerfilRequestDto request) {
        isLoading.setValue(true);
        perfilRepository.updatePerfil(id, request, new PerfilRepository.PerfilCallback<PerfilResponseDto>() {
            @Override
            public void onSuccess(PerfilResponseDto result) {
                perfil.setValue(result);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }

    public void registrarSalud(Long id, SaludRequestDto request) {
        isLoading.setValue(true);
        perfilRepository.registrarSalud(id, request, new PerfilRepository.PerfilCallback<SaludResponseDto>() {
            @Override
            public void onSuccess(SaludResponseDto result) {
                salud.setValue(result);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }
}
