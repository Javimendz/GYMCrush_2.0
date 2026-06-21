package com.example.skynet.ui.rutinas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.ui.ejercicios.Ejercicio;
import com.example.skynet.ui.ejercicios.RepositorioEjercicios;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeleccionEjerciciosActivity extends AppCompatActivity {

    private RecyclerView rvEjercicios;
    private EjercicioSeleccionAdapter adapter;
    private List<Ejercicio> listaEjercicios = new ArrayList<>();
    private Button btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_ejercicios);

        findViewById(R.id.btnCancelarSeleccion).setOnClickListener(v -> finish());
        btnConfirmar = findViewById(R.id.btnConfirmarSeleccion);

        rvEjercicios = findViewById(R.id.rvEjerciciosParaSeleccionar);
        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EjercicioSeleccionAdapter(listaEjercicios);
        rvEjercicios.setAdapter(adapter);

        // Actualizar botón cuando se selecciona algo
        // Nota: Deberías modificar EjercicioSeleccionAdapter para notificar cambios o usar un listener
        // Por ahora, simulamos el refresco
        rvEjercicios.setOnClickListener(v -> updateConfirmButton());

        cargarEjercicios();

        btnConfirmar.setOnClickListener(v -> {
            ArrayList<Ejercicio> seleccionados = (ArrayList<Ejercicio>) listaEjercicios.stream()
                    .filter(Ejercicio::isSeleccionado)
                    .collect(Collectors.toList());
            
            Intent data = new Intent();
            data.putParcelableArrayListExtra("EJERCICIOS_SELECCIONADOS", seleccionados);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    private void cargarEjercicios() {
        RepositorioEjercicios.getTodosAsync(new RepositorioEjercicios.RepositorioCallback() {
            @Override
            public void onLoaded(List<Ejercicio> ejercicios) {
                listaEjercicios.clear();
                listaEjercicios.addAll(ejercicios);
                adapter.notifyDataSetChanged();
            }
            @Override public void onError(String mensaje) {}
        });
    }

    private void updateConfirmButton() {
        long count = listaEjercicios.stream().filter(Ejercicio::isSeleccionado).count();
        if (count > 0) {
            btnConfirmar.setVisibility(View.VISIBLE);
            btnConfirmar.setText("Agrega " + count + " ejercicios");
        } else {
            btnConfirmar.setVisibility(View.GONE);
        }
    }
}