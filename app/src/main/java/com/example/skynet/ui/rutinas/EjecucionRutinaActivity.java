package com.example.skynet.ui.rutinas;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.ui.ejercicios.Ejercicio;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EjecucionRutinaActivity extends AppCompatActivity implements EjerciciosAgregadosAdapter.OnWorkoutUpdateListener {

    private Chronometer chronometer;
    private TextView tvVolumenTotal, tvSeriesTotales;
    private RecyclerView rvEjercicios;
    private EjerciciosAgregadosAdapter adapter;
    private List<Ejercicio> listaEjercicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenamiento_ejecucion);

        // Inicializar vistas
        chronometer = new Chronometer(this); // Just for timing, UI uses tvDuracionRun
        tvVolumenTotal = findViewById(R.id.tvVolumenRun);
        tvSeriesTotales = findViewById(R.id.tvSeriesRun);
        TextView tvDuracionRun = findViewById(R.id.tvDuracionRun);
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
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(c -> {
            long elapsedMillis = SystemClock.elapsedRealtime() - c.getBase();
            int hours = (int) (elapsedMillis / 3600000);
            int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
            int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
            
            String time;
            if (hours > 0) {
                time = String.format(Locale.getDefault(), "%dh %02dm %02ds", hours, minutes, seconds);
            } else if (minutes > 0) {
                time = String.format(Locale.getDefault(), "%dm %02ds", minutes, seconds);
            } else {
                time = String.format(Locale.getDefault(), "%ds", seconds);
            }
            tvDuracionRun.setText(time);
        });
        chronometer.start();

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
            }
        }
    }

    private void updateStats() {
        double totalVolumen = 0;
        int totalSeries = 0;

        for (Ejercicio ejercicio : listaEjercicios) {
            if (ejercicio.getSeriesList() != null) {
                for (Ejercicio.Serie serie : ejercicio.getSeriesList()) {
                    if (serie.isCompletada()) {
                        totalVolumen += (serie.getKg() * serie.getReps());
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
        chronometer.stop();
        // Aquí podrías guardar el resumen del entrenamiento
        Toast.makeText(this, "Entrenamiento finalizado", Toast.LENGTH_SHORT).show();
        finish();
    }
}