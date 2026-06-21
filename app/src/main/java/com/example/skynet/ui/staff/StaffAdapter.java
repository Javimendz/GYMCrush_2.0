package com.example.skynet.ui.staff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.remote.dto.PerfilResponseDto;

import java.util.ArrayList;
import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    public interface OnStaffInteractionListener {
        void onDeleteRole(PerfilResponseDto perfil);
        void onChangeRole(PerfilResponseDto perfil);
    }

    private List<PerfilResponseDto> staffList;
    private List<PerfilResponseDto> staffListFull;
    private final OnStaffInteractionListener listener;

    public StaffAdapter(List<PerfilResponseDto> staffList, OnStaffInteractionListener listener) {
        this.staffList = staffList;
        this.staffListFull = new ArrayList<>(staffList);
        this.listener = listener;
    }

    public void updateList(List<PerfilResponseDto> newList) {
        this.staffList = newList;
        this.staffListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        List<PerfilResponseDto> filteredList = new ArrayList<>();
        for (PerfilResponseDto item : staffListFull) {
            String fullName = (item.getNombre() + " " + (item.getApellidos() != null ? item.getApellidos() : "")).toLowerCase();
            String dni = (item.getDni() != null ? item.getDni() : "").toLowerCase();
            if (fullName.contains(text.toLowerCase()) || dni.contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        staffList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        PerfilResponseDto perfil = staffList.get(position);
        holder.tvNombre.setText(perfil.getNombre() + " " + (perfil.getApellidos() != null ? perfil.getApellidos() : ""));

        // Mostrar el username real. Si no existe, mostrar el ID de usuario para depuración.
        String username = perfil.getUsername();
        if (username != null && !username.isEmpty()) {
            holder.tvUsername.setText("@" + username);
        } else {
            // Esto ayuda a identificar si el usuarioId está llegando correctamente
            holder.tvUsername.setText("Usuario ID: " + (perfil.getUsuarioId() != null ? perfil.getUsuarioId() : "null"));
        }

        boolean isTrainer = false;
        List<String> roles = perfil.getRoles();
        if (roles != null) {
            for (String roleName : roles) {
                if (roleName != null && (roleName.toUpperCase().contains("ENTRENADOR") ||
                        roleName.toUpperCase().contains("COACH") ||
                        roleName.toUpperCase().contains("TRAINER"))) {
                    isTrainer = true;
                    break;
                }
            }
        }

        if (isTrainer) {
            holder.tvRol.setVisibility(View.VISIBLE);
        } else {
            holder.tvRol.setVisibility(View.GONE);
        }

        holder.btnDelete.setVisibility(View.VISIBLE);
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteRole(perfil);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onChangeRole(perfil);
        });
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUsername, tvRol;
        View btnDelete;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvStaffNombre);
            tvUsername = itemView.findViewById(R.id.tvStaffUsername);
            tvRol = itemView.findViewById(R.id.tvStaffRol);
            btnDelete = itemView.findViewById(R.id.btnDeleteStaff);
        }
    }
}
