package com.example.skynet.ui.servicios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.example.skynet.data.model.Suplemento;
import java.util.List;
import java.util.Locale;

public class SuplementoAdapter extends RecyclerView.Adapter<SuplementoAdapter.ViewHolder> {

    private final List<Suplemento> suplementos;
    private final OnSuplementoClickListener listener;
    private boolean isAdmin = false;

    public interface OnSuplementoClickListener {
        void onSuplementoClick(Suplemento suplemento);
        void onEditClick(Suplemento suplemento);
        void onDeleteClick(Suplemento suplemento);
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
        notifyDataSetChanged();
    }

    public SuplementoAdapter(List<Suplemento> suplementos, OnSuplementoClickListener listener) {
        this.suplementos = suplementos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_suplemento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Suplemento suplemento = suplementos.get(position);
        holder.tvNombre.setText(suplemento.getNombre());
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "%.2f€", suplemento.getPrecio()));

        Glide.with(holder.itemView.getContext())
                .load(suplemento.getImageUrl())
                .placeholder(R.drawable.whey)
                .into(holder.imgProducto);

        holder.itemView.setOnClickListener(v -> listener.onSuplementoClick(suplemento));

        if (isAdmin) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(suplemento));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(suplemento));
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return suplementos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView tvNombre;
        TextView tvPrecio;
        ImageView btnEdit;
        ImageView btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.img_producto);
            tvNombre = itemView.findViewById(R.id.tv_nombre_producto);
            tvPrecio = itemView.findViewById(R.id.tv_precio_producto);
            btnEdit = itemView.findViewById(R.id.btn_edit_producto);
            btnDelete = itemView.findViewById(R.id.btn_delete_producto);
        }
    }
}
