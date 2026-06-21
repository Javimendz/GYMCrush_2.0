package com.example.skynet.ui.salud;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.remote.dto.SaludResponseDto;

import java.util.List;
import java.util.Locale;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<SaludResponseDto> historialList;

    public HistorialAdapter(List<SaludResponseDto> historialList) {
        this.historialList = historialList;
    }

    public void setHistorial(List<SaludResponseDto> newList) {
        this.historialList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial_salud, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaludResponseDto registro = historialList.get(position);
        
        String fecha = registro.getFechaMedicion();
        if (fecha != null && (fecha.contains("T") || fecha.contains(" "))) {
            String delimiter = fecha.contains("T") ? "T" : " ";
            fecha = fecha.split(delimiter)[0]; // Quedarnos solo con la parte de la fecha YYYY-MM-DD
            String[] parts = fecha.split("-");
            if (parts.length == 3) {
                fecha = parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        }
        
        holder.tvFecha.setText(fecha != null ? fecha : "N/A");
        holder.tvPeso.setText(String.format(Locale.getDefault(), "%.1f kg", registro.getPeso()));
        
        String actividad = registro.getNivelActividad() != null ? registro.getNivelActividad() : "No especificado";
        holder.tvActividad.setText("Actividad: " + actividad);
        
        String comentario = registro.getComentario() != null && !registro.getComentario().isEmpty() 
                ? registro.getComentario() : "Sin comentarios";
        holder.tvComentario.setText("Comentario: " + comentario);
    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvPeso, tvActividad, tvComentario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaItem);
            tvPeso = itemView.findViewById(R.id.tvPesoItem);
            tvActividad = itemView.findViewById(R.id.tvActividadItem);
            tvComentario = itemView.findViewById(R.id.tvComentarioItem);
        }
    }
}