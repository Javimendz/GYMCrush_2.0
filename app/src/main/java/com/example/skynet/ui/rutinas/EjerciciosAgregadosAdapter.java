package com.example.skynet.ui.rutinas;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.ui.ejercicios.DetalleEjercicioActivity;
import com.example.skynet.ui.ejercicios.Ejercicio;
import com.bumptech.glide.Glide;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;

public class EjerciciosAgregadosAdapter extends RecyclerView.Adapter<EjerciciosAgregadosAdapter.ViewHolder> {

    private List<Ejercicio> ejercicios;
    private OnWorkoutUpdateListener updateListener;

    public interface OnWorkoutUpdateListener {
        void onWorkoutUpdate();
    }

    public EjerciciosAgregadosAdapter(List<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }

    public void setOnWorkoutUpdateListener(OnWorkoutUpdateListener listener) {
        this.updateListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio_agregado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ejercicio ejercicio = ejercicios.get(position);
        holder.tvNombre.setText(ejercicio.getNombre());
        
        if (ejercicio.getUrlVideo() != null && !ejercicio.getUrlVideo().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(ejercicio.getUrlVideo())
                .placeholder(R.drawable.ic_workout)
                .error(R.drawable.ic_workout)
                .centerCrop()
                .into(holder.ivEjercicio);
        } else {
            Glide.with(holder.itemView.getContext())
                .load("file:///android_asset/login.jpg")
                .centerCrop()
                .into(holder.ivEjercicio);
        }

        holder.ivEjercicio.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetalleEjercicioActivity.class);
            intent.putExtra("ejercicio", ejercicio);
            holder.itemView.getContext().startActivity(intent);
        });

        holder.layoutSeries.removeAllViews();
        // Initialize with one series if empty for the UI
        if (ejercicio.getSeriesList() == null) {
            ejercicio.setSeriesList(new ArrayList<>());
            ejercicio.getSeriesList().add(new Ejercicio.Serie(1, 0, 0));
        }

        for (Ejercicio.Serie serie : ejercicio.getSeriesList()) {
            addSerieRow(holder, serie);
        }

        holder.btnAgregarSerie.setOnClickListener(v -> {
            int nextNum = ejercicio.getSeriesList().size() + 1;
            Ejercicio.Serie nueva = new Ejercicio.Serie(nextNum, 0, 0);
            ejercicio.getSeriesList().add(nueva);
            addSerieRow(holder, nueva);
        });
    }

    private void addSerieRow(ViewHolder holder, Ejercicio.Serie serie) {
        View row = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_serie_row, holder.layoutSeries, false);
        TextView tvNum = row.findViewById(R.id.tvSerieNumero);
        EditText etPeso = row.findViewById(R.id.etPeso);
        EditText etReps = row.findViewById(R.id.etReps);
        CheckBox cbCompletada = row.findViewById(R.id.cbSerieCompletada);

        tvNum.setText(String.valueOf(serie.getNumero()));
        if (serie.getKg() > 0) etPeso.setText(String.valueOf(serie.getKg()));
        if (serie.getReps() > 0) etReps.setText(String.valueOf(serie.getReps()));
        cbCompletada.setChecked(serie.isCompletada());

        etPeso.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    serie.setKg(Double.parseDouble(s.toString()));
                } catch (Exception e) {
                    serie.setKg(0);
                }
                if (updateListener != null) updateListener.onWorkoutUpdate();
            }
        });

        etReps.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    serie.setReps(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    serie.setReps(0);
                }
                if (updateListener != null) updateListener.onWorkoutUpdate();
            }
        });

        cbCompletada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            serie.setCompletada(isChecked);
            if (updateListener != null) updateListener.onWorkoutUpdate();
        });

        holder.layoutSeries.addView(row);
    }

    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView ivEjercicio;
        LinearLayout layoutSeries;
        Button btnAgregarSerie;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicioAgregado);
            ivEjercicio = itemView.findViewById(R.id.ivEjercicioAgregado);
            layoutSeries = itemView.findViewById(R.id.layoutSeries);
            btnAgregarSerie = itemView.findViewById(R.id.btnAgregarSerie);
        }
    }
}