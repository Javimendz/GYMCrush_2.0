package com.example.skynet.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.SharedPreferences;
import com.example.skynet.R;
import com.example.skynet.ui.main.DesarrolloActivity;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.skynet.data.remote.dto.AuthResponse;
import com.google.android.material.button.MaterialButton;

public class EmailLoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private TextView tvIrARegistro;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_email);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initViews();
        setupStyledText();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsernameLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvIrARegistro = findViewById(R.id.tvIrARegistroEmail);

        ImageView ivBackground = findViewById(R.id.ivBackgroundEmail);
        if (ivBackground != null) {
            Glide.with(this)
                    .load("file:///android_asset/login.jpg")
                    .into(ivBackground);
        }
    }

    private void setupStyledText() {
        String text = "¿No tienes una cuenta? Regístrate";
        SpannableString ss = new SpannableString(text);
        
        int start = text.indexOf("Regístrate");
        if (start != -1) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(EmailLoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(@NonNull android.text.TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#3498db"));
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(clickableSpan, start, start + "Regístrate".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        tvIrARegistro.setText(ss);
        tvIrARegistro.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void observeViewModel() {
        loginViewModel.getLoginResult().observe(this, authResponse -> {
            if (authResponse != null) {
                Log.d("LOGIN_SUCCESS", "Usuario autenticado: " + authResponse.getUsername());
                
                // Guardar token y datos del usuario en SharedPreferences
                SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                
                editor.putString("auth_token", authResponse.getAccessToken());
                editor.putLong("user_id", authResponse.getId());
                editor.putString("nombre_usuario", authResponse.getNombre());
                editor.putString("username", authResponse.getUsername());
                
                if (authResponse.getRoles() != null) {
                    editor.putStringSet("roles", new HashSet<>(authResponse.getRoles()));
                }
                
                editor.apply();

                Toast.makeText(this, "¡Bienvenido " + authResponse.getNombre() + "!", Toast.LENGTH_SHORT).show();
                
                // Redirigir a DesarrolloActivity (que actúa como pantalla principal)
                Log.d("LOGIN_REDIRECT", "Redirigiendo a DesarrolloActivity...");
                Intent intent = new Intent(EmailLoginActivity.this, DesarrolloActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        loginViewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Log.e("LOGIN_ERROR", errorMessage);
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        loginViewModel.getIsLoading().observe(this, isLoading -> {
            btnLogin.setEnabled(!isLoading);
            btnLogin.setText(isLoading ? "CARGANDO..." : "INICIAR SESIÓN");
        });
    }

    private void loginExitoso(AuthResponse authData, String nombreFallback) {
        String nombreFinal = authData.getNombre();
        if (nombreFinal == null || nombreFinal.isEmpty() || nombreFinal.equalsIgnoreCase("admin") || nombreFinal.equalsIgnoreCase("user")) {
            nombreFinal = nombreFallback;
        }

        SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("auth_token", authData.getAccessToken());
        editor.putLong("user_id", authData.getId());
        editor.putString("nombre_usuario", nombreFinal);
        editor.putString("username", authData.getUsername());
        if (authData.getRoles() != null) {
            editor.putStringSet("roles", new HashSet<>(authData.getRoles()));
        }
        editor.apply();

        Toast.makeText(EmailLoginActivity.this, "Bienvenido, " + nombreFinal, Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(EmailLoginActivity.this, DesarrolloActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            loginViewModel.login(username, password);
        });

        findViewById(R.id.tvForgotPassword).setOnClickListener(v -> {
            Toast.makeText(this, "Próximamente: Recuperar contraseña", Toast.LENGTH_SHORT).show();
        });
    }
}