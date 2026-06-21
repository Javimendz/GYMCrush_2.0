package com.example.skynet.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.skynet.R;
import com.example.skynet.ui.staff.StaffFragment;

import android.widget.TextView;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.PerfilResponseDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardFragment extends Fragment {

    private TextView tvUsuariosTotales, tvAforoActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        tvUsuariosTotales = view.findViewById(R.id.tvUsuariosActivos);
        tvAforoActual = view.findViewById(R.id.tvAforoActual);

        view.findViewById(R.id.cardManageUsers).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StaffFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.cardManageClasses).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Módulo de gestión de clases en desarrollo", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.cardMasterPlans).setOnClickListener(v -> {
             Toast.makeText(getContext(), "Módulo de planes maestros en desarrollo", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.cardSystemConfig).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Configuración del sistema", Toast.LENGTH_SHORT).show();
        });

        cargarEstadisticas();

        return view;
    }

    private void cargarEstadisticas() {
        // Cargar Usuarios Totales
        RetrofitClient.getApiService().findAllPerfiles().enqueue(new Callback<ApiResponseDto<List<PerfilResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Response<ApiResponseDto<List<PerfilResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PerfilResponseDto> perfiles = response.body().getDatos();
                    int total = perfiles != null ? perfiles.size() : 0;
                    tvUsuariosTotales.setText("Usuarios Totales: " + total);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Throwable t) {
                tvUsuariosTotales.setText("Usuarios Totales: Error");
            }
        });

        // Cargar Aforo Actual
        RetrofitClient.getApiService().obtenerAforoActual().enqueue(new Callback<ApiResponseDto<Long>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Long>> call, Response<ApiResponseDto<Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long aforo = response.body().getDatos();
                    tvAforoActual.setText("Aforo Actual: " + (aforo != null ? aforo : 0));
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Long>> call, Throwable t) {
                tvAforoActual.setText("Aforo Actual: Error");
            }
        });
    }
}
