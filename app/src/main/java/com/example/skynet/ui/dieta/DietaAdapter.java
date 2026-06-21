package com.example.skynet.ui.dieta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.DietaResponseDto;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class DietaAdapter extends RecyclerView.Adapter<DietaAdapter.DietaViewHolder> {

    private List<DietaResponseDto> dietas = new ArrayList<>();
    private final OnDietaClickListener listener;

    public interface OnDietaClickListener {
        void onDietaClick(DietaResponseDto dieta);
    }

    public DietaAdapter(OnDietaClickListener listener) {
        this.listener = listener;
    }

    public void setDietas(List<DietaResponseDto> dietas) {
        this.dietas = dietas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DietaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dieta_catalogo, parent, false);
        return new DietaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DietaViewHolder holder, int position) {
        DietaResponseDto dieta = dietas.get(position);
        holder.bind(dieta, listener);
    }

    @Override
    public int getItemCount() {
        return dietas.size();
    }

    static class DietaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre, tvDescripcion, tvCalorias, tvBadge;
        private final ImageView ivFondo;
        private final MaterialButton btnVerMas;

        public DietaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreDieta);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionDieta);
            tvCalorias = itemView.findViewById(R.id.tvCaloriasDieta);
            tvBadge = itemView.findViewById(R.id.tvBadgeTipo);
            ivFondo = itemView.findViewById(R.id.ivDietaFondo);
            btnVerMas = itemView.findViewById(R.id.btnVerDetalles);
        }

        public void bind(DietaResponseDto dieta, OnDietaClickListener listener) {
            tvNombre.setText(dieta.getNombre());
            tvBadge.setText(dieta.getTipo() != null ? dieta.getTipo().toUpperCase() : "DIETA");
            tvDescripcion.setText(dieta.getDescripcion());
            tvCalorias.setText(dieta.getObjetivoCalorico().intValue() + " kcal");

            if (dieta.getImagenUrl() != null && !dieta.getImagenUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(dieta.getImagenUrl())
                        .centerCrop()
                        .placeholder(R.drawable.bg_neon_card)
                        .error(Glide.with(itemView.getContext()).load("file:///android_asset/login.jpg").centerCrop())
                        .into(ivFondo);
            } else {
                Glide.with(itemView.getContext())
                        .load("file:///android_asset/login.jpg")
                        .centerCrop()
                        .into(ivFondo);
            }

            btnVerMas.setOnClickListener(v -> listener.onDietaClick(dieta));
            itemView.setOnClickListener(v -> listener.onDietaClick(dieta));
        }
    }
}