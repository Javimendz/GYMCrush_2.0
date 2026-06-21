package com.example.skynet.ui.ejercicios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.skynet.R;
import java.util.List;

public class EntrenamientoAdapter extends RecyclerView.Adapter<EntrenamientoAdapter.ViewHolder> {

    private List<EntrenamientoDto> ejercicios;
    private List<Long> selectedIds;
    private final OnEjercicioClickListener listener;

    public interface OnEjercicioClickListener {
        void onEjercicioClick(EntrenamientoDto ejercicio);
    }

    public EntrenamientoAdapter(List<EntrenamientoDto> ejercicios, OnEjercicioClickListener listener) {
        this.ejercicios = ejercicios;
        this.selectedIds = new java.util.ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<EntrenamientoDto> newList, List<Long> selectedIds) {
        this.ejercicios = newList;
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int view) {
        View viewElement = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio_catalogo, parent, false);
        return new ViewHolder(viewElement);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EntrenamientoDto ejercicio = ejercicios.get(position);
        holder.tvNombre.setText(ejercicio.getNombre());
        holder.tvMusculo.setText(ejercicio.getMusculo());

        boolean isSelected = selectedIds.contains(ejercicio.getId());
        
        if (isSelected) {
            holder.ivAction.setImageResource(android.R.drawable.checkbox_on_background);
            holder.ivAction.setBackgroundResource(R.drawable.bg_circle_neon);
            holder.ivAction.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#1A0088CC"));
        } else {
            holder.ivAction.setImageResource(R.drawable.ic_arrow_forward);
            holder.ivAction.setBackgroundResource(R.drawable.bg_circle_gray);
            holder.ivAction.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#0088CC")));
            holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }

        Glide.with(holder.itemView.getContext())
                .load(ejercicio.getImagenUrl() != null && !ejercicio.getImagenUrl().isEmpty() ? ejercicio.getImagenUrl() : "file:///android_asset/login.jpg")
                .placeholder(R.drawable.ic_workout)
                .error(Glide.with(holder.itemView.getContext()).load("file:///android_asset/login.jpg").circleCrop())
                .circleCrop()
                .into(holder.ivImagen);

        holder.itemView.setOnClickListener(v -> listener.onEjercicioClick(ejercicio));
    }

    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvMusculo;
        ImageView ivImagen, ivAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
            tvMusculo = itemView.findViewById(R.id.tvMusculoEjercicio);
            ivImagen = itemView.findViewById(R.id.ivEjercicio);
            ivAction = itemView.findViewById(R.id.ivActionIcon);
        }
    }
}
