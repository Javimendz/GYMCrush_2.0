package com.example.skynet.data.repository;

import androidx.annotation.NonNull;
import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.HorarioResponseDto;
import com.example.skynet.data.remote.dto.ReservaResponseDto;
import com.example.skynet.data.remote.dto.SalaResponseDto;
import com.example.skynet.data.remote.dto.EntrenamientoResponseDto;
import com.example.skynet.data.model.Reserva;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservasRepository {
    private static ReservasRepository instance;
    private final ApiService apiService;
    private final List<Reserva> misReservasLocal = new ArrayList<>();

    private ReservasRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public static synchronized ReservasRepository getInstance() {
        if (instance == null) {
            instance = new ReservasRepository();
        }
        return instance;
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    // --- HORARIOS ---
    public void getHorariosPorDia(String dia, RepositoryCallback<List<HorarioResponseDto>> callback) {
        apiService.getHorariosPorDia(dia).enqueue(new Callback<ApiResponseDto<List<HorarioResponseDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<HorarioResponseDto>>> call, @NonNull Response<ApiResponseDto<List<HorarioResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    callback.onError("Error al obtener horarios");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<HorarioResponseDto>>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // --- RESERVAS ---
    public void crearReserva(Long usuarioId, Long horarioId, RepositoryCallback<ReservaResponseDto> callback) {
        apiService.crearReserva(usuarioId, horarioId).enqueue(new Callback<ApiResponseDto<ReservaResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<ReservaResponseDto>> call, @NonNull Response<ApiResponseDto<ReservaResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    String errorMsg = "No se pudo realizar la reserva";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(errorBody).getAsJsonObject();
                            if (json.has("mensaje")) {
                                errorMsg = json.get("mensaje").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        errorMsg = "Error en el servidor: " + response.code();
                    }
                    callback.onError(errorMsg);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<ReservaResponseDto>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getMisReservas(Long usuarioId, RepositoryCallback<List<ReservaResponseDto>> callback) {
        apiService.getMisReservas(usuarioId).enqueue(new Callback<ApiResponseDto<List<ReservaResponseDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<ReservaResponseDto>>> call, @NonNull Response<ApiResponseDto<List<ReservaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    callback.onError("Error al obtener tus reservas");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<ReservaResponseDto>>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getTodasLasReservasPorDia(String dia, RepositoryCallback<List<ReservaResponseDto>> callback) {
        apiService.getReservasPorDia(dia).enqueue(new Callback<ApiResponseDto<List<ReservaResponseDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<ReservaResponseDto>>> call, @NonNull Response<ApiResponseDto<List<ReservaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getDatos());
                } else {
                    callback.onError("Error al obtener reservas");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<ReservaResponseDto>>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getAsistentesClase(Long horarioId, RepositoryCallback<List<ReservaResponseDto>> callback) {
        apiService.obtenerAsistentesClase(horarioId).enqueue(new Callback<ApiResponseDto<List<ReservaResponseDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<ReservaResponseDto>>> call, @NonNull Response<ApiResponseDto<List<ReservaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getDatos());
                } else {
                    callback.onError("Error al obtener asistentes");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<ReservaResponseDto>>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void cancelarReserva(Long reservaId, RepositoryCallback<Void> callback) {
        apiService.cancelarReserva(reservaId).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<Void>> call, @NonNull Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    callback.onError("Error al cancelar la reserva");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<Void>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // --- MÉTODOS LOCALES (PARA PRUEBAS/MOCK) ---
    public void agregarReserva(Reserva reserva) {
        misReservasLocal.add(reserva);
    }

    public List<Reserva> getMisReservasLocal() {
        return misReservasLocal;
    }

    // --- SALAS ---
    public void getSalasActivas(RepositoryCallback<List<SalaResponseDto>> callback) {
        apiService.getSalasActivas().enqueue(new Callback<ApiResponseDto<List<SalaResponseDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<SalaResponseDto>>> call, @NonNull Response<ApiResponseDto<List<SalaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    callback.onError("Error al obtener salas");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<SalaResponseDto>>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // --- ENTRENAMIENTOS ---
    public void getEntrenamientosUsuario(Long usuarioId, RepositoryCallback<List<EntrenamientoResponseDto>> callback) {
        apiService.getEntrenamientosUsuario(usuarioId).enqueue(new Callback<ApiResponseDto<List<EntrenamientoResponseDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, @NonNull Response<ApiResponseDto<List<EntrenamientoResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    callback.onError("Error al obtener entrenamientos");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}