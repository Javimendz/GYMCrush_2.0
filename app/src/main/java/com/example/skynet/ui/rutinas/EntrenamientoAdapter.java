package com.example.skynet.ui.rutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.EntrenamientoResponseDto;
import com.bumptech.glide.Glide;
import java.util.List;

public class EntrenamientoAdapter extends RecyclerView.Adapter<EntrenamientoAdapter.ViewHolder> {

    private List<EntrenamientoResponseDto> lista;
    private OnItemClickListener listener;
    private boolean isAdmin = false;
    private long currentUserId = -1;

    public interface OnItemClickListener {
        void onStartWorkout(EntrenamientoResponseDto entrenamiento);
        void onDeleteWorkout(EntrenamientoResponseDto entrenamiento);
    }

    public EntrenamientoAdapter(List<EntrenamientoResponseDto> lista) {
        this.lista = lista;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setAdmin(boolean admin, long currentUserId) {
        this.isAdmin = admin;
        this.currentUserId = currentUserId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_item_lista, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EntrenamientoResponseDto item = lista.get(position);
        holder.tvNombre.setText(item.getNombre());
        String categoria = item.getCategoria() != null ? item.getCategoria() : "Sin categoría";
        holder.tvDetalles.setText(categoria);

        // Tags de Duración e Intensidad
        if (item.getDuracion() != null && item.getDuracion() > 0) {
            holder.layoutDuracionTag.setVisibility(View.VISIBLE);
            holder.tvDuracionTag.setText(item.getDuracion() + " min");
        } else {
            holder.layoutDuracionTag.setVisibility(View.GONE);
        }

        if (item.getIntensidad() != null && !item.getIntensidad().isEmpty()) {
            holder.tvIntensidadTag.setVisibility(View.VISIBLE);
            holder.tvIntensidadTag.setText(item.getIntensidad().toUpperCase());
        } else {
            holder.tvIntensidadTag.setVisibility(View.GONE);
        }

        // Tag Global/Personal
        if (item.isEsGlobal()) {
            holder.tvGlobalTag.setText("OFICIAL");
            holder.tvGlobalTag.setBackgroundResource(R.drawable.bg_chip_global);
        } else {
            holder.tvGlobalTag.setText("PERSONAL");
            holder.tvGlobalTag.setBackgroundResource(R.drawable.bg_chip_personal);
        }

        // Cargar Imagen con Glide (Prioridad a urlImagen, luego urlVideo como fallback para GIFs)
        String imageUrl = (item.getUrlImagen() != null && !item.getUrlImagen().isEmpty()) 
                ? item.getUrlImagen() 
                : item.getUrlVideo();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_workout)
                    .error(R.drawable.ic_workout)
                    .centerCrop()
                    .into(holder.imgRutina);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load("file:///android_asset/login.jpg")
                    .centerCrop()
                    .into(holder.imgRutina);
        }

        boolean isOwner = item.getUsuarioId() != null && item.getUsuarioId() == currentUserId;
        boolean canManage = isAdmin || isOwner;

        holder.btnMasOpciones.setVisibility(canManage ? View.VISIBLE : View.GONE);
        holder.btnMasOpciones.setOnClickListener(v -> {
            if (listener != null) {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
                popup.getMenu().add("Eliminar Entrenamiento");

                popup.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getTitle().equals("Eliminar Entrenamiento")) {
                        listener.onDeleteWorkout(item);
                    }
                    return true;
                });
                popup.show();
            }
        });

        holder.tvEstado.setOnClickListener(v -> {
            if (listener != null) listener.onStartWorkout(item);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void updateLista(List<EntrenamientoResponseDto> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles, tvEstado, tvGlobalTag;
        TextView tvDuracionTag, tvIntensidadTag;
        ImageView btnMasOpciones, imgRutina;
        LinearLayout layoutDuracionTag;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvNombre = v.findViewById(R.id.tvNombreRutina);
            tvDetalles = v.findViewById(R.id.tvDetallesRutina);
            tvEstado = v.findViewById(R.id.tvEstadoRutina);
            tvDuracionTag = v.findViewById(R.id.tvDuracionTagItem);
            tvIntensidadTag = v.findViewById(R.id.tvIntensidadTagItem);
            tvGlobalTag = v.findViewById(R.id.tvGlobalTagEntrenamiento);
            btnMasOpciones = v.findViewById(R.id.btnMasOpciones);
            imgRutina = v.findViewById(R.id.imgRutina);
            layoutDuracionTag = v.findViewById(R.id.layoutDuracionTag);
        }
    }
}