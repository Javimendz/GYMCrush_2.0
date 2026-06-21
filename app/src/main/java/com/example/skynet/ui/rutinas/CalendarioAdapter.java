package com.example.skynet.ui.rutinas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class CalendarioAdapter extends RecyclerView.Adapter<CalendarioAdapter.ViewHolder> {

    private List<LocalDate> dias;
    private LocalDate fechaSeleccionada;
    private OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(LocalDate date);
    }

    public CalendarioAdapter(List<LocalDate> dias, LocalDate seleccionada, OnDateClickListener listener) {
        this.dias = dias;
        this.fechaSeleccionada = seleccionada;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendario_dia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalDate fecha = dias.get(position);
        holder.tvNombre.setText(fecha.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "ES")).toUpperCase());
        holder.tvNumero.setText(String.valueOf(fecha.getDayOfMonth()));

        boolean esHoy = fecha.equals(LocalDate.now());
        boolean esSeleccionado = fecha.equals(fechaSeleccionada);

        if (esSeleccionado) {
            holder.tvNumero.setBackgroundResource(R.drawable.bg_calendar_selected);
            holder.tvNumero.setTextColor(Color.WHITE);
            holder.tvNombre.setTextColor(Color.parseColor("#CD0277"));
        } else {
            holder.tvNumero.setBackgroundColor(Color.TRANSPARENT);
            holder.tvNumero.setTextColor(esHoy ? Color.parseColor("#CD0277") : Color.WHITE);
            holder.tvNombre.setTextColor(Color.parseColor("#99FFFFFF"));
        }

        holder.itemView.setOnClickListener(v -> {
            fechaSeleccionada = fecha;
            notifyDataSetChanged();
            if (listener != null) listener.onDateClick(fecha);
        });
    }

    @Override
    public int getItemCount() {
        return dias.size();
    }

    public void setFechaSeleccionada(LocalDate date) {
        this.fechaSeleccionada = date;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvNumero;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreDia);
            tvNumero = itemView.findViewById(R.id.tvNumeroDia);
        }
    }
}
