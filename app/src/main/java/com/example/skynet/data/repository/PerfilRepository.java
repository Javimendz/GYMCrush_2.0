package com.example.skynet.data.repository;

import androidx.annotation.NonNull;

import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.PerfilRequestDto;
import com.example.skynet.data.remote.dto.PerfilResponseDto;
import com.example.skynet.data.remote.dto.SaludRequestDto;
import com.example.skynet.data.remote.dto.SaludResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio para gestionar las operaciones de perfil con el backend.
 */
public class PerfilRepository {

    private final ApiService apiService;

    public PerfilRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public interface PerfilCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    /**
     * Obtiene el perfil de un usuario por su ID.
     */
    public void getPerfil(Long id, PerfilCallback<PerfilResponseDto> callback) {
        android.util.Log.d("PerfilRepository", "Solicitando perfil para ID de usuario: " + id);
        apiService.getPerfilByUsuarioId(id).enqueue(new Callback<ApiResponseDto<PerfilResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<PerfilResponseDto>> call,
                                   @NonNull Response<ApiResponseDto<PerfilResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        android.util.Log.d("PerfilRepository", "Perfil obtenido con éxito para ID: " + id);
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        android.util.Log.e("PerfilRepository", "Error en respuesta de perfil: " + response.body().getMensaje());
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    android.util.Log.e("PerfilRepository", "Error HTTP en perfil: " + response.code());
                    callback.onError("Error al obtener perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<PerfilResponseDto>> call, @NonNull Throwable t) {
                android.util.Log.e("PerfilRepository", "Error de conexión en perfil", t);
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    /**
     * Actualiza el perfil de un usuario.
     */
    public void updatePerfil(Long id, PerfilRequestDto request, PerfilCallback<PerfilResponseDto> callback) {
        apiService.updatePerfil(id, request).enqueue(new Callback<ApiResponseDto<PerfilResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<PerfilResponseDto>> call,
                                   @NonNull Response<ApiResponseDto<PerfilResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    callback.onError("Error al actualizar perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<PerfilResponseDto>> call, @NonNull Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    /**
     * Obtiene los datos de salud actuales de un usuario.
     */
    public void getSaludActual(Long id, PerfilCallback<SaludResponseDto> callback) {
        android.util.Log.d("PerfilRepository", "Solicitando salud actual para ID: " + id);
        apiService.getSaludActual(id).enqueue(new Callback<ApiResponseDto<SaludResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<SaludResponseDto>> call,
                                   @NonNull Response<ApiResponseDto<SaludResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        android.util.Log.d("PerfilRepository", "Salud obtenida con éxito para ID: " + id);
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        android.util.Log.w("PerfilRepository", "Aviso en salud: " + response.body().getMensaje());
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    android.util.Log.e("PerfilRepository", "Error HTTP en salud: " + response.code());
                    callback.onError("No se encontraron datos de salud.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<SaludResponseDto>> call, @NonNull Throwable t) {
                android.util.Log.e("PerfilRepository", "Error de conexión en salud", t);
                callback.onError("Error de conexión salud: " + t.getMessage());
            }
        });
    }

    /**
     * Obtiene el historial de salud de un usuario.
     */
    public void getHistorialSalud(Long id, PerfilCallback<List<SaludResponseDto>> callback) {
        apiService.getHistorialSalud(id).enqueue(new Callback<ApiResponseDto<List<SaludResponseDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<SaludResponseDto>>> call,
                                   @NonNull Response<ApiResponseDto<List<SaludResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    callback.onError("Error al obtener historial: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<SaludResponseDto>>> call, @NonNull Throwable t) {
                callback.onError("Error de conexión historial: " + t.getMessage());
            }
        });
    }

    /**
     * Registra una nueva medición de salud.
     */
    public void registrarSalud(Long id, SaludRequestDto request, PerfilCallback<SaludResponseDto> callback) {
        apiService.registrarSalud(id, request).enqueue(new Callback<ApiResponseDto<SaludResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<SaludResponseDto>> call,
                                   @NonNull Response<ApiResponseDto<SaludResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    callback.onError("Error al registrar salud: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<SaludResponseDto>> call, @NonNull Throwable t) {
                callback.onError("Error de conexión al registrar salud: " + t.getMessage());
            }
        });
    }
}
