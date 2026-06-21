package com.example.skynet.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.core.splashscreen.SplashScreen;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.AuthResponse;
import com.example.skynet.data.remote.dto.GoogleLoginRequest;
import com.example.skynet.ui.main.DesarrolloActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private LoginViewModel loginViewModel;
    private TextView tvIrARegistro;
    private MaterialButton btnGoogleLogin, btnEmailLogin;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Manejar la pantalla de carga (Splash Screen)
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        
        // Mantener la Splash Screen del sistema por un tiempo determinado para asegurar que se vea la nuestra
        final long startTime = System.currentTimeMillis();
        splashScreen.setKeepOnScreenCondition(() -> {
            return System.currentTimeMillis() - startTime < 500; // 0.5 segundos de espera
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_principal); 

        // Inicializar vistas antes de la animación
        initViews();
        
        final View splashLogo = findViewById(R.id.layoutLogoSkynetSplash);
        final View mainLogo = findViewById(R.id.layoutLogoSkynet);

        // Configurar la transición personalizada
        splashScreen.setOnExitAnimationListener(splashScreenView -> {
            // Asegurarnos de que el logo de la splash sea visible al inicio de la animación
            splashLogo.setVisibility(View.VISIBLE);
            splashLogo.setAlpha(1f);
            
            // Animación del Logo Splash: Escalar masivamente (zoom in)
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(splashLogo, View.SCALE_X, 1f, 15f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(splashLogo, View.SCALE_Y, 1f, 15f);
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(splashLogo, View.ALPHA, 1f, 0f);
            
            // Animación del Logo Principal: Aparecer suavemente (fade in)
            ObjectAnimator logoFadeIn = ObjectAnimator.ofFloat(mainLogo, View.ALPHA, 0f, 1f);
            
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(800L); // Duración optimizada para suavidad
            animatorSet.setInterpolator(new AccelerateInterpolator(1.2f));
            animatorSet.playTogether(scaleX, scaleY, fadeOut, logoFadeIn);
            
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    splashLogo.setVisibility(View.GONE);
                    splashScreenView.remove();
                }
            });
            
            animatorSet.start();
        });

        // 2. Inicializar ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 3. Configurar Texto con Estilo (Spannable)
        setupStyledText();

        // 4. Configurar Eventos de Clic
        setupListeners();

        // Configurar Google Sign-In
        // NOTA: Para Google Auth se debe usar SIEMPRE el WEB CLIENT ID en requestIdToken, 
        // incluso en Android, para que el servidor pueda validar el token.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("765139895932-it22dhcqfe3mbbhgka7vuobea7no3sb5.apps.googleusercontent.com")
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        // Forzamos el cierre de sesión previo para que Google siempre muestre el selector de cuentas.
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            if (idToken != null) {
                // Enviar el token al servidor, pasando el objeto account para posibles redirecciones
                enviarTokenAlServidor(idToken, account.getDisplayName(), account);
            } else {
                Toast.makeText(this, "No se pudo obtener el token de Google", Toast.LENGTH_SHORT).show();
            }

        } catch (ApiException e) {
            // Error común: 10 (DEVELOPER_ERROR) -> Falta el SHA-1 en la consola de Google.
            Log.e("GOOGLE_LOGIN", "Error Google. Código: " + e.getStatusCode() + " - Mensaje: " + e.getMessage());
            Toast.makeText(this, "Error Google (" + e.getStatusCode() + "). Revisa la consola/SHA-1.", Toast.LENGTH_LONG).show();
        }
    }

    private void enviarTokenAlServidor(String idToken, String nombreFallback, GoogleSignInAccount account) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);

        RetrofitClient.getApiService().googleLogin(request)
                .enqueue(new Callback<ApiResponseDto<AuthResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponseDto<AuthResponse>> call,
                                           @NonNull Response<ApiResponseDto<AuthResponse>> response) {

                        if (response.isSuccessful()) {
                            // Si el login es exitoso, redirigimos a la pantalla principal
                            loginExitoso(response.body().getDatos(), nombreFallback);
                        } else {
                            // 404: No existe en la BD -> Redirigir directamente a registro
                            if (response.code() == 404) {
                                irARegistroConGoogle(account);
                            } else if (response.code() == 409) {
                                // 409 Conflict: El email ya existe con otro método de login (Manual)
                                mostrarDialogoEmailYaExiste();
                            } else {
                                Toast.makeText(LoginActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponseDto<AuthResponse>> call, @NonNull Throwable t) {
                        Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDialogoCuentaYaRegistradaPrincipal() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cuenta ya registrada")
                .setMessage("Esta cuenta de Google ya está vinculada a un usuario. Por favor, utiliza el enlace de abajo ('Inicio sesión Facial o Email') para entrar.")
                .setPositiveButton("Entendido", null)
                .show();
    }

    private void loginExitoso(AuthResponse authData, String nombreFallback) {
        String nombreFinal = authData.getNombre();
        if (nombreFinal == null || nombreFinal.isEmpty() || nombreFinal.equalsIgnoreCase("admin") || nombreFinal.equalsIgnoreCase("user")) {
            nombreFinal = nombreFallback;
        }

        android.content.SharedPreferences.Editor editor = getSharedPreferences("DatosUsuario", MODE_PRIVATE).edit();
        editor.putString("auth_token", authData.getAccessToken());
        editor.putLong("user_id", authData.getId());
        editor.putString("nombre_usuario", nombreFinal);
        editor.apply();

        Toast.makeText(LoginActivity.this, "Bienvenido, " + nombreFinal, Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(LoginActivity.this, DesarrolloActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void irARegistroConGoogle(GoogleSignInAccount account) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        // Pasamos los datos extraídos de Google para pre-rellenar el formulario en RegisterActivity
        intent.putExtra("google_email", account.getEmail());
        intent.putExtra("google_name", account.getDisplayName());
        startActivity(intent);
    }

    private void mostrarDialogoEmailYaExiste() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cuenta ya existente")
                .setMessage("Este correo electrónico ya está registrado de forma manual. Por favor, inicia sesión con tu contraseña o biometría.")
                .setPositiveButton("Entendido", null)
                .show();
    }
    private void initViews() {
        tvIrARegistro = findViewById(R.id.tvIrARegistro);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnEmailLogin = findViewById(R.id.btnEmailLogin);

        ImageView ivBackground = findViewById(R.id.ivBackground);
        if (ivBackground != null) {
            Glide.with(this)
                    .load("file:///android_asset/login.jpg")
                    .into(ivBackground);
        }
    }

    private void setupStyledText() {
        String text = "¿Tienes una cuenta? Inicio sesión Facial o Email";
        SpannableString ss = new SpannableString(text);
        
        // Enlace para "Facial"
        int facialStart = text.indexOf("Facial");
        if (facialStart != -1) {
            ClickableSpan facialClick = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(LoginActivity.this, FaceLoginActivity.class);
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(@NonNull android.text.TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#3498db"));
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(facialClick, facialStart, facialStart + "Facial".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Enlace para "Email"
        int emailStart = text.indexOf("Email");
        if (emailStart != -1) {
            ClickableSpan emailClick = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(LoginActivity.this, EmailLoginActivity.class);
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(@NonNull android.text.TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#3498db"));
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(emailClick, emailStart, emailStart + "Email".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        tvIrARegistro.setText(ss);
        tvIrARegistro.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupListeners() {
        // Botón de Google
        btnGoogleLogin.setOnClickListener(v -> {
            signInWithGoogle();
        });

        // Botón de Email -> Va a la pantalla de Registro
        btnEmailLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // El listener general de tvIrARegistro se elimina o se deja vacío para que los ClickableSpans tomen prioridad
        tvIrARegistro.setOnClickListener(null);
    }
}