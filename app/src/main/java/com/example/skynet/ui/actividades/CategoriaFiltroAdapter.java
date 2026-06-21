package com.example.skynet.ui.actividades;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.CategoriaResponseDto;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class CategoriaFiltroAdapter extends RecyclerView.Adapter<CategoriaFiltroAdapter.ViewHolder> {

    private final List<Object> categorias; // List of CategoriaResponseDto or String "Todas"
    private final OnCategoriaClickListener listener;
    private int selectedPosition = 0;

    public interface OnCategoriaClickListener {
        void onCategoriaClick(Object categoria);
    }

    public CategoriaFiltroAdapter(List<Object> categorias, OnCategoriaClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria_filtro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = categorias.get(position);
        String nombre = (item instanceof CategoriaResponseDto) ? ((CategoriaResponseDto) item).getNombre() : item.toString();
        
        holder.tvNombre.setText(nombre);

        if (selectedPosition == position) {
            holder.card.setStrokeColor(Color.parseColor("#CD0277"));
            holder.card.setCardBackgroundColor(Color.parseColor("#33CD0277"));
            holder.tvNombre.setTextColor(Color.parseColor("#CD0277"));
        } else {
            holder.card.setStrokeColor(Color.parseColor("#33FFFFFF"));
            holder.card.setCardBackgroundColor(Color.parseColor("#1E2640"));
            holder.tvNombre.setTextColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onCategoriaClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        MaterialCardView card;

        ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCategoria);
            card = itemView.findViewById(R.id.cardCategoria);
        }
    }
}
