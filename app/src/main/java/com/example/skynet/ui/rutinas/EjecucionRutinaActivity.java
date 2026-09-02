package com.example.skynet.ui.rutinas;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.ui.ejercicios.Ejercicio;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.EjercicioHistorialDto;
import com.example.skynet.data.remote.dto.RutinaResponseDto;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EjecucionRutinaActivity extends AppCompatActivity implements EjerciciosAgregadosAdapter.OnWorkoutUpdateListener {

    private TextView tvVolumenTotal, tvSeriesTotales, tvDuracionRun;
    private RecyclerView rvEjercicios;
    private EjerciciosAgregadosAdapter adapter;
    private List<Ejercicio> listaEjercicios;
    
    private long startTime = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = SystemClock.elapsedRealtime() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            minutes = minutes % 60;
            seconds = seconds % 60;

            String time;
            if (hours > 0) {
                time = String.format(Locale.getDefault(), "%dh %02dm %02ds", hours, minutes, seconds);
            } else if (minutes > 0) {
                time = String.format(Locale.getDefault(), "%dm %02ds", minutes, seconds);
            } else {
                time = String.format(Locale.getDefault(), "%ds", seconds);
            }
            tvDuracionRun.setText(time);
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenamiento_ejecucion);

        // Inicializar vistas
        tvVolumenTotal = findViewById(R.id.tvVolumenRun);
        tvSeriesTotales = findViewById(R.id.tvSeriesRun);
        tvDuracionRun = findViewById(R.id.tvDuracionRun);
        rvEjercicios = findViewById(R.id.rvEjerciciosRun);

        // Obtener datos del intent
        listaEjercicios = getIntent().getParcelableArrayListExtra("LISTA_EJERCICIOS");
        if (listaEjercicios == null) {
            listaEjercicios = new ArrayList<>();
        }

        // Configurar RecyclerView
        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EjerciciosAgregadosAdapter(listaEjercicios);
        adapter.setOnWorkoutUpdateListener(this);
        rvEjercicios.setAdapter(adapter);

        // Configurar botones
        findViewById(R.id.btnTerminar).setOnClickListener(v -> terminarEntrenamiento());
        findViewById(R.id.btnBackRun).setOnClickListener(v -> finish());
        findViewById(R.id.btnAgregarEjercicioRun).setOnClickListener(v -> {
            // Reutilizar AgregarEjercicioActivity
            android.content.Intent intent = new android.content.Intent(this, com.example.skynet.ui.ejercicios.AgregarEjercicioActivity.class);
            startActivityForResult(intent, 1001);
        });

        // Iniciar cronómetro
        WorkoutManager wm = WorkoutManager.getInstance();
        wm.restoreState(this);
        
        if (wm.isActive()) {
            startTime = wm.getStartTime();
            // Si la lista del intent está vacía pero tenemos una activa, recuperamos la activa
            if (listaEjercicios.isEmpty() && !wm.getCurrentExercises().isEmpty()) {
                listaEjercicios.addAll(wm.getCurrentExercises());
                adapter.notifyDataSetChanged();
            }
        } else {
            startTime = SystemClock.elapsedRealtime();
            wm.startWorkout(this, listaEjercicios);
        }
        timerHandler.postDelayed(timerRunnable, 0);

        updateStats();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            ArrayList<Ejercicio> seleccionados = data.getParcelableArrayListExtra("EJERCICIOS_SELECCIONADOS");
            if (seleccionados != null && !seleccionados.isEmpty()) {
                listaEjercicios.addAll(seleccionados);
                adapter.notifyDataSetChanged();
                WorkoutManager.getInstance().setExercises(this, listaEjercicios);
            }
        }
    }

    private void updateStats() {
        double totalVolumen = 0;
        int totalSeries = 0;

        for (Ejercicio ejercicio : listaEjercicios) {
            if (ejercicio.getSeriesList() != null) {
                for (Ejercicio.Serie serie : ejercicio.getSeriesList()) {
                    // Contamos volumen si tiene datos, aunque no esté el checkbox marcado
                    // para dar feedback visual al usuario mientras escribe.
                    if (serie.getKg() > 0 && serie.getReps() > 0) {
                        totalVolumen += (serie.getKg() * serie.getReps());
                    }
                    if (serie.isCompletada()) {
                        totalSeries++;
                    }
                }
            }
        }

        tvVolumenTotal.setText(String.format(Locale.getDefault(), "%.1f kg", totalVolumen));
        tvSeriesTotales.setText(String.valueOf(totalSeries));
    }

    @Override
    public void onWorkoutUpdate() {
        updateStats();
    }

    private void terminarEntrenamiento() {
        timerHandler.removeCallbacks(timerRunnable);
        guardarEstadisticasYFinalizar();
    }

    private void guardarEstadisticasYFinalizar() {
        android.content.SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
        long usuarioId = prefs.getLong("user_id", -1);

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        List<EjercicioHistorialDto> dtosParaGuardar = new ArrayList<>();
        List<Long> ejerciciosIds = new ArrayList<>();

        for (Ejercicio ejercicio : listaEjercicios) {
            List<Ejercicio.Serie> seriesCompletadas = new ArrayList<>();
            double volumenTotal = 0;
            double mejorPeso = 0;
            double mejor1RM = 0;

            if (ejercicio.getSeriesList() != null) {
                for (Ejercicio.Serie s : ejercicio.getSeriesList()) {
                    if (s.isCompletada()) {
                        seriesCompletadas.add(s);
                        double peso = s.getKg();
                        int reps = s.getReps();
                        volumenTotal += (peso * reps);
                        if (peso > mejorPeso) mejorPeso = peso;
                        
                        double rm = peso * (1 + 0.0333 * reps);
                        if (rm > mejor1RM) mejor1RM = rm;
                    }
                }
            }

            if (!seriesCompletadas.isEmpty()) {
                String duracion = tvDuracionRun.getText().toString();
                EjercicioHistorialDto dto = new EjercicioHistorialDto(fechaActual, mejorPeso, mejor1RM, volumenTotal, duracion);
                List<EjercicioHistorialDto.SerieDto> seriesDto = new ArrayList<>();
                for (Ejercicio.Serie s : seriesCompletadas) {
                    seriesDto.add(new EjercicioHistorialDto.SerieDto(s.getKg(), s.getReps(), fechaActual));
                }
                dto.setSeries(seriesDto);
                
                if (ejercicio.getId() == null || ejercicio.getId() <= 0) {
                    android.util.Log.e("EjecucionRutina", "El ejercicio " + ejercicio.getNombre() + " no tiene una ID válida. Saltando guardado.");
                    continue;
                }
                
                dtosParaGuardar.add(dto);
                ejerciciosIds.add(ejercicio.getId());
                
                android.util.Log.d("EjecucionRutina", "Preparado para guardar ejercicio ID: " + ejercicio.getId() + " con " + seriesCompletadas.size() + " series");
            }
        }

        if (dtosParaGuardar.isEmpty()) {
            marcarRutinaComoCompletadaSiEsNecesario();
            Toast.makeText(this, "Entrenamiento finalizado. No se completaron series (debes marcar el checkbox verde) o faltan IDs de ejercicio.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final int totalAGuardar = dtosParaGuardar.size();
        final int[] guardadosCount = {0};

        for (int i = 0; i < totalAGuardar; i++) {
            EjercicioHistorialDto dto = dtosParaGuardar.get(i);
            Long ejercicioId = ejerciciosIds.get(i);
            
            android.util.Log.d("EjecucionRutina", "Enviando POST a /api/v1/ejercicios/" + ejercicioId + "/historial/usuario/" + usuarioId);
            android.util.Log.d("EjecucionRutina", "Payload: " + new com.google.gson.Gson().toJson(dto));
            
            RetrofitClient.getApiService().guardarHistorialEjercicio(ejercicioId, usuarioId, dto)
                    .enqueue(new Callback<EjercicioHistorialDto>() {
                        @Override
                        public void onResponse(Call<EjercicioHistorialDto> call, Response<EjercicioHistorialDto> response) {
                            if (response.isSuccessful()) {
                                android.util.Log.d("EjecucionRutina", "Historial guardado exitosamente en servidor para ID: " + ejercicioId);
                            } else {
                                String errorMsg = "Desconocido";
                                try {
                                    errorMsg = response.errorBody().string();
                                } catch (Exception ignored) {}
                                android.util.Log.e("EjecucionRutina", "Error en servidor (" + response.code() + ") para ID: " + ejercicioId + ". Detalle: " + errorMsg);
                            }
                            checkFinalizacion(guardadosCount, totalAGuardar);
                        }

                        @Override
                        public void onFailure(Call<EjercicioHistorialDto> call, Throwable t) {
                            android.util.Log.e("EjecucionRutina", "Fallo de red al guardar historial para ID: " + ejercicioId, t);
                            checkFinalizacion(guardadosCount, totalAGuardar);
                        }
                    });
        }
    }

    private void checkFinalizacion(int[] count, int total) {
        synchronized (count) {
            count[0]++;
            if (count[0] >= total) {
                marcarRutinaComoCompletadaSiEsNecesario();
            }
        }
    }

    private void marcarRutinaComoCompletadaSiEsNecesario() {
        long rutinaId = getIntent().getLongExtra("RUTINA_ID", -1L);
        boolean isFromAgenda = getIntent().getBooleanExtra("IS_FROM_AGENDA", false);

        if (rutinaId != -1 && isFromAgenda) {
            RetrofitClient.getApiService().completarRutina(rutinaId).enqueue(new Callback<ApiResponseDto<RutinaResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<RutinaResponseDto>> call, Response<ApiResponseDto<RutinaResponseDto>> response) {
                    finalizarConExito();
                }

                @Override
                public void onFailure(Call<ApiResponseDto<RutinaResponseDto>> call, Throwable t) {
                    finalizarConExito();
                }
            });
        } else {
            finalizarConExito();
        }
    }

    private void finalizarConExito() {
        WorkoutManager.getInstance().stopWorkout(this);
        Toast.makeText(this, "Entrenamiento guardado correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}