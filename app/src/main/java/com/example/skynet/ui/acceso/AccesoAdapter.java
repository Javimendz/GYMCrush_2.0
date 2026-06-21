package com.example.skynet.ui.acceso;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.remote.dto.AccesoResponseDto;

import java.util.ArrayList;
import java.util.List;

public class AccesoAdapter extends RecyclerView.Adapter<AccesoAdapter.ViewHolder> {

    private List<AccesoResponseDto> accesos = new ArrayList<>();

    public void setAccesos(List<AccesoResponseDto> accesos) {
        this.accesos = accesos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acceso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccesoResponseDto acceso = accesos.get(position);
        holder.tvTipo.setText(acceso.getTipo() != null ? acceso.getTipo() : "ACCESO");
        holder.tvFecha.setText(acceso.getFechaHoraEntrada() != null ? acceso.getFechaHoraEntrada() : "Fecha desconocida");
        
        if ("SALIDA".equalsIgnoreCase(acceso.getTipo())) {
            holder.ivIcono.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            holder.tvTipo.setTextColor(0xFFFF4081); // Color acento/rosa
        } else {
            holder.ivIcono.setImageResource(android.R.drawable.ic_menu_today);
            holder.tvTipo.setTextColor(0xFF4CAF50); // Color verde
        }

        holder.tvStatus.setText(acceso.getMensaje() != null ? acceso.getMensaje() : "Completado");
    }

    @Override
    public int getItemCount() {
        return accesos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcono;
        TextView tvTipo, tvFecha, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcono = itemView.findViewById(R.id.ivAccesoTipo);
            tvTipo = itemView.findViewById(R.id.tvAccesoTipo);
            tvFecha = itemView.findViewById(R.id.tvAccesoFecha);
            tvStatus = itemView.findViewById(R.id.tvAccesoStatus);
        }
    }
}
