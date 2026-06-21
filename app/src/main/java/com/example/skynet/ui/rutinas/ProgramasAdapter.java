package com.example.skynet.ui.rutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.EntrenamientoResponseDto;
import java.util.List;

public class ProgramasAdapter extends RecyclerView.Adapter<ProgramasAdapter.ViewHolder> {

    private List<EntrenamientoResponseDto> programas;
    private OnProgramClickListener listener;

    public interface OnProgramClickListener {
        void onProgramClick(EntrenamientoResponseDto programa);
    }

    public ProgramasAdapter(List<EntrenamientoResponseDto> programas, OnProgramClickListener listener) {
        this.programas = programas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_programa_explorar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EntrenamientoResponseDto programa = programas.get(position);
        holder.tvNombre.setText(programa.getNombre());
        holder.tvInfo.setText(programa.getDuracion() + " min • " + programa.getIntensidad());
        
        // Mocking the logo text based on name or category
        if (programa.getNombre().toLowerCase().contains("push")) {
            holder.tvLogo.setText("PUSH\nPULL\nLEGS");
        } else if (programa.getNombre().toLowerCase().contains("full")) {
            holder.tvLogo.setText("FULL\nBODY");
        } else {
            holder.tvLogo.setText("SKY\nNET");
        }

        holder.itemView.setOnClickListener(v -> listener.onProgramClick(programa));
    }

    @Override
    public int getItemCount() {
        return programas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvInfo, tvLogo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePrograma);
            tvInfo = itemView.findViewById(R.id.tvInfoPrograma);
            tvLogo = itemView.findViewById(R.id.tvLogoPrograma);
        }
    }
}