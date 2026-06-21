package com.example.skynet.ui.rutinas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.EntrenamientoResponseDto;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExplorarRutinasActivity extends AppCompatActivity {

    private RecyclerView rvProgramas, rvCategorias;
    private ProgramasAdapter programasAdapter;
    private CategoriasExplorarAdapter categoriasAdapter;
    private List<EntrenamientoResponseDto> listaProgramas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorar_rutinas);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvProgramas = findViewById(R.id.rvProgramas);
        rvCategorias = findViewById(R.id.rvCategoriasRutinas);

        setupAdapters();
        cargarDatos();
    }

    private void setupAdapters() {
        programasAdapter = new ProgramasAdapter(listaProgramas, programa -> {
            Intent intent = new Intent(this, DetalleRutinaActivity.class);
            intent.putExtra("RUTINA_ID", programa.getId());
            intent.putExtra("TITULO_RUTINA", programa.getNombre());
            startActivity(intent);
        });
        rvProgramas.setAdapter(programasAdapter);

        List<CategoriasExplorarAdapter.CategoriaExplorar> categorias = new ArrayList<>();
        categorias.add(new CategoriasExplorarAdapter.CategoriaExplorar("En casa", R.drawable.ic_history)); // Using existing icons
        categorias.add(new CategoriasExplorarAdapter.CategoriaExplorar("Viajar", R.drawable.ic_workout));
        categorias.add(new CategoriasExplorarAdapter.CategoriaExplorar("Solo Mancuernas", R.drawable.ic_notifications));
        categorias.add(new CategoriasExplorarAdapter.CategoriaExplorar("Banda", R.drawable.ic_help_outline));
        categorias.add(new CategoriasExplorarAdapter.CategoriaExplorar("Cardio y HIIT", R.drawable.ic_dieta));
        categorias.add(new CategoriasExplorarAdapter.CategoriaExplorar("Gimnasio", R.drawable.ic_reservaclases));

        categoriasAdapter = new CategoriasExplorarAdapter(categorias);
        rvCategorias.setAdapter(categoriasAdapter);
    }

    private void cargarDatos() {
        RetrofitClient.getApiService().obtenerProgramasGlobales(null, null).enqueue(new Callback<ApiResponseDto<List<EntrenamientoResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Response<ApiResponseDto<List<EntrenamientoResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProgramas.clear();
                    listaProgramas.addAll(response.body().getDatos());
                    programasAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Throwable t) {
                Toast.makeText(ExplorarRutinasActivity.this, "Error cargando programas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}