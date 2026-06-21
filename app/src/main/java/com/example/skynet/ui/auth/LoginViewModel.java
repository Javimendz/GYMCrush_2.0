package com.example.skynet.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.skynet.data.remote.dto.AuthResponse;
import com.example.skynet.data.repository.AuthRepository;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<AuthResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LoginViewModel() {
        this.authRepository = new AuthRepository();
    }

    public LiveData<AuthResponse> getLoginResult() { return loginResult; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            error.setValue("Por favor, rellena todos los campos");
            return;
        }

        isLoading.setValue(true);

        authRepository.login(username, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthResponse authResponse) {
                isLoading.setValue(false);
                loginResult.setValue(authResponse);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.setValue(false);
                error.setValue(errorMessage);
            }
        });
    }
}