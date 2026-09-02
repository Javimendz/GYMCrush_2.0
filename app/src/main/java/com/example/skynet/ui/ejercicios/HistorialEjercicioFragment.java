package com.example.skynet.ui.ejercicios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.EjercicioHistorialDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialEjercicioFragment extends Fragment {

    private RecyclerView rvHistorial;
    private HistorialEjercicioAdapter adapter;
    private Ejercicio ejercicio;

    public static HistorialEjercicioFragment newInstance(Ejercicio ejercicio) {
        HistorialEjercicioFragment fragment = new HistorialEjercicioFragment();
        Bundle args = new Bundle();
        args.putParcelable("ejercicio", ejercicio);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_ejercicio, container, false);

        if (getArguments() != null) {
            ejercicio = getArguments().getParcelable("ejercicio");
        }

        rvHistorial = view.findViewById(R.id.rvHistorialEjercicio);
        rvHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        
        cargarHistorial();

        return view;
    }

    private void cargarHistorial() {
        if (ejercicio == null) return;
        
        if (ejercicio.getId() == null || ejercicio.getId() <= 0) {
            android.util.Log.e("HistorialEjercicio", "El ejercicio no tiene ID válida para cargar historial.");
            return;
        }

        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", android.content.Context.MODE_PRIVATE);
        long usuarioId = prefs.getLong("user_id", -1L);

        if (usuarioId == -1L) {
            Toast.makeText(getContext(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getHistorialEjercicio(ejercicio.getId(), usuarioId).enqueue(new Callback<List<EjercicioHistorialDto>>() {
            @Override
            public void onResponse(Call<List<EjercicioHistorialDto>> call, Response<List<EjercicioHistorialDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EjercicioHistorialDto> historial = response.body();
                    if (historial != null && !historial.isEmpty()) {
                        adapter = new HistorialEjercicioAdapter(historial);
                        rvHistorial.setAdapter(adapter);
                    } else {
                        android.util.Log.d("HistorialEjercicio", "No hay registros históricos para este ejercicio.");
                    }
                } else {
                    android.util.Log.e("HistorialEjercicio", "Error servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<EjercicioHistorialDto>> call, Throwable t) {
                android.util.Log.e("HistorialEjercicio", "Error de red", t);
                if (isAdded()) {
                    Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}