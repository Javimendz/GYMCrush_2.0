package com.example.skynet.ui.social;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<MensajeChat> mensajes;
    private String usuarioActual;
    private Long usuarioIdActual;
    private OnMessageOptionsListener optionsListener;

    public interface OnMessageOptionsListener {
        void onEliminar(int position);
        void onEditar(int position);
        void onCopiar(int position);
    }

    public ChatAdapter(List<MensajeChat> mensajes, String usuarioActual, Long usuarioIdActual, OnMessageOptionsListener optionsListener) {
        this.mensajes = mensajes;
        this.usuarioActual = usuarioActual;
        this.usuarioIdActual = usuarioIdActual;
        this.optionsListener = optionsListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        MensajeChat mensaje = mensajes.get(position);
        holder.tvUsuario.setText(mensaje.getUsuario());
        holder.tvTexto.setText(mensaje.getTexto());
        holder.tvHora.setText(mensaje.getHora());

        // Lógica de autoría robusta (ID o Nombre)
        boolean esMio = false;
        if (usuarioIdActual != null && usuarioIdActual != -1L && mensaje.getUsuarioId() != null) {
            esMio = usuarioIdActual.equals(mensaje.getUsuarioId());
        }
        
        if (!esMio && mensaje.getUsuario() != null && usuarioActual != null) {
            String msgUser = mensaje.getUsuario().trim().toLowerCase();
            String currentUser = usuarioActual.trim().toLowerCase();
            // Comparación flexible: coincidencia exacta o uno contiene al otro (ej. javi vs javier)
            esMio = msgUser.equals(currentUser) || msgUser.contains(currentUser) || currentUser.contains(msgUser);
        }

        // --- APLICACIÓN DE ESTILOS Y ALINEACIÓN ---
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) holder.cvMensaje.getLayoutParams();
        // Convertimos 80dp a píxeles para el margen del lado opuesto
        int marginPx = (int) (80 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
        
        if (esMio) {
            // MIS MENSAJES: Derecha + Rosa
            params.gravity = android.view.Gravity.END;
            params.setMargins(marginPx, 8, 0, 8); 
            holder.cvMensaje.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.brand_pink));
            
            holder.tvUsuario.setVisibility(View.GONE); // Ocultamos el nombre en tus propios mensajes
            holder.tvTexto.setTextColor(Color.WHITE);
            holder.tvHora.setTextColor(Color.parseColor("#E0E0E0"));
        } else {
            // OTROS: Izquierda + Azul
            params.gravity = android.view.Gravity.START;
            params.setMargins(0, 8, marginPx, 8);
            holder.cvMensaje.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.card_surface));
            
            holder.tvUsuario.setVisibility(View.VISIBLE);
            holder.tvUsuario.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.brand_pink));
            holder.tvTexto.setTextColor(Color.WHITE);
            holder.tvHora.setTextColor(Color.parseColor("#99FFFFFF"));
        }
        
        holder.cvMensaje.setLayoutParams(params);

        final boolean finalEsMio = esMio;
        View.OnLongClickListener longClickListener = v -> {
            showCustomOptionsDialog(v, position, finalEsMio);
            return true;
        };
        holder.itemView.setOnLongClickListener(longClickListener);
        holder.tvTexto.setOnLongClickListener(longClickListener);
    }

    private void showCustomOptionsDialog(View view, int position, boolean esMio) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.dialog_mensaje_opciones);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        View btnCopiar = dialog.findViewById(R.id.btn_opcion_copiar);
        View btnEditar = dialog.findViewById(R.id.btn_opcion_editar);
        View btnEliminar = dialog.findViewById(R.id.btn_opcion_eliminar);
        View btnCancelar = dialog.findViewById(R.id.btn_opcion_cancelar);

        // Si el mensaje no es mío, ocultamos Editar y Eliminar
        if (!esMio) {
            btnEditar.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);
        }

        btnCopiar.setOnClickListener(v -> {
            optionsListener.onCopiar(position);
            dialog.dismiss();
        });

        btnEditar.setOnClickListener(v -> {
            optionsListener.onEditar(position);
            dialog.dismiss();
        });

        btnEliminar.setOnClickListener(v -> {
            optionsListener.onEliminar(position);
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvTexto, tvHora;
        androidx.cardview.widget.CardView cvMensaje;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tv_usuario_chat);
            tvTexto = itemView.findViewById(R.id.tv_texto_mensaje);
            tvHora = itemView.findViewById(R.id.tv_hora_mensaje);
            cvMensaje = itemView.findViewById(R.id.cv_mensaje);
        }
    }
}
