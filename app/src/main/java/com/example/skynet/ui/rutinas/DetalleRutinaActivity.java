package com.example.skynet.ui.rutinas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.RutinaRequestDto;
import com.example.skynet.data.remote.dto.RutinaResponseDto;
import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.ui.ejercicios.Ejercicio;
import com.example.skynet.ui.ejercicios.EjercicioAdapter;
import com.example.skynet.ui.ejercicios.ReproductorFragment;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleRutinaActivity extends AppCompatActivity {

    private List<Ejercicio> listaEjercicios;
    private String nombreRutina;
    private String urlVideo;
    private String descripcion;
    private String intensidad;
    private Long tutorialId;
    private Long userId;

    private EjercicioAdapter adapter;
    private android.content.BroadcastReceiver actualizadorReceiver;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rutina_activity_detalle);

        // --- PASO 0: CARGAR DATOS LOCALES ---
        RepositorioRutinas.cargarDatosDesdeLocal(this);

        // Vincular con los IDs del XML
        TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
        TextView tvDuracionDetalle = findViewById(R.id.tvDuracionDetalle);
        TextView tvIntensidadDetalle = findViewById(R.id.tvIntensidadDetalle);
        RecyclerView rvEjercicios = findViewById(R.id.rvEjerciciosDetalle);
        TextView tvLabelEjercicios = findViewById(R.id.tvLabelEjercicios);
        Button btnVolver = findViewById(R.id.btnVolver);
        Button btnCompletar = findViewById(R.id.btnCompletarRutina);
        View btnBack = findViewById(R.id.btnBackDetalle);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Obtener ID de usuario
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1L);

        // Obtener datos del Intent
        nombreRutina = getIntent().getStringExtra("NOMBRE_RUTINA");
        urlVideo = getIntent().getStringExtra("URL_VIDEO");
        descripcion = getIntent().getStringExtra("DESCRIPCION");
        intensidad = getIntent().getStringExtra("INTENSIDAD");
        String subtitulo = getIntent().getStringExtra("SUBTITULO");

        // Recuperar ID de tutorial/rutina
        long tId = getIntent().getLongExtra("TUTORIAL_ID", -1L);
        if (tId == -1L) tId = getIntent().getLongExtra("RUTINA_ID", -1L);
        tutorialId = (tId != -1L) ? tId : null;

        // Manejar duración
        Object durObj = getIntent().getExtras() != null ? getIntent().getExtras().get("DURACION") : null;
        String duracionStr = (durObj != null) ? durObj.toString() : "0";
        if (!duracionStr.contains("min")) duracionStr += " min";

        // Setear Textos del Header
        if (tvTitulo != null) tvTitulo.setText(nombreRutina);
        if (tvDuracionDetalle != null) tvDuracionDetalle.setText(duracionStr);
        if (tvIntensidadDetalle != null) {
            tvIntensidadDetalle.setText(intensidad != null && intensidad.startsWith("Categoría") ? intensidad : "Intensidad: " + (intensidad != null ? intensidad.toUpperCase() : "MEDIA"));
        }

        boolean esEjercicio = getIntent().getBooleanExtra("ES_EJERCICIO_BIBLIOTECA", false);

        // Si es un ejercicio individual, cargamos el ReproductorFragment en el contenedor
        if (esEjercicio && urlVideo != null && !urlVideo.isEmpty()) {
            if (savedInstanceState == null) {
                Integer duracionInt = null;
                try {
                    String cleanDur = duracionStr.replace(" min", "").trim();
                    duracionInt = Integer.parseInt(cleanDur);
                } catch (Exception ignored) {}

                String parentName = getIntent().getStringExtra("PARENT_ROUTINE_NAME");
                ReproductorFragment fragment = ReproductorFragment.newInstance(
                        tutorialId, urlVideo, nombreRutina, descripcion, subtitulo, duracionInt, intensidad, parentName
                );

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }

            if (btnCompletar != null) btnCompletar.setVisibility(View.GONE);
            if (tvLabelEjercicios != null) tvLabelEjercicios.setVisibility(View.GONE);
            if (btnVolver != null) btnVolver.setText("VOLVER A BIBLIOTECA");
        }

        // Configurar Lista de Ejercicios (si es una rutina)
        if (!esEjercicio) {
            setupRecyclerView(rvEjercicios, tvLabelEjercicios);
        } else {
            if (rvEjercicios != null) rvEjercicios.setVisibility(View.GONE);
        }

        actualizarEstadoCompletado();

        actualizadorReceiver = new android.content.BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, android.content.Intent intent) {
                if (nombreRutina != null) {
                    listaEjercicios = RepositorioRutinas.getEjerciciosDeRutina(nombreRutina);
                    if (adapter != null) {
                        adapter.setListaEjercicios(listaEjercicios);
                        adapter.notifyDataSetChanged();
                        actualizarEstadoCompletado();
                    }
                }
            }
        };
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(actualizadorReceiver, new android.content.IntentFilter("com.example.gymcrush.ACTUALIZAR_LISTA_EJERCICIOS"), android.content.Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(actualizadorReceiver, new android.content.IntentFilter("com.example.gymcrush.ACTUALIZAR_LISTA_EJERCICIOS"));
        }

        if (btnVolver != null) btnVolver.setOnClickListener(v -> finish());
        if (btnCompletar != null) {
            btnCompletar.setOnClickListener(v -> {
                if (nombreRutina != null) {
                    RepositorioRutinas.marcarComoCompletada(this, nombreRutina);
                    long id = getIntent().getLongExtra("RUTINA_ID", -1L);
                    boolean isFromAgenda = getIntent().getBooleanExtra("IS_FROM_AGENDA", false);

                    if (id != -1) {
                        if (isFromAgenda) {
                            completarRutinaEnServidor(id);
                        } else {
                            // Si no viene de la agenda, es un entrenamiento maestro. 
                            // Debemos asignarlo a hoy y marcarlo como completado.
                            asignarYCompletarRutina(id);
                        }
                    } else {
                        Toast.makeText(this, "¡Rutina completada localmente!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (actualizadorReceiver != null) {
            unregisterReceiver(actualizadorReceiver);
        }
    }

    private void setupRecyclerView(RecyclerView rv, TextView label) {
        listaEjercicios = RepositorioRutinas.getEjerciciosDeRutina(nombreRutina);
        if (listaEjercicios != null && !listaEjercicios.isEmpty()) {
            if (rv != null) {
                rv.setLayoutManager(new LinearLayoutManager(this));
                adapter = new EjercicioAdapter(listaEjercicios, true, nombreRutina);
                rv.setAdapter(adapter);
            }
            if (label != null) label.setVisibility(View.VISIBLE);
        }
    }

    private void actualizarEstadoCompletado() {
        RepositorioRutinas.RutinaModel rutina = RepositorioRutinas.getRutinaPorNombre(nombreRutina);
        View tvCompletada = findViewById(R.id.tvEstadoCompletadoDetalle);
        View btnCompletar = findViewById(R.id.btnCompletarRutina);

        if (rutina != null && rutina.completada) {
            if (tvCompletada != null) tvCompletada.setVisibility(View.VISIBLE);
            if (btnCompletar != null) btnCompletar.setVisibility(View.GONE);
        } else {
            if (tvCompletada != null) tvCompletada.setVisibility(View.GONE);
            if (btnCompletar != null) btnCompletar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos por si se completó algún ejercicio en el reproductor
        if (nombreRutina != null && adapter != null) {
            listaEjercicios = RepositorioRutinas.getEjerciciosDeRutina(nombreRutina);
            adapter.setListaEjercicios(listaEjercicios);
            adapter.notifyDataSetChanged();
            actualizarEstadoCompletado();
        }
    }

    private void completarRutinaEnServidor(long id) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.completarRutina(id).enqueue(new Callback<ApiResponseDto<RutinaResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<RutinaResponseDto>> call, Response<ApiResponseDto<RutinaResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleRutinaActivity.this, "¡Buen trabajo! Ejercicio guardado", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(DetalleRutinaActivity.this, "Error al completar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<RutinaResponseDto>> call, Throwable t) {
                Toast.makeText(DetalleRutinaActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void asignarYCompletarRutina(long entrenamientoId) {
        if (userId == -1L) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        String hoy = java.time.LocalDate.now().toString();
        RutinaRequestDto request = new RutinaRequestDto(userId, entrenamientoId, hoy, 1);
        request.setCompletado(true);

        RetrofitClient.getApiService().asignarEntrenamiento(request).enqueue(new Callback<ApiResponseDto<RutinaResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<RutinaResponseDto>> call, Response<ApiResponseDto<RutinaResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleRutinaActivity.this, "¡Entrenamiento asignado y completado!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(DetalleRutinaActivity.this, "Error al registrar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<RutinaResponseDto>> call, Throwable t) {
                Toast.makeText(DetalleRutinaActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
