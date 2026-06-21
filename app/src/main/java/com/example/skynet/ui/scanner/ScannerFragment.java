package com.example.skynet.ui.scanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import android.graphics.drawable.GradientDrawable;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerFragment extends Fragment {

    private BarcodeView barcodeScanner;
    private ProgressBar progressBar;
    private Button btnFlash;
    private View viewOverlay;
    private MaterialButtonToggleGroup toggleGroup;
    private ToneGenerator toneGenerator;
    private boolean isFlashOn = false;
    private boolean isProcessing = false;

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null && !isProcessing) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> procesarToken(result.getText()));
                }
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // Requerido por la interfaz BarcodeCallback
        }
    };

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    barcodeScanner.resume();
                    barcodeScanner.decodeContinuous(callback);
                } else {
                    Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        barcodeScanner = view.findViewById(R.id.barcodeScanner);
        progressBar = view.findViewById(R.id.progressScanner);
        btnFlash = view.findViewById(R.id.btnToggleFlash);
        toggleGroup = view.findViewById(R.id.toggleGroupAcceso);
        viewOverlay = view.findViewById(R.id.viewScannerOverlay);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                actualizarInterfazModo(checkedId == R.id.btnModoEntrada);
            }
        });

        // Configuración inicial (Entrada por defecto)
        actualizarInterfazModo(true);

        toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

        btnFlash.setOnClickListener(v -> {
            isFlashOn = !isFlashOn;
            barcodeScanner.setTorch(isFlashOn);
            btnFlash.setText(isFlashOn ? "Apagar Linterna" : "Encender Linterna");
        });

        checkCameraPermission();

        return view;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void actualizarInterfazModo(boolean esEntrada) {
        int color = esEntrada ? Color.parseColor("#4CAF50") : Color.parseColor("#FF4081");
        if (viewOverlay.getBackground() instanceof GradientDrawable) {
            GradientDrawable drawable = (GradientDrawable) viewOverlay.getBackground();
            drawable.setStroke((int) (4 * getResources().getDisplayMetrics().density), color);
        }
    }

    private void procesarToken(String token) {
        isProcessing = true;
        progressBar.setVisibility(View.VISIBLE);
        
        // Sonido de confirmación
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
        }

        boolean esSalida = toggleGroup.getCheckedButtonId() == R.id.btnModoSalida;
        Map<String, String> body = new HashMap<>();
        body.put("token", token);

        Call<ApiResponseDto<Void>> call;
        if (esSalida) {
            call = RetrofitClient.getApiService().validarSalida(body);
        } else {
            call = RetrofitClient.getApiService().validarEntrada(body);
        }

        call.enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseDto<Void> apiResponse = response.body();
                    String titulo = esSalida ? "Salida Registrada" : "Entrada Registrada";
                    mostrarDialogoExito(titulo, apiResponse.getMensaje());
                } else {
                    manejarErrorServidor(response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void mostrarDialogoExito(String titulo, String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setMessage(mensaje != null ? mensaje : "Operación completada con éxito")
                .setPositiveButton("Siguiente", (dialog, which) -> isProcessing = false)
                .setCancelable(false)
                .show();
    }

    private void manejarErrorServidor(Response<?> response) {
        String errorMsg = "Error: " + response.code();
        try {
            if (response.errorBody() != null) {
                errorMsg = response.errorBody().string();
            }
        } catch (Exception ignored) {}
        mostrarError(errorMsg);
    }

    private void mostrarError(String error) {
        if (!isAdded()) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Error de Acceso")
                .setMessage(error)
                .setPositiveButton("Reintentar", (dialog, which) -> isProcessing = false)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            barcodeScanner.resume();
            barcodeScanner.decodeContinuous(callback);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScanner.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }
    }
}
