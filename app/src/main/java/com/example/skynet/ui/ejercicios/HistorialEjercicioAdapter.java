package com.example.skynet.ui.ejercicios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.EjercicioHistorialDto;
import java.util.List;
import java.util.Locale;

public class HistorialEjercicioAdapter extends RecyclerView.Adapter<HistorialEjercicioAdapter.ViewHolder> {

    private List<EjercicioHistorialDto> historial;

    public HistorialEjercicioAdapter(List<EjercicioHistorialDto> historial) {
        this.historial = historial;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial_ejercicio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EjercicioHistorialDto dto = historial.get(position);
        holder.tvFecha.setText(dto.getFecha());
        holder.tvStats.setText(String.format(Locale.getDefault(), "Volumen: %.1f kg | 1RM: %.1f kg", dto.getVolumenTotal(), dto.getMejor1RM()));
        
        if (dto.getDuracion() != null && !dto.getDuracion().isEmpty()) {
            holder.tvDuracion.setVisibility(View.VISIBLE);
            holder.tvDuracion.setText("Duración: " + dto.getDuracion());
        } else {
            holder.tvDuracion.setVisibility(View.GONE);
        }

        StringBuilder seriesStr = new StringBuilder();
        if (dto.getSeries() != null) {
            for (EjercicioHistorialDto.SerieDto s : dto.getSeries()) {
                seriesStr.append(s.getReps()).append(" x ").append(s.getKg()).append("kg, ");
            }
            if (seriesStr.length() > 2) seriesStr.setLength(seriesStr.length() - 2);
        }
        holder.tvSeriesDetalle.setText(seriesStr.toString());
    }

    @Override
    public int getItemCount() {
        return historial != null ? historial.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvStats, tvSeriesDetalle, tvDuracion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaHistorial);
            tvStats = itemView.findViewById(R.id.tvStatsHistorial);
            tvSeriesDetalle = itemView.findViewById(R.id.tvSeriesDetalle);
            tvDuracion = itemView.findViewById(R.id.tvDuracionHistorial);
        }
    }
}