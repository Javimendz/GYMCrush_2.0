package com.example.skynet.ui.rutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import java.util.List;

public class CategoriasExplorarAdapter extends RecyclerView.Adapter<CategoriasExplorarAdapter.ViewHolder> {

    private List<CategoriaExplorar> categorias;

    public CategoriasExplorarAdapter(List<CategoriaExplorar> categorias) {
        this.categorias = categorias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria_rutina, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoriaExplorar cat = categorias.get(position);
        holder.tvNombre.setText(cat.getNombre());
        holder.ivIcono.setImageResource(cat.getIconoResId());
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView ivIcono;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCategoria);
            ivIcono = itemView.findViewById(R.id.ivIconoCategoria);
        }
    }

    public static class CategoriaExplorar {
        private String nombre;
        private int iconoResId;

        public CategoriaExplorar(String nombre, int iconoResId) {
            this.nombre = nombre;
            this.iconoResId = iconoResId;
        }

        public String getNombre() { return nombre; }
        public int getIconoResId() { return iconoResId; }
    }
}