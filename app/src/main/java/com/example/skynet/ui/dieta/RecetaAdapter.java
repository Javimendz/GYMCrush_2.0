package com.example.skynet.ui.dieta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.FatSecretRecipeDto;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class RecetaAdapter extends RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder> {

    private List<FatSecretRecipeDto> recetas = new ArrayList<>();
    private final OnRecetaClickListener listener;

    public interface OnRecetaClickListener {
        void onRecetaClick(FatSecretRecipeDto receta);
    }

    public RecetaAdapter(OnRecetaClickListener listener) {
        this.listener = listener;
    }

    public void setRecetas(List<FatSecretRecipeDto> recetas) {
        this.recetas = recetas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receta_card, parent, false);
        return new RecetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int position) {
        FatSecretRecipeDto receta = recetas.get(position);
        holder.bind(receta, listener);
    }

    @Override
    public int getItemCount() {
        return recetas.size();
    }

    static class RecetaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivReceta;
        private final TextView tvNombre, tvCalorias;
        private final MaterialButton btnVer;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivReceta = itemView.findViewById(R.id.ivReceta);
            tvNombre = itemView.findViewById(R.id.tvNombreReceta);
            tvCalorias = itemView.findViewById(R.id.tvCaloriasReceta);
            btnVer = itemView.findViewById(R.id.btnVerReceta);
        }

        public void bind(FatSecretRecipeDto receta, OnRecetaClickListener listener) {
            tvNombre.setText(receta.getRecipeName());
            if (receta.getRecipeNutrition() != null) {
                tvCalorias.setText(receta.getRecipeNutrition().getCalories() + " kcal");
            }

            if (receta.getRecipeImage() != null && !receta.getRecipeImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(receta.getRecipeImage())
                        .centerCrop()
                        .placeholder(R.drawable.mujer_deporte)
                        .error(Glide.with(itemView.getContext()).load("file:///android_asset/login.jpg").centerCrop())
                        .into(ivReceta);
            } else {
                Glide.with(itemView.getContext())
                        .load("file:///android_asset/login.jpg")
                        .centerCrop()
                        .into(ivReceta);
            }

            btnVer.setOnClickListener(v -> listener.onRecetaClick(receta));
            itemView.setOnClickListener(v -> listener.onRecetaClick(receta));
        }
    }
}