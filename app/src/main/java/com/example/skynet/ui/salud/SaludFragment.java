package com.example.skynet.ui.salud;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.skynet.R;
import com.example.skynet.data.remote.dto.SaludRequestDto;
import com.example.skynet.data.remote.dto.SaludResponseDto;
import com.example.skynet.ui.main.DesarrolloActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SaludFragment extends Fragment {

    private TextView tvImcValue, tvImcStatus, tvPesoActual, tvAltura;
    private CircularProgressIndicator imcProgress;
    private ProgressGraphView graphView;
    private SaludViewModel viewModel;
    private SharedPreferences userPrefs;
    private Long userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salud, container, false);

        tvImcValue = view.findViewById(R.id.tvImcValue);
        tvImcStatus = view.findViewById(R.id.tvImcStatus);
        tvPesoActual = view.findViewById(R.id.tvPesoActual);
        tvAltura = view.findViewById(R.id.tvAltura);
        imcProgress = view.findViewById(R.id.imcProgress);
        graphView = view.findViewById(R.id.graphView);
        Button btnEditar = view.findViewById(R.id.btnEditarSalud);
        MaterialCardView cardHistorial = view.findViewById(R.id.cardHistorial);

        view.findViewById(R.id.btnBackSalud).setOnClickListener(v -> {
            if (getActivity() instanceof DesarrolloActivity) {
                ((DesarrolloActivity) getActivity()).mostrarHome();
            }
        });

        userPrefs = requireActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = userPrefs.getLong("user_id", -1L);

        viewModel = new ViewModelProvider(requireActivity()).get(SaludViewModel.class);

        setupObservers();

        if (userId != -1L) {
            viewModel.cargarDatos(userId);
        } else {
            Toast.makeText(getContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
        }

        btnEditar.setOnClickListener(v -> mostrarDialogoEdicion());

        cardHistorial.setOnClickListener(v -> {
            HistorialSaludFragment historialFragment = new HistorialSaludFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, historialFragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        });

        return view;
    }

    private void setupObservers() {
        viewModel.getSaludActual().observe(getViewLifecycleOwner(), salud -> {
            if (salud != null) {
                actualizarUI(salud);
            }
        });

        viewModel.getHistorialSalud().observe(getViewLifecycleOwner(), historial -> {
            if (historial != null && !historial.isEmpty()) {
                actualizarGrafico(historial);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                if (!error.contains("404") && !error.contains("no tiene registros") && !error.contains("No se encontraron datos de salud")) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarUI(SaludResponseDto salud) {
        double peso = salud.getPeso() != null ? salud.getPeso() : 0.0;
        double estatura = salud.getEstatura() != null ? salud.getEstatura() : 0.0;
        double imc = salud.getImc() != null ? salud.getImc() : 0.0;

        tvPesoActual.setText(String.format(Locale.getDefault(), "%.1f kg", peso));
        double estaturaMostrar = estatura < 3 ? estatura * 100 : estatura;
        tvAltura.setText(String.format(Locale.getDefault(), "%.0f cm", estaturaMostrar));
        tvImcValue.setText(String.format(Locale.getDefault(), "%.1f", imc));

        String estado;
        int progreso;
        if (imc < 18.5) {
            estado = "Bajo peso";
            progreso = 30;
        } else if (imc < 25) {
            estado = "Normal";
            progreso = 65;
        } else if (imc < 30) {
            estado = "Sobrepeso";
            progreso = 85;
        } else {
            estado = "Obesidad";
            progreso = 100;
        }

        tvImcStatus.setText(estado);
        imcProgress.setProgress(progreso);
    }

    private void actualizarGrafico(List<SaludResponseDto> historial) {
        List<Float> puntos = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        
        int numPuntos = Math.min(historial.size(), 7);
        for (int i = numPuntos - 1; i >= 0; i--) {
            SaludResponseDto s = historial.get(i);
            puntos.add(s.getPeso().floatValue());
            
            String fecha = s.getFechaMedicion();
            if (fecha != null && fecha.length() >= 10) {
                String delimiter = fecha.contains("T") ? "T" : " ";
                String[] parts = fecha.split(delimiter);
                String[] ymd = parts[0].split("-");
                if (ymd.length == 3) {
                    etiquetas.add(ymd[2] + "/" + ymd[1]);
                } else {
                    etiquetas.add("");
                }
            } else {
                etiquetas.add("");
            }
        }
        
        if (!puntos.isEmpty()) {
            graphView.setData(puntos, etiquetas);
        }
    }

    private void mostrarDialogoEdicion() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_salud, null);
        EditText etPeso = dialogView.findViewById(R.id.etPeso);
        EditText etAltura = dialogView.findViewById(R.id.etAltura);
        EditText etActividad = dialogView.findViewById(R.id.etActividad);
        EditText etComentario = dialogView.findViewById(R.id.etComentario);
        Button btnGuardar = dialogView.findViewById(R.id.btnGuardar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        SaludResponseDto actual = viewModel.getSaludActual().getValue();
        if (actual != null) {
            etPeso.setText(String.valueOf(actual.getPeso()));
            double h = actual.getEstatura();
            etAltura.setText(String.valueOf(h < 3 ? (int)(h*100) : (int)h));
            etActividad.setText(actual.getNivelActividad());
            etComentario.setText(actual.getComentario());
        }

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialogView);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        btnGuardar.setOnClickListener(v -> {
            String sPeso = etPeso.getText().toString();
            String sAltura = etAltura.getText().toString();
            String sActividad = etActividad.getText().toString();
            String sComentario = etComentario.getText().toString();

            if (!sPeso.isEmpty() && !sAltura.isEmpty()) {
                try {
                    double nuevoPeso = Double.parseDouble(sPeso);
                    double nuevaAlturaCm = Double.parseDouble(sAltura);
                    
                    SaludRequestDto request = new SaludRequestDto();
                    request.setPeso(nuevoPeso);
                    request.setEstatura(nuevaAlturaCm > 3 ? nuevaAlturaCm / 100.0 : nuevaAlturaCm);
                    request.setNivelActividad(sActividad.isEmpty() ? "Moderada" : sActividad);
                    request.setComentario(sComentario);

                    viewModel.registrarSalud(userId, request);
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Guardando medición...", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Formato inválido", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Peso y Altura son obligatorios", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}