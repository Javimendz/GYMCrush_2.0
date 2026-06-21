package com.example.skynet.ui.dieta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.NutricionResponseDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorialNutricionAdapter extends RecyclerView.Adapter<HistorialNutricionAdapter.ViewHolder> {

    private final List<NutricionResponseDto> historial = new ArrayList<>();
    private final OnPlanClickListener listener;

    public interface OnPlanClickListener {
        void onPlanClick(NutricionResponseDto plan);
    }

    public HistorialNutricionAdapter(OnPlanClickListener listener) {
        this.listener = listener;
    }

    // MEJORA 2: Limpiar y añadir en lugar de reasignar
    public void setHistorial(List<NutricionResponseDto> nuevoHistorial) {
        this.historial.clear();
        if (nuevoHistorial != null) {
            this.historial.addAll(nuevoHistorial);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nutricion_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NutricionResponseDto plan = historial.get(position);
        holder.bind(plan, listener);
    }

    @Override
    public int getItemCount() {
        return historial.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvNombre, tvCalorias, tvResumen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaHistorial);
            tvNombre = itemView.findViewById(R.id.tvNombrePlanHistorial);
            tvCalorias = itemView.findViewById(R.id.tvCaloriasHistorial);
            tvResumen = itemView.findViewById(R.id.tvResumenComidas);
        }

        public void bind(NutricionResponseDto plan, OnPlanClickListener listener) {

            // MEJORA 1: Formatear la fecha para que sea amigable ("dd/MM/yyyy")
            String fechaMostrada = "Fecha desconocida";
            if (plan.getFechaGeneracion() != null) {
                try {
                    // Si tu backend envía LocalDateTime, suele llegar así: "2026-04-24T12:30:00"
                    String fechaRaw = plan.getFechaGeneracion();
                    // Fallback rápido: Si tiene la 'T', la quitamos y nos quedamos con la fecha
                    if (fechaRaw.contains("T")) {
                        fechaRaw = fechaRaw.split("T")[0]; // Queda "2026-04-24"

                        // Opcional: Convertir de yyyy-MM-dd a dd/MM/yyyy
                        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date date = input.parse(fechaRaw);
                        if (date != null) {
                            fechaMostrada = output.format(date);
                        }
                    } else {
                        fechaMostrada = fechaRaw;
                    }
                } catch (Exception e) {
                    fechaMostrada = plan.getFechaGeneracion(); // Si falla, muestra la original
                }
            }
            tvFecha.setText(fechaMostrada);

            // Título
            String titulo = plan.getNombreDieta() != null && !plan.getNombreDieta().isEmpty()
                    ? plan.getNombreDieta()
                    : (plan.getTipoDieta() != null ? plan.getTipoDieta() : "Plan Personalizado");
            tvNombre.setText(titulo);

            // Calorías
            tvCalorias.setText(plan.getCaloriasObjetivo() + " kcal / día");

            // Resumen de comidas (Mejorado para revisar ambas listas)
            StringBuilder sb = new StringBuilder();
            List<com.example.skynet.data.remote.dto.ComidaDiariaResponseDto> todasLasComidas = new ArrayList<>();

            if (plan.getComidas() != null && !plan.getComidas().isEmpty()) {
                todasLasComidas.addAll(plan.getComidas());
            } else if (plan.getDietas() != null && !plan.getDietas().isEmpty()) {
                todasLasComidas.addAll(plan.getDietas());
            }

            if (!todasLasComidas.isEmpty()) {
                for (int i = 0; i < Math.min(todasLasComidas.size(), 4); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(todasLasComidas.get(i).getNombreAlimento());
                }
                if (todasLasComidas.size() > 4) sb.append("...");
            } else {
                sb.append("Sin registros de alimentos");
            }
            tvResumen.setText(sb.toString());

            // Click listener
            itemView.setOnClickListener(v -> listener.onPlanClick(plan));
        }
    }
}