package com.example.skynet.data.repository;

import androidx.annotation.NonNull;

import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.AuthResponse;
import com.example.skynet.data.remote.dto.LoginRequest;
import com.example.skynet.data.remote.dto.RegisterRequest;
import com.example.skynet.data.remote.dto.UsuarioResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio dedicado exclusivamente a la autenticación (Login y Registro).
 */
public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    /**
     * Interfaz para comunicar el resultado del login al ViewModel
     */
    public interface AuthCallback {
        void onSuccess(AuthResponse authResponse);
        void onError(String errorMessage);
    }

    /**
     * Interfaz para comunicar el resultado del registro al ViewModel
     */
    public interface RegisterCallback {
        void onSuccess(UsuarioResponseDto usuario);
        void onError(String errorMessage);
    }

    /**
     * Realiza la petición de login al servidor de Spring Boot
     */
    public void login(String username, String password, AuthCallback callback) {
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.login(loginRequest).enqueue(new Callback<ApiResponseDto<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<AuthResponse>> call,
                                   @NonNull Response<ApiResponseDto<AuthResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseDto<AuthResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getDatos());
                    } else {
                        String mensaje = apiResponse.getMensaje();
                        callback.onError(mensaje != null ? mensaje : "Error desconocido");
                    }
                } else {
                    if (response.code() == 401) {
                        callback.onError("Credenciales inválidas. Revisa tu usuario o contraseña.");
                    } else {
                        callback.onError("Error en el servidor: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<AuthResponse>> call, @NonNull Throwable t) {
                callback.onError("No se pudo conectar con el servidor. Verifica tu conexión.");
            }
        });
    }

    /**
     * Realiza la petición de registro al servidor usando el objeto RegisterRequest completo
     */
    public void register(RegisterRequest registerRequest, RegisterCallback callback) {
        apiService.register(registerRequest).enqueue(new Callback<ApiResponseDto<UsuarioResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<UsuarioResponseDto>> call, @NonNull Response<ApiResponseDto<UsuarioResponseDto>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getDatos());
                    } else {
                        callback.onError(response.body().getMensaje());
                    }
                } else {
                    // ERROR (400, 409, 500, etc.)
                    String mensajeError = "Error desconocido";

                    try {
                        // Intentamos parsear el errorBody que viene del servidor
                        if (response.errorBody() != null) {
                            // Usamos Gson para convertir el errorBody en tu clase ApiResponseDto
                            com.google.gson.Gson gson = new com.google.gson.Gson();
                            ApiResponseDto<?> errorResponse = gson.fromJson(
                                    response.errorBody().charStream(),
                                    ApiResponseDto.class
                            );

                            if (errorResponse != null && errorResponse.getMensaje() != null) {
                                mensajeError = errorResponse.getMensaje();
                            }
                        }
                    } catch (Exception e) {
                        mensajeError = "Error al procesar respuesta del servidor";
                    }

                    // Aquí enviamos el mensaje específico que vino del backend
                    callback.onError(mensajeError);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<UsuarioResponseDto>> call, @NonNull Throwable t) {
                String errorMsg = t.getMessage() != null ? t.getMessage() : "desconocido";
                callback.onError("Error de conexión: " + errorMsg);
            }
        });
    }

    /**
     * Método sobrecargado para compatibilidad (si es necesario) que crea un request básico.
     * @deprecated Usar register(RegisterRequest, RegisterCallback) en su lugar.
     */
    @Deprecated
    public void register(String username, String password, RegisterCallback callback) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        register(request, callback);
    }
}
