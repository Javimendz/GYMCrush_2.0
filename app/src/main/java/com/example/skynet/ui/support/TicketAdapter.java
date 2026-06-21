package com.example.skynet.ui.support;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.TicketResponseDto;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<TicketResponseDto> tickets;
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(TicketResponseDto ticket);
    }

    public TicketAdapter(List<TicketResponseDto> tickets, OnTicketClickListener listener) {
        this.tickets = tickets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        TicketResponseDto ticket = tickets.get(position);
        holder.tvAsunto.setText(ticket.getAsunto());
        holder.tvMensaje.setText(ticket.getMensaje());
        holder.tvStatus.setText(ticket.getEstado());
        holder.tvId.setText("#" + ticket.getId());

        if (ticket.getNombreCompletoUsuario() != null && !ticket.getNombreCompletoUsuario().isEmpty()) {
            holder.tvUser.setVisibility(View.VISIBLE);
            holder.tvUser.setText("De: " + ticket.getNombreCompletoUsuario());
        } else {
            holder.tvUser.setVisibility(View.GONE);
        }

        // Color según estado (soporta tanto ENUM en inglés como strings en español para retrocompatibilidad)
        int color = 0xFFCD0277; // Default (ABIERTO/PENDIENTE)
        if (ticket.getEstado() != null) {
            String estado = ticket.getEstado().toUpperCase();
            switch (estado) {
                case "PENDIENTE":
                case "ABIERTO":
                    color = 0xFFCD0277;
                    break;
                case "RESUELTO":
                    color = 0xFF4CAF50;
                    break;
                case "CERRADO":
                    color = 0xFF757575;
                    break;
            }
        }
        holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTicketClick(ticket);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvAsunto, tvMensaje, tvStatus, tvId, tvUser;

        TicketViewHolder(View itemView) {
            super(itemView);
            tvAsunto = itemView.findViewById(R.id.tvTicketAsunto);
            tvMensaje = itemView.findViewById(R.id.tvTicketMensaje);
            tvStatus = itemView.findViewById(R.id.tvTicketStatus);
            tvId = itemView.findViewById(R.id.tvTicketId);
            tvUser = itemView.findViewById(R.id.tvTicketUser);
        }
    }
}
