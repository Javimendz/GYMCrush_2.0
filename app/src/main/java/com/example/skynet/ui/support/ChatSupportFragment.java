package com.example.skynet.ui.support;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.TicketRequestDto;
import com.example.skynet.data.remote.dto.TicketResponseDto;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatSupportFragment extends Fragment {

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<ChatMessage> messageList;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvStatus;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_support, container, false);

        apiService = RetrofitClient.getApiService();
        rvChat = view.findViewById(R.id.rvChat);
        etMessage = view.findViewById(R.id.etChatMessage);
        btnSend = view.findViewById(R.id.btnSendChat);
        tvStatus = view.findViewById(R.id.tvChatStatus);

        messageList = new ArrayList<>();
        adapter = new ChatAdapter(messageList);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        // Mensaje de bienvenida del bot
        addBotMessage("¡Hola! Soy el asistente de GYMCrush. ¿En qué puedo ayudarte hoy?");

        btnSend.setOnClickListener(v -> sendMessage());

        view.findViewById(R.id.btnVerTickets).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new TicketListFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        view.findViewById(R.id.btnBackChat).setOnClickListener(v -> {
            if (getActivity() != null) {
                // Simplemente damos un paso atrás en el historial.
                // Como ContactoFragment nos guardó aquí, volverá a Contacto mágicamente.
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        // Añadir mensaje del usuario
        messageList.add(new ChatMessage(text, true));
        adapter.notifyItemInserted(messageList.size() - 1);
        rvChat.scrollToPosition(messageList.size() - 1);
        etMessage.setText("");

        // Simular "Escribiendo..."
        tvStatus.setText("Escribiendo...");
        tvStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            processBotResponse(text);
        }, 1500);
    }

    private void processBotResponse(String userMessage) {
        tvStatus.setText("En línea");
        tvStatus.setTextColor(0xFF00FF88);

        String response;
        String lowerMsg = userMessage.toLowerCase();

        if (lowerMsg.contains("hola") || lowerMsg.contains("buenas")) {
            response = "¡Hola de nuevo! Recuerda que puedo ayudarte con tus rutinas, pagos o problemas técnicos.";
        } else if (lowerMsg.contains("rutina") || lowerMsg.contains("entrenar")) {
            response = "Puedes ver tus rutinas asignadas en la sección 'Mis Rutinas' del menú principal.";
        } else if (lowerMsg.contains("ticket") || lowerMsg.contains("problema") || lowerMsg.contains("ayuda") || lowerMsg.contains("fallo")) {
            response = "He detectado que podrías necesitar asistencia técnica. ¿Quieres que cree un ticket de soporte con tu mensaje?";
            addBotMessage(response);
            // Opción especial: Crear ticket
            addBotMessage("Escribe 'SI' para confirmar la creación del ticket.");
            return;
        } else if (lowerMsg.equals("si") && messageList.size() > 1) {
            crearTicketSoporte(messageList.get(messageList.size() - 2).getText());
            return;
        } else {
            response = "Entiendo. Si esto es una incidencia técnica, escribe 'AYUDA' y crearé un ticket para que un humano lo revise.";
        }

        addBotMessage(response);
    }

    private void addBotMessage(String text) {
        messageList.add(new ChatMessage(text, false));
        adapter.notifyItemInserted(messageList.size() - 1);
        rvChat.scrollToPosition(messageList.size() - 1);
    }

    private void crearTicketSoporte(String descripcion) {
        tvStatus.setText("Creando ticket...");

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        if (userId == -1) {
            tvStatus.setText("En línea");
            addBotMessage("Error: No se pudo identificar al usuario. Por favor, inicia sesión de nuevo.");
            return;
        }

        TicketRequestDto request = new TicketRequestDto("Consulta desde Chat AI", descripcion);

        apiService.crearTicket(userId, request).enqueue(new Callback<ApiResponseDto<TicketResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<TicketResponseDto>> call, Response<ApiResponseDto<TicketResponseDto>> response) {
                tvStatus.setText("En línea");
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    addBotMessage("¡Listo! He creado el ticket #" + response.body().getDatos().getId() + ". Un agente humano te contactará pronto.");
                } else {
                    addBotMessage("Lo siento, hubo un error al crear el ticket. Inténtalo más tarde.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<TicketResponseDto>> call, Throwable t) {
                tvStatus.setText("En línea");
                addBotMessage("Error de conexión. No he podido crear el ticket.");
            }
        });
    }
}
