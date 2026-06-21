package com.example.skynet.ui.ejercicios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.TutorialResponseDto;
import com.example.skynet.data.remote.dto.VisualizacionRequestDto;
import com.example.skynet.data.remote.dto.VisualizacionResponseDto;
import com.example.skynet.data.repository.VisualizacionRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TutorialViewModel extends ViewModel {
    private final VisualizacionRepository repository;
    private final MutableLiveData<List<VisualizacionResponseDto>> pendientes = new MutableLiveData<>();
    private final MutableLiveData<List<TutorialResponseDto>> populares = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public TutorialViewModel() {
        this.repository = new VisualizacionRepository();
    }

    public LiveData<List<VisualizacionResponseDto>> getPendientes() { return pendientes; }
    public LiveData<List<TutorialResponseDto>> getPopulares() { return populares; }
    public LiveData<String> getError() { return error; }

    public void guardarProgreso(Long usuarioId, Long tutorialId, int progresoSegundos) {
        VisualizacionRequestDto request = new VisualizacionRequestDto();
        request.setUsuarioId(usuarioId);
        request.setTutorialId(tutorialId);
        request.setProgresoSegundos(progresoSegundos);

        repository.guardarProgreso(request, new Callback<ApiResponseDto<VisualizacionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<VisualizacionResponseDto>> call, Response<ApiResponseDto<VisualizacionResponseDto>> response) {
                if (!response.isSuccessful()) {
                    error.postValue("Error al guardar progreso: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<VisualizacionResponseDto>> call, Throwable t) {
                error.postValue("Fallo de red: " + t.getMessage());
            }
        });
    }

    public void cargarPendientes(Long usuarioId) {
        repository.getVideosPendientes(usuarioId, new Callback<ApiResponseDto<List<VisualizacionResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<VisualizacionResponseDto>>> call, Response<ApiResponseDto<List<VisualizacionResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pendientes.postValue(response.body().getDatos());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<VisualizacionResponseDto>>> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    public void cargarPopulares() {
        repository.getTutorialesPopulares(new Callback<ApiResponseDto<List<TutorialResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<TutorialResponseDto>>> call, Response<ApiResponseDto<List<TutorialResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populares.postValue(response.body().getDatos());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<TutorialResponseDto>>> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }
}
