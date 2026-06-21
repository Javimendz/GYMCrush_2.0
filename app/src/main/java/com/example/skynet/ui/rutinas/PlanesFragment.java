package com.example.skynet.ui.rutinas;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.PlanResponseDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanesFragment extends Fragment {

    private RecyclerView rvPlanes;
    private FloatingActionButton fabAddPlan;
    private PlanesAdapter adapter;
    private long userId;
    private boolean isAdmin = false;

    private final ActivityResultLauncher<Intent> startCrearPlan = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    cargarPlanes();
                    if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                        ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).crearNotificacionManual(
                                "Nuevo Plan",
                                "Has creado un nuevo plan de entrenamiento correctamente."
                        );
                    }
                }
            }
    );

    public interface OnPlanSubscriptionListener {
        void onPlanSubscribed();
    }
    private OnPlanSubscriptionListener subscriptionListener;

    public void setOnPlanSubscriptionListener(OnPlanSubscriptionListener listener) {
        this.subscriptionListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planes_entrenamiento, container, false);

        rvPlanes = view.findViewById(R.id.rvPlanesEntrenamiento);
        fabAddPlan = view.findViewById(R.id.fabAddPlan);
        rvPlanes.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        // El botón FAB se oculta en esta vista según requerimiento UI
        fabAddPlan.setVisibility(View.GONE);
        fabAddPlan.setOnClickListener(v -> startCrearPlan.launch(new Intent(requireContext(), CrearPlanActivity.class)));

        View btnNuevoPlanToolbar = view.findViewById(R.id.btnNuevoPlanToolbar);
        if (btnNuevoPlanToolbar != null) {
            btnNuevoPlanToolbar.setVisibility(View.VISIBLE);
            btnNuevoPlanToolbar.setOnClickListener(v -> startCrearPlan.launch(new Intent(requireContext(), CrearPlanActivity.class)));
        }

        adapter = new PlanesAdapter(new ArrayList<>(), new PlanesAdapter.OnPlanClickListener() {
            @Override
            public void onSuscribir(PlanResponseDto plan) {
                suscribirUsuario(plan.getId());
            }

            @Override
            public void onEditar(PlanResponseDto plan) {
                // Diálogo para elegir entre editar detalles o gestionar ejercicios
                String[] opciones = {"Editar Información", "Gestionar Ejercicios"};
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("¿Qué deseas hacer?")
                        .setItems(opciones, (dialog, which) -> {
                            if (which == 0) {
                                // Abrir CrearPlanActivity para editar
                                Intent intent = new Intent(requireContext(), CrearPlanActivity.class);
                                intent.putExtra("PLAN_ID", plan.getId());
                                intent.putExtra("PLAN_NOMBRE", plan.getNombre());
                                intent.putExtra("PLAN_NIVEL", plan.getNivel());
                                intent.putExtra("PLAN_OBJETIVO", plan.getObjetivo());
                                intent.putExtra("PLAN_DESCRIPCION", plan.getDescripcion());
                                startCrearPlan.launch(intent);
                            } else {
                                // Abrir GestionarEjerciciosPlanActivity
                                Intent intent = new Intent(requireContext(), GestionarEjerciciosPlanActivity.class);
                                intent.putExtra("PLAN_ID", plan.getId());
                                intent.putExtra("PLAN_NOMBRE", plan.getNombre());
                                intent.putExtra("PLAN_NIVEL", plan.getNivel());
                                intent.putExtra("PLAN_OBJETIVO", plan.getObjetivo());
                                intent.putExtra("PLAN_DESCRIPCION", plan.getDescripcion());
                                startActivity(intent);
                            }
                        })
                        .show();
            }

            @Override
            public void onEliminar(PlanResponseDto plan) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Eliminar Plan")
                        .setMessage("¿Estás seguro de que quieres eliminar el plan '" + plan.getNombre() + "'?")
                        .setPositiveButton("Eliminar", (dialog, which) -> eliminarPlanMaestro(plan.getId()))
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        adapter.setAdmin(isAdmin, userId);
        rvPlanes.setAdapter(adapter);
        cargarPlanes();

        return view;
    }

    private void suscribirUsuario(Long planId) {
        if (userId == -1) return;

        // ¡LA MAGIA DEL BACKEND!
        // Llamamos al endpoint y el backend automáticamente llena la tabla 'rutinas' del usuario
        RetrofitClient.getApiService().suscribirUsuarioAPlan(planId, userId).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Plan activado con éxito! Revisa tu agenda.", Toast.LENGTH_LONG).show();
                    
                    if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                        ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).crearNotificacionManual(
                                "Plan Suscrito",
                                "Te has suscrito a un nuevo plan de entrenamiento."
                        );
                    }

                    if (subscriptionListener != null) {
                        subscriptionListener.onPlanSubscribed();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al activar el plan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarPlanMaestro(Long planId) {
        RetrofitClient.getApiService().eliminarPlanMaestro(planId).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Plan eliminado", Toast.LENGTH_SHORT).show();
                    cargarPlanes();
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {}
        });
    }

    private void cargarPlanes() {
        RetrofitClient.getApiService().getPlanes().enqueue(new Callback<ApiResponseDto<List<PlanResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<PlanResponseDto>>> call, Response<ApiResponseDto<List<PlanResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateList(response.body().getDatos());
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<PlanResponseDto>>> call, Throwable t) {}
        });
    }
}