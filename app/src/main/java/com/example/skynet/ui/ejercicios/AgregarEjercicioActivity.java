package com.example.skynet.ui.ejercicios;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import com.example.skynet.R;
import java.util.ArrayList;
import java.util.Objects;

public class AgregarEjercicioActivity extends AppCompatActivity {

    private AgregarEjercicioViewModel viewModel;
    private EntrenamientoAdapter adapter;
    private ProgressBar progressBar;
    private com.google.android.material.button.MaterialButton btnAgregaEjercicios;
    private java.util.ArrayList<Ejercicio> seleccionados = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ejercicio);

        viewModel = new ViewModelProvider(this).get(AgregarEjercicioViewModel.class);

        progressBar = findViewById(R.id.progressBar);
        RecyclerView rvEjercicios = findViewById(R.id.rvEjercicios);
        EditText etBuscar = findViewById(R.id.etBuscar);
        btnAgregaEjercicios = findViewById(R.id.btnAgregaEjercicios);

        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrenamientoAdapter(new ArrayList<>(), dto -> {
            viewModel.toggleExerciseSelection(dto.getId());
            
            // Manage local 'seleccionados' list for the result Intent
            Ejercicio ejercicio = new Ejercicio(
                    dto.getId(),
                    dto.getNombre(),
                    "Wger",
                    dto.getDescripcion(),
                    "10 min",
                    dto.getImagenUrl(),
                    R.drawable.ic_workout,
                    "Media"
            );
            
            boolean exists = false;
            for (int i = 0; i < seleccionados.size(); i++) {
                if (Objects.equals(seleccionados.get(i).getId(), ejercicio.getId())) {
                    seleccionados.remove(i);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                seleccionados.add(ejercicio);
            }
            actualizarBotonSeleccion();
        });
        rvEjercicios.setAdapter(adapter);

        rvEjercicios.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Solo actuar si el usuario está bajando
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        // Disparar carga cuando queden 5 elementos para llegar al final
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                            viewModel.loadNextPage();
                        }
                    }
                }
            }
        });

        btnAgregaEjercicios.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra("EJERCICIOS_SELECCIONADOS", seleccionados);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());
        findViewById(R.id.btnCrear).setOnClickListener(v -> {
            // Navegar a creación manual si fuera necesario
        });

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.onSearchQueryChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        viewModel.getUiState().observe(this, state -> {
            if (state instanceof AgregarEjercicioUiState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (state instanceof AgregarEjercicioUiState.Success) {
                progressBar.setVisibility(View.GONE);
                AgregarEjercicioUiState.Success success = (AgregarEjercicioUiState.Success) state;
                adapter.updateList(success.getEjercicios(), success.getSelectedIds());
            } else if (state instanceof AgregarEjercicioUiState.Error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void actualizarBotonSeleccion() {
        if (seleccionados.isEmpty()) {
            btnAgregaEjercicios.setVisibility(View.GONE);
        } else {
            btnAgregaEjercicios.setVisibility(View.VISIBLE);
            btnAgregaEjercicios.setText("Agrega " + seleccionados.size() + " ejercicio" + (seleccionados.size() > 1 ? "s" : ""));
        }
    }
}
