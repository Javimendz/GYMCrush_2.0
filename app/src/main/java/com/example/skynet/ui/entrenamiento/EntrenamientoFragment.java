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
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class EntrenamientoFragment extends Fragment {

    private MaterialCardView btnEmpezarVacio, btnNuevaRutina, btnExplorarRutinas, btnComoEmpezar;
    private androidx.recyclerview.widget.RecyclerView rvMisRutinas;
    private android.widget.TextView tvMisRutinasCount;
    private android.widget.LinearLayout layoutMisRutinasHeader;
    private RutinaGuardadaAdapter adapter;

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

        rvMisRutinas.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));

        // Configurar listeners
        setupListeners();
        cargarRutinas();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarRutinas();
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
    }
}
