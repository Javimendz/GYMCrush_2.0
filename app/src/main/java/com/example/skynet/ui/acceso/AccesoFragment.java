package com.example.skynet.ui.acceso;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.ui.main.DesarrolloActivity;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.AccesoResponseDto;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccesoFragment extends Fragment {

    private ImageView ivQr;
    private TextView tvContador, tvAforo;
    private ProgressBar progressAforo;
    private CountDownTimer timer;
    private RecyclerView rvHistorial;
    private AccesoAdapter adapter;
    private String currentToken;
    private final android.os.Handler aforoHandler = new android.os.Handler();
    private final Runnable aforoRunnable = new Runnable() {
        @Override
        public void run() {
            cargarAforo();
            cargarHistorial(); // Ahora también actualiza el historial automáticamente
            aforoHandler.postDelayed(this, 3000); 
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acceso, container, false);

        ivQr = view.findViewById(R.id.ivQrAcceso);
        tvContador = view.findViewById(R.id.tvTimerQr);
        tvAforo = view.findViewById(R.id.tvAforoActual);
        progressAforo = view.findViewById(R.id.progressAforo);
        rvHistorial = view.findViewById(R.id.rvHistorialAccesos);

        adapter = new AccesoAdapter();
        rvHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistorial.setAdapter(adapter);

        view.findViewById(R.id.btnBackAcceso).setOnClickListener(v -> {
            if (getActivity() instanceof DesarrolloActivity) {
                ((DesarrolloActivity) getActivity()).mostrarHome();
            }
        });

        view.findViewById(R.id.btnRefrescarQr).setOnClickListener(v -> obtenerNuevoToken());
        view.findViewById(R.id.tvTestManual).setOnClickListener(v -> {
            if (currentToken != null) {
                probarTokenManualmente(currentToken);
            } else {
                Toast.makeText(getContext(), "Genera un token primero", Toast.LENGTH_SHORT).show();
            }
        });
        ivQr.setOnClickListener(v -> {
            if (currentToken != null) {
                mostrarQrAmpliado(currentToken);
            }
        });

        obtenerNuevoToken();
        // cargarHistorial(); // Ya no hace falta aquí, lo hace el aforoRunnable
        aforoHandler.post(aforoRunnable);

        return view;
    }

    private void mostrarQrAmpliado(String token) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_qr_gym);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageView ivAmpliado = dialog.findViewById(R.id.ivQrCode);
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(token, BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            ivAmpliado.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.btnCerrarQr).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void obtenerNuevoToken() {
        RetrofitClient.getApiService().generarTokenQr().enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentToken = response.body().getDatos();
                    generarImagenQr(currentToken);
                    iniciarContador();
                } else {
                    Toast.makeText(getContext(), "Error al obtener el token QR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generarImagenQr(String token) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(token, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            ivQr.setImageBitmap(bitmap);
            ivQr.setAlpha(1.0f);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void iniciarContador() {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (isAdded()) {
                    tvContador.setText("Expira en: " + millisUntilFinished / 1000 + "s");
                }
            }

            public void onFinish() {
                if (isAdded()) {
                    tvContador.setText("Token expirado. ¡Refresca!");
                    ivQr.setAlpha(0.3f);
                }
            }
        }.start();
    }

    private void cargarAforo() {
        RetrofitClient.getApiService().obtenerAforoActual().enqueue(new Callback<ApiResponseDto<Long>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Long>> call, Response<ApiResponseDto<Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    long personas = response.body().getDatos();
                    if (isAdded()) {
                        tvAforo.setText("Aforo actual: " + personas + " personas");
                        progressAforo.setProgress((int) personas);
                        // Log para depuración
                        android.util.Log.d("AforoUpdate", "Aforo recibido: " + personas);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Long>> call, Throwable t) {
                android.util.Log.e("AforoUpdate", "Error al obtener aforo: " + t.getMessage());
            }
        });
    }

    private void cargarHistorial() {
        RetrofitClient.getApiService().obtenerHistorialAccesos().enqueue(new Callback<ApiResponseDto<List<AccesoResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<AccesoResponseDto>>> call, Response<ApiResponseDto<List<AccesoResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setAccesos(response.body().getDatos());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<AccesoResponseDto>>> call, Throwable t) {
                // Silently fail or show message
            }
        });
    }

    private void probarTokenManualmente(String token) {
        RetrofitClient.getApiService().procesarQr(token).enqueue(new Callback<ApiResponseDto<AccesoResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<AccesoResponseDto>> call, Response<ApiResponseDto<AccesoResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseDto<AccesoResponseDto> apiResponse = response.body();
                    mostrarResultadoTest(apiResponse.getMensaje(), apiResponse.isSuccess());
                    if (apiResponse.isSuccess()) {
                        cargarAforo();
                        cargarHistorial();
                    }
                } else {
                    mostrarResultadoTest("Error en el servidor", false);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<AccesoResponseDto>> call, Throwable t) {
                mostrarResultadoTest("Error de conexión: " + t.getMessage(), false);
            }
        });
    }

    private void mostrarResultadoTest(String mensaje, boolean esExito) {
        if (isAdded()) {
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
        aforoHandler.removeCallbacks(aforoRunnable); // Detiene el polling
    }
}
