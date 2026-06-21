package com.example.skynet.ui.rutinas;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.ui.ejercicios.Ejercicio;
import java.util.List;

public class RutinaGuardadaAdapter extends RecyclerView.Adapter<RutinaGuardadaAdapter.ViewHolder> {

    private List<RepositorioRutinas.RutinaModel> listaRutinas;
    private boolean forViewPager = false;
    private OnRutinaGuardadaClickListener listener;

    public interface OnRutinaGuardadaClickListener {
        void onDelete(RepositorioRutinas.RutinaModel rutina);
    }

    public RutinaGuardadaAdapter(List<RepositorioRutinas.RutinaModel> listaRutinas) {
        this.listaRutinas = listaRutinas;
    }

    public RutinaGuardadaAdapter(List<RepositorioRutinas.RutinaModel> listaRutinas, OnRutinaGuardadaClickListener listener) {
        this.listaRutinas = listaRutinas;
        this.listener = listener;
    }

    public RutinaGuardadaAdapter(List<RepositorioRutinas.RutinaModel> listaRutinas, boolean forViewPager) {
        this.listaRutinas = listaRutinas;
        this.forViewPager = forViewPager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_item_lista, parent, false);
        if (forViewPager) {
            view.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RepositorioRutinas.RutinaModel rutina = listaRutinas.get(position);
        holder.tvNombre.setText(rutina.nombre);

        String desc = rutina.descripcion;
        if (desc == null || desc.isEmpty() || desc.equals("Rutina personalizada")) {
            // Intentar mostrar primer ejercicio como detalle si no hay descripción
            List<Ejercicio> ejercicios = RepositorioRutinas.getEjerciciosDeRutina(rutina.nombre);
            if (ejercicios != null && !ejercicios.isEmpty()) {
                desc = ejercicios.get(0).getNombre();
                if (ejercicios.size() > 1) desc += " y " + (ejercicios.size() - 1) + " más";
            } else {
                desc = "Rutina personalizada";
            }
        }
        holder.tvDetalles.setText(desc);

        // Lógica de progreso y estado en el botón azul
        if (rutina.completada) {
            holder.tvEstado.setText("COMPLETADA ✓");
            holder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        } else {
            // Calcular progreso parcial
            List<Ejercicio> ejercicios = RepositorioRutinas.getEjerciciosDeRutina(rutina.nombre);
            if (ejercicios != null && !ejercicios.isEmpty()) {
                int total = ejercicios.size();
                int hechos = 0;
                for (Ejercicio e : ejercicios) {
                    if (e.isSeleccionado()) hechos++;
                }
                
                if (hechos > 0) {
                    holder.tvEstado.setText("CONTINUAR (" + hechos + "/" + total + ")");
                    holder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0088CC")));
                } else {
                    holder.tvEstado.setText("Empezar Rutina");
                    holder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0088CC")));
                }
            } else {
                holder.tvEstado.setText("Empezar Rutina");
                holder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0088CC")));
            }
        }

        // El botón ahora es el que lanza la acción
        holder.tvEstado.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleRutinaActivity.class);
            intent.putExtra("NOMBRE_RUTINA", rutina.nombre);
            intent.putExtra("DESCRIPCION", rutina.descripcion);
            intent.putExtra("URL_VIDEO", rutina.urlVideo);
            intent.putExtra("RUTINA_ID", rutina.id);
            intent.putExtra("TUTORIAL_ID", rutina.id);
            intent.putExtra("DURACION", rutina.duracion);
            intent.putExtra("INTENSIDAD", rutina.dificultad);
            intent.putExtra("IS_FROM_AGENDA", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> holder.tvEstado.performClick());

        holder.btnMasOpciones.setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
            popup.getMenu().add("Eliminar rutina");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Eliminar rutina")) {
                    if (listener != null) listener.onDelete(rutina);
                }
                return true;
            });
            popup.show();
        });
    }

    public void updateList(List<RepositorioRutinas.RutinaModel> newList) {
        this.listaRutinas = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles, tvDuracionTag, tvIntensidadTag;
        MaterialButton tvEstado;
        ImageView imgRutina, btnMasOpciones, imgStatusCheck;
        LinearLayout layoutDuracionTag, layoutIntensidadTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRutina);
            tvDetalles = itemView.findViewById(R.id.tvDetallesRutina);
            tvEstado = itemView.findViewById(R.id.tvEstadoRutina);
            tvDuracionTag = itemView.findViewById(R.id.tvDuracionTagItem);
            tvIntensidadTag = itemView.findViewById(R.id.tvIntensidadTagItem);
            imgRutina = itemView.findViewById(R.id.imgRutina);
            btnMasOpciones = itemView.findViewById(R.id.btnMasOpciones);
            imgStatusCheck = itemView.findViewById(R.id.imgStatusCheck);
            layoutDuracionTag = itemView.findViewById(R.id.layoutDuracionTag);
            layoutIntensidadTag = itemView.findViewById(R.id.layoutIntensidadTag);
        }
    }
}