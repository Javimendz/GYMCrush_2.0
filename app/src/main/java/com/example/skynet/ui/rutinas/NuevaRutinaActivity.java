package com.example.skynet.ui.rutinas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.ui.ejercicios.Ejercicio;
import java.util.ArrayList;
import java.util.List;

public class NuevaRutinaActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECCION = 1001;
    private RecyclerView rvEjercicios;
    private EjerciciosAgregadosAdapter adapter;
    private List<Ejercicio> listaEjercicios = new ArrayList<>();
    private LinearLayout layoutEmpty;
    private EditText etTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenamiento_ejecucion);

        etTitulo = findViewById(R.id.etNombreRutinaEjecucion);
        layoutEmpty = findViewById(R.id.layoutEmptyState);
        rvEjercicios = findViewById(R.id.rvEjerciciosAgregados);
        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EjerciciosAgregadosAdapter(listaEjercicios);
        rvEjercicios.setAdapter(adapter);

        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());
        findViewById(R.id.btnAgregarEjercicioAccion).setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.skynet.ui.ejercicios.AgregarEjercicioActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SELECCION);
        });

        findViewById(R.id.btnGuardarFinal).setOnClickListener(v -> guardarRutina());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECCION && resultCode == RESULT_OK && data != null) {
            ArrayList<Ejercicio> seleccionados = data.getParcelableArrayListExtra("EJERCICIOS_SELECCIONADOS");
            if (seleccionados != null && !seleccionados.isEmpty()) {
                listaEjercicios.addAll(seleccionados);
                adapter.notifyDataSetChanged();
                layoutEmpty.setVisibility(View.GONE);
                rvEjercicios.setVisibility(View.VISIBLE);
            }
        }
    }

    private void guardarRutina() {
        String titulo = etTitulo.getText().toString().trim();
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un título", Toast.LENGTH_SHORT).show();
            return;
        }
        if (listaEjercicios.isEmpty()) {
            Toast.makeText(this, "Agrega al menos un ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Crear el EntrenamientoRequestDto
        com.example.skynet.data.remote.dto.EntrenamientoRequestDto entrenamientoReq = 
            new com.example.skynet.data.remote.dto.EntrenamientoRequestDto();
        entrenamientoReq.setNombre(titulo);
        entrenamientoReq.setDescripcion("Rutina personalizada");
        entrenamientoReq.setDuracion(45); // Estimado
        entrenamientoReq.setIntensidad("MEDIA");
        entrenamientoReq.setCategoria("PERSONAL");
        entrenamientoReq.setEsGlobal(false);
        
        List<Long> tutorialesIds = new ArrayList<>();
        for (Ejercicio e : listaEjercicios) {
            if (e.getId() != null) tutorialesIds.add(e.getId());
        }
        entrenamientoReq.setTutorialesIds(tutorialesIds);

        // 2. Llamada al API
        com.example.skynet.data.remote.RetrofitClient.getApiService().crearEntrenamiento(entrenamientoReq)
            .enqueue(new retrofit2.Callback<com.example.skynet.data.remote.dto.ApiResponseDto<com.example.skynet.data.remote.dto.EntrenamientoResponseDto>>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.skynet.data.remote.dto.ApiResponseDto<com.example.skynet.data.remote.dto.EntrenamientoResponseDto>> call, 
                                       retrofit2.Response<com.example.skynet.data.remote.dto.ApiResponseDto<com.example.skynet.data.remote.dto.EntrenamientoResponseDto>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Long entrenamientoId = response.body().getDatos().getId();
                        
                        // GUARDAR LOCALMENTE para que aparezca en EntrenamientoFragment (Imagen 3)
                        RepositorioRutinas.guardarRutinaCompletaConPortada(
                                NuevaRutinaActivity.this,
                                entrenamientoId,
                                titulo,
                                listaEjercicios,
                                null, // Portada
                                "45 min",
                                "Rutina personalizada"
                        );

                        Toast.makeText(NuevaRutinaActivity.this, "Rutina guardada", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(NuevaRutinaActivity.this, "Error al guardar en el servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.example.skynet.data.remote.dto.ApiResponseDto<com.example.skynet.data.remote.dto.EntrenamientoResponseDto>> call, Throwable t) {
                    Toast.makeText(NuevaRutinaActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}