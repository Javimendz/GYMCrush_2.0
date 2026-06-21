package com.example.skynet.ui.rutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.RutinaResponseDto;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class RutinaApiAdapter extends RecyclerView.Adapter<RutinaApiAdapter.ViewHolder> {

    private List<RutinaResponseDto> listaRutinas;
    private OnRutinaClickListener listener;

    public interface OnRutinaClickListener {
        void onCompletar(RutinaResponseDto rutina);
        void onEliminar(RutinaResponseDto rutina);
        void onClick(RutinaResponseDto rutina);
    }

    public RutinaApiAdapter(List<RutinaResponseDto> listaRutinas, OnRutinaClickListener listener) {
        this.listaRutinas = listaRutinas;
        this.listener = listener;
    }

    public void updateList(List<RutinaResponseDto> newList) {
        this.listaRutinas.clear();
        if (newList != null) this.listaRutinas.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_item_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RutinaResponseDto rutina = listaRutinas.get(position);

        // 1. Mostrar nombre
        holder.tvNombre.setText(rutina.getNombreEntrenamiento() != null ? rutina.getNombreEntrenamiento() : "Entrenamiento");
        holder.tvNombre.setVisibility(View.VISIBLE);

        // 2. Mostrar detalles (ej: "3 sets x 12 reps")
        String series = rutina.getSeries() != null ? rutina.getSeries() : "0";
        String reps = rutina.getRepeticiones() != null ? rutina.getRepeticiones() : "0";
        holder.tvDetalles.setText(series + " Series x " + reps + " Reps");

        // 3. Control de estado y botón
        if (rutina.getCompletado() != null && rutina.getCompletado()) {
            holder.tvEstado.setText("COMPLETADO");
            holder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
        } else {
            holder.tvEstado.setText("Empezar Rutina");
            holder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#0088CC")));
        }

        // Listener para el botón principal
        holder.tvEstado.setOnClickListener(v -> listener.onClick(rutina));
        holder.itemView.setOnClickListener(v -> listener.onClick(rutina));
        
        holder.btnOpciones.setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
            popup.getMenu().add("Marcar como completado");
            popup.getMenu().add("Eliminar de la agenda");
            
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Marcar como completado")) {
                    listener.onCompletar(rutina);
                } else if (item.getTitle().equals("Eliminar de la agenda")) {
                    listener.onEliminar(rutina);
                }
                return true;
            });
            popup.show();
        });

        // 5. Cargar Imagen con Glide (Prioridad a urlImagen, luego urlVideo como fallback)
        String imageUrl = (rutina.getUrlImagen() != null && !rutina.getUrlImagen().isEmpty())
                ? rutina.getUrlImagen()
                : rutina.getUrlVideo();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            String urlToLoad = imageUrl;
            if (urlToLoad.contains("youtube.com") || urlToLoad.contains("youtu.be")) {
                String videoId = "";
                if (urlToLoad.contains("v=")) videoId = urlToLoad.split("v=")[1].split("&")[0];
                else if (urlToLoad.contains("youtu.be/")) videoId = urlToLoad.split("youtu.be/")[1].split("\\?")[0];
                
                if (!videoId.isEmpty()) urlToLoad = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
            }

            Glide.with(holder.itemView.getContext())
                    .load(urlToLoad)
                    .centerCrop()
                    .placeholder(R.drawable.cardio)
                    .error(R.drawable.cardio)
                    .into(holder.imgRutina);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load("file:///android_asset/login.jpg")
                    .centerCrop()
                    .into(holder.imgRutina);
        }
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles, tvNombrePlan;
        TextView tvDuracionTag, tvIntensidadTag;
        MaterialButton tvEstado;
        ImageView imgCheck, btnOpciones, imgRutina;
        LinearLayout layoutHeaderPlan, layoutDuracionTag, layoutIntensidadTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRutina);
            tvDetalles = itemView.findViewById(R.id.tvDetallesRutina);
            tvEstado = itemView.findViewById(R.id.tvEstadoRutina);
            tvNombrePlan = itemView.findViewById(R.id.tvNombrePlanPertenece);
            tvDuracionTag = itemView.findViewById(R.id.tvDuracionTagItem);
            tvIntensidadTag = itemView.findViewById(R.id.tvIntensidadTagItem);
            imgCheck = itemView.findViewById(R.id.imgStatusCheck);
            btnOpciones = itemView.findViewById(R.id.btnMasOpciones);
            imgRutina = itemView.findViewById(R.id.imgRutina);
            layoutHeaderPlan = itemView.findViewById(R.id.layoutHeaderPlan);
            layoutDuracionTag = itemView.findViewById(R.id.layoutDuracionTag);
            layoutIntensidadTag = itemView.findViewById(R.id.layoutIntensidadTag);
        }
    }
}