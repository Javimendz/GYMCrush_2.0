package com.example.skynet.ui.ejercicios;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skynet.R;
import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.EjercicioHistorialDto;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResumenEjercicioFragment extends Fragment {

    private LineChart chart;
    private TextView tv1RM, tvMejorVolumen;
    private Ejercicio ejercicio;

    public static ResumenEjercicioFragment newInstance(Ejercicio ejercicio) {
        ResumenEjercicioFragment fragment = new ResumenEjercicioFragment();
        Bundle args = new Bundle();
        args.putParcelable("ejercicio", ejercicio);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resumen_ejercicio, container, false);

        if (getArguments() != null) {
            ejercicio = getArguments().getParcelable("ejercicio");
        }

        chart = view.findViewById(R.id.chartVolumen);
        tv1RM = view.findViewById(R.id.tv1RM);
        tvMejorVolumen = view.findViewById(R.id.tvMejorVolumen);

        setupChart(chart);
        cargarDatosHistorial();

        return view;
    }

    private void cargarDatosHistorial() {
        if (ejercicio == null) {
            android.util.Log.e("ResumenEjercicio", "Ejercicio es null");
            return;
        }
        
        if (ejercicio.getId() == null) {
            android.util.Log.e("ResumenEjercicio", "El ejercicio '" + ejercicio.getNombre() + "' no tiene ID. No se pueden cargar estadísticas.");
            tv1RM.setText("N/A");
            tvMejorVolumen.setText("N/A");
            return;
        }

        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", android.content.Context.MODE_PRIVATE);
        Long usuarioId = prefs.getLong("user_id", -1L);

        if (usuarioId == -1L) {
            Toast.makeText(getContext(), "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getHistorialEjercicio(ejercicio.getId(), usuarioId).enqueue(new Callback<List<EjercicioHistorialDto>>() {
            @Override
            public void onResponse(Call<List<EjercicioHistorialDto>> call, Response<List<EjercicioHistorialDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EjercicioHistorialDto> historial = response.body();
                    if (historial != null && !historial.isEmpty()) {
                        actualizarUI(historial);
                    } else {
                        android.util.Log.d("ResumenEjercicio", "Historial vacío para el ejercicio " + ejercicio.getId());
                    }
                } else {
                    android.util.Log.e("ResumenEjercicio", "Error en API: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<EjercicioHistorialDto>> call, Throwable t) {
                android.util.Log.e("ResumenEjercicio", "Error de red", t);
                if (isAdded()) {
                    Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarUI(List<EjercicioHistorialDto> historial) {
        if (historial == null || historial.isEmpty()) return;

        double max1RM = 0;
        double maxVolumen = 0;
        List<Entry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        for (int i = 0; i < historial.size(); i++) {
            EjercicioHistorialDto data = historial.get(i);
            if (data.getMejor1RM() > max1RM) max1RM = data.getMejor1RM();
            if (data.getVolumenTotal() > maxVolumen) maxVolumen = data.getVolumenTotal();

            entries.add(new Entry(i, (float) data.getVolumenTotal()));
            dates.add(data.getFecha());
        }

        tv1RM.setText(String.format(Locale.getDefault(), "%.1f kg", max1RM));
        tvMejorVolumen.setText(String.format(Locale.getDefault(), "%.1f kg", maxVolumen));

        LineDataSet dataSet = new LineDataSet(entries, "Volumen Total");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setFillAlpha(50);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.invalidate();
    }

    private void setupChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisRight().setEnabled(false);
    }
}