package com.example.skynet.ui.clases;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.model.Clase;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class ClasesAdapter extends RecyclerView.Adapter<ClasesAdapter.ClaseViewHolder> {

    private List<Clase> clases;
    private OnClaseClickListener listener;
    private boolean adminMode = false;

    public interface OnClaseClickListener {
        void onApuntarseClick(Clase clase);
        default void onEliminarClick(Clase clase) {}
    }

    public ClasesAdapter(List<Clase> clases, OnClaseClickListener listener) {
        this.clases = clases;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clase, parent, false);
        return new ClaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaseViewHolder holder, int position) {
        Clase clase = clases.get(position);
        holder.tvHora.setText(clase.getHora());
        holder.tvNombre.setText(clase.getNombre());
        
        StringBuilder infoBuilder = new StringBuilder();
        String entrenador = clase.getEntrenador();
        String sala = clase.getSala();

        // 1. Añadimos el monitor si existe
        if (entrenador != null && !entrenador.trim().isEmpty() && 
            !entrenador.equalsIgnoreCase("Sin entrenador") && 
            !entrenador.equalsIgnoreCase("null")) {
            infoBuilder.append(entrenador);
        } else {
            infoBuilder.append("Monitor pendiente"); // Texto profesional si no hay dato
        }

        // 2. Añadimos la sala con el separador
        if (sala != null && !sala.trim().isEmpty()) {
            infoBuilder.append(" | ").append(sala);
        }

        holder.tvEntrenador.setText(infoBuilder.toString());
        int cupos = clase.getCuposDisponibles();
        int aforo = clase.getAforoMax();
        
        holder.tvCupos.setText("Plazas: " + cupos + " / " + aforo);

        if (cupos == 0) {
            holder.tvCupos.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_light));
            holder.btnApuntarse.setText("COMPLETO");
            holder.btnApuntarse.setAlpha(0.5f);
        } else if (cupos <= 3) {
            holder.tvCupos.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_light));
            holder.btnApuntarse.setText("¡ÚLTIMAS PLAZAS!");
            holder.btnApuntarse.setAlpha(1.0f);
        } else {
            holder.tvCupos.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
            holder.btnApuntarse.setText("APUNTARSE");
            holder.btnApuntarse.setAlpha(1.0f);
        }

        holder.btnApuntarse.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApuntarseClick(clase);
            }
        });

        if (holder.btnEliminar != null) {
            holder.btnEliminar.setVisibility(adminMode ? View.VISIBLE : View.GONE);
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEliminarClick(clase);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return clases.size();
    }

    public void setClases(List<Clase> nuevasClases) {
        this.clases = nuevasClases;
        notifyDataSetChanged();
    }

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
        notifyDataSetChanged();
    }

    static class ClaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvHora, tvNombre, tvEntrenador, tvCupos;
        MaterialButton btnApuntarse;
        View btnEliminar;

        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora = itemView.findViewById(R.id.tvHoraClase);
            tvNombre = itemView.findViewById(R.id.tvNombreClase);
            tvEntrenador = itemView.findViewById(R.id.tvEntrenador); // Ahora usado para Sala
            tvCupos = itemView.findViewById(R.id.tvCupos);
            btnApuntarse = itemView.findViewById(R.id.btnApuntarse);
            btnEliminar = itemView.findViewById(R.id.btnEliminarClase);
        }
    }
}