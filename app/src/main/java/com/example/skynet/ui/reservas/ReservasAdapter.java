package com.example.skynet.ui.reservas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.ReservaResponseDto;
import java.util.List;

public class ReservasAdapter extends RecyclerView.Adapter<ReservasAdapter.ViewHolder> {

    private List<ReservaResponseDto> reservas;
    private OnReservaClickListener listener;
    private boolean isAdmin = false;

    public interface OnReservaClickListener {
        void onCancelarClick(ReservaResponseDto reserva);
    }

    public ReservasAdapter(List<ReservaResponseDto> reservas, OnReservaClickListener listener) {
        this.reservas = reservas;
        this.listener = listener;
    }

    public void setAdminMode(boolean admin) {
        this.isAdmin = admin;
        notifyDataSetChanged();
    }

    public void setReservas(List<ReservaResponseDto> nuevasReservas) {
        this.reservas = nuevasReservas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReservaResponseDto reserva = reservas.get(position);
        
        String hora = reserva.getHoraInicio() != null ? reserva.getHoraInicio() : "--:--";
        String nombreClase = reserva.getNombreActividad() != null ? reserva.getNombreActividad() : "Actividad";
        String sala = "Sin sala";
        try {
            sala = reserva.getNombreSala();
            if (sala == null) sala = "Sin sala";
        } catch (Exception e) {
            // Fallback en caso de error de compilación fantasma o nulo
        }

        holder.tvHoraClase.setText(hora + " - " + nombreClase);
        
        String detalle = "(" + sala + ")";
        if (isAdmin && reserva.getUsuarioId() != null) {
            detalle += " - User ID: " + reserva.getUsuarioId();
        }
        holder.tvDetalleClase.setText(detalle);
        
        if (position % 2 == 0) {
            holder.viewColor.setBackgroundColor(Color.parseColor("#FF4081"));
        } else {
            holder.viewColor.setBackgroundColor(Color.parseColor("#0A1128"));
        }

        holder.btnCancelar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelarClick(reserva);
            }
        });

        if (isAdmin) {
            ((com.google.android.material.button.MaterialButton)holder.btnCancelar).setText("GESTIONAR");
        } else {
            ((com.google.android.material.button.MaterialButton)holder.btnCancelar).setText("CANCELAR");
        }
    }

    @Override
    public int getItemCount() {
        return reservas != null ? reservas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHoraClase, tvDetalleClase;
        View viewColor;
        View btnCancelar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHoraClase = itemView.findViewById(R.id.tvHoraClase);
            tvDetalleClase = itemView.findViewById(R.id.tvDetalleClase);
            viewColor = itemView.findViewById(R.id.viewIndicadorColor);
            btnCancelar = itemView.findViewById(R.id.btnCancelarReserva);
        }
    }
}