package com.example.skynet.ui.entrenamiento;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skynet.R;
import com.example.skynet.ui.rutinas.CrearRutinaActivity;
import com.example.skynet.ui.rutinas.ExplorarRutinasActivity;
import com.example.skynet.ui.rutinas.NuevaRutinaActivity;
import com.example.skynet.ui.rutinas.RepositorioRutinas;
import com.example.skynet.ui.rutinas.RutinaGuardadaAdapter;
import com.example.skynet.ui.rutinas.WorkoutManager;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Locale;

public class EntrenamientoFragment extends Fragment {

    private MaterialCardView btnEmpezarVacio, btnNuevaRutina, btnExplorarRutinas, btnComoEmpezar;
    private androidx.recyclerview.widget.RecyclerView rvMisRutinas;
    private android.widget.TextView tvMisRutinasCount;
    private android.widget.LinearLayout layoutMisRutinasHeader;
    private RutinaGuardadaAdapter adapter;

    // Floating Workout Card views
    private MaterialCardView cardActiveWorkout;
    private android.widget.TextView tvActiveWorkoutTimer, tvActiveWorkoutLastExercise;
    private android.view.View btnStopActiveWorkout;
    private android.os.Handler timerHandler = new android.os.Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (WorkoutManager.getInstance().isActive()) {
                updateFloatingCardTimer();
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrenamiento, container, false);

        // Inicializar vistas
        btnEmpezarVacio = view.findViewById(R.id.btnEmpezarVacio);
        btnNuevaRutina = view.findViewById(R.id.btnNuevaRutina);
        btnExplorarRutinas = view.findViewById(R.id.btnExplorarRutinas);
        btnComoEmpezar = view.findViewById(R.id.btnComoEmpezar);
        
        rvMisRutinas = view.findViewById(R.id.rvMisRutinas);
        tvMisRutinasCount = view.findViewById(R.id.tvMisRutinasCount);
        layoutMisRutinasHeader = view.findViewById(R.id.layoutMisRutinasHeader);

        // Vistas de la tarjeta flotante
        cardActiveWorkout = view.findViewById(R.id.cardActiveWorkout);
        tvActiveWorkoutTimer = view.findViewById(R.id.tvActiveWorkoutTimer);
        tvActiveWorkoutLastExercise = view.findViewById(R.id.tvActiveWorkoutLastExercise);
        btnStopActiveWorkout = view.findViewById(R.id.btnStopActiveWorkout);

        rvMisRutinas.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));

        // Configurar listeners
        setupListeners();
        cargarRutinas();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WorkoutManager.getInstance().restoreState(requireContext());
        cargarRutinas();
        checkActiveWorkout();
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void checkActiveWorkout() {
        if (WorkoutManager.getInstance().isActive()) {
            cardActiveWorkout.setVisibility(View.VISIBLE);
            tvActiveWorkoutLastExercise.setText(WorkoutManager.getInstance().getLastExerciseName());
            timerHandler.post(timerRunnable);
        } else {
            cardActiveWorkout.setVisibility(View.GONE);
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void updateFloatingCardTimer() {
        long millis = android.os.SystemClock.elapsedRealtime() - WorkoutManager.getInstance().getStartTime();
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        String time = String.format(Locale.getDefault(), "Entrenamiento %dmin %02ds", minutes, seconds);
        tvActiveWorkoutTimer.setText(time);
        tvActiveWorkoutLastExercise.setText(WorkoutManager.getInstance().getLastExerciseName());
    }

    private void cargarRutinas() {
        RepositorioRutinas.cargarDatosDesdeLocal(requireContext());
        List<RepositorioRutinas.RutinaModel> rutinas = RepositorioRutinas.getRutinas();
        
        if (rutinas != null && !rutinas.isEmpty()) {
            layoutMisRutinasHeader.setVisibility(View.VISIBLE);
            tvMisRutinasCount.setText("Mis rutinas (" + rutinas.size() + ")");
            
            adapter = new RutinaGuardadaAdapter(rutinas, new RutinaGuardadaAdapter.OnRutinaGuardadaClickListener() {
                @Override
                public void onDelete(RepositorioRutinas.RutinaModel rutina) {
                    mostrarDialogoConfirmacionEliminar(rutina);
                }
            });
            rvMisRutinas.setAdapter(adapter);
        } else {
            layoutMisRutinasHeader.setVisibility(View.GONE);
            rvMisRutinas.setAdapter(null);
        }
    }

    private void mostrarDialogoConfirmacionEliminar(RepositorioRutinas.RutinaModel rutina) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar rutina")
                .setMessage("¿Estás seguro de que quieres eliminar la rutina \"" + rutina.nombre + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    RepositorioRutinas.eliminarRutina(requireContext(), rutina.nombre);
                    cargarRutinas();
                    Toast.makeText(getContext(), "Rutina eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupListeners() {
        btnEmpezarVacio.setOnClickListener(v -> {
            // Navegar a iniciar entrenamiento vacío
            Toast.makeText(getContext(), "Iniciando entrenamiento vacío...", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(getActivity(), EntrenamientoVacioActivity.class);
            // startActivity(intent);
        });

        btnNuevaRutina.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevaRutinaActivity.class);
            startActivity(intent);
        });

        btnExplorarRutinas.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ExplorarRutinasActivity.class);
            startActivity(intent);
        });

        btnComoEmpezar.setOnClickListener(v -> {
            // Navegar a tutorial/guía
            Toast.makeText(getContext(), "Mostrando guía de inicio...", Toast.LENGTH_SHORT).show();
        });

        cardActiveWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.skynet.ui.rutinas.EjecucionRutinaActivity.class);
            // Pasar los datos actuales
            intent.putParcelableArrayListExtra("LISTA_EJERCICIOS", new java.util.ArrayList<>(WorkoutManager.getInstance().getCurrentExercises()));
            startActivity(intent);
        });

        btnStopActiveWorkout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Detener entrenamiento")
                    .setMessage("¿Estás seguro de que quieres descartar el entrenamiento actual?")
                    .setPositiveButton("Sí, descartar", (dialog, which) -> {
                        WorkoutManager.getInstance().stopWorkout(requireContext());
                        checkActiveWorkout();
                        Toast.makeText(getContext(), "Entrenamiento descartado", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
