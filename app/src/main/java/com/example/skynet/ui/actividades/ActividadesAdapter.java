package com.example.skynet.ui.actividades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.ActividadResponseDto;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ActividadesAdapter extends RecyclerView.Adapter<ActividadesAdapter.ActividadViewHolder> {

    private List<ActividadResponseDto> actividades;
    private final OnActividadClickListener listener;
    private boolean adminMode = false;

    public interface OnActividadClickListener {
        void onActividadClick(ActividadResponseDto actividad);
        void onEditClick(ActividadResponseDto actividad);
    }

    public ActividadesAdapter(List<ActividadResponseDto> actividades, OnActividadClickListener listener) {
        this.actividades = actividades;
        this.listener = listener;
    }

    public void setActividades(List<ActividadResponseDto> actividades) {
        this.actividades = actividades;
        notifyDataSetChanged();
    }

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        ActividadResponseDto actividad = actividades.get(position);
        holder.tvNombre.setText(actividad.getNombre());
        holder.tvSala.setText(actividad.getSala());
        holder.tvDuracion.setText(actividad.getDuracion() + " min");
        holder.tvPrecio.setText(actividad.getPrecio() + "€");
        holder.tvDescripcion.setText(actividad.getDescripcion());

        if (adminMode) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(actividad));
        } else {
            holder.btnEdit.setVisibility(View.GONE);
        }

        // Simular imagen si no hay una en el DTO
        Glide.with(holder.itemView.getContext())
                .load("https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&q=80&w=500") // URL más estable
                .placeholder(R.drawable.bg_neon_card)
                .error(Glide.with(holder.itemView.getContext()).load("file:///android_asset/login.jpg").centerCrop())
                .centerCrop()
                .into(holder.ivActividad);
        
        if (actividad.getCategoria() != null) {
            holder.tvCategoria.setText(actividad.getCategoria().getNombre().toUpperCase());
            holder.tvCategoria.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategoria.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onActividadClick(actividad));
    }

    @Override
    public int getItemCount() {
        return actividades != null ? actividades.size() : 0;
    }

    public static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvSala, tvDuracion, tvPrecio, tvDescripcion, tvCategoria;
        ImageView ivActividad;
        ImageButton btnEdit;

        public ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreActividad);
            tvSala = itemView.findViewById(R.id.tvSala);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvDuracion = itemView.findViewById(R.id.tvDuracion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            ivActividad = itemView.findViewById(R.id.ivActividad);
            btnEdit = itemView.findViewById(R.id.btnEditActividad);
        }
    }
}
