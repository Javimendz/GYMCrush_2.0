package com.example.skynet.ui.social;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import com.example.skynet.Config;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class SocialFragment extends Fragment implements ChatAdapter.OnMessageOptionsListener {

    private static final String TAG = "SocialFragment";
    // Usamos la configuración centralizada para el WebSocket
    private static final String WS_URL = Config.WS_URL;

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<MensajeChat> listaMensajes;
    private EditText etMensaje;
    private String nombreUsuario;
    private Long usuarioId;
    private Gson gson;
    private ApiService apiService;

    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        rvChat = view.findViewById(R.id.rv_chat);
        etMensaje = view.findViewById(R.id.et_mensaje);
        View btnEnviar = view.findViewById(R.id.btn_enviar);

        gson = new Gson();
        listaMensajes = new ArrayList<>();

        SharedPreferences userPrefs = getActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        nombreUsuario = userPrefs.getString("nombre_usuario", "Usuario");
        usuarioId = userPrefs.getLong("user_id", -1L);
        String token = userPrefs.getString("auth_token", "");

        adapter = new ChatAdapter(listaMensajes, nombreUsuario, usuarioId, this);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        btnEnviar.setOnClickListener(v -> enviarMensaje());

        view.findViewById(R.id.btnVolverSocial).setOnClickListener(v -> {
            if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).mostrarHome();
            }
        });

        apiService = RetrofitClient.getApiService();
        cargarHistorial();
        initStomp(token);

        return view;
    }

    private void cargarHistorial() {
        Log.d(TAG, "Cargando historial de chat...");
        apiService.getHistorialChat().enqueue(new Callback<List<ChatMessageDto>>() {
            @Override
            public void onResponse(Call<List<ChatMessageDto>> call, Response<List<ChatMessageDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatMessageDto> historial = response.body();
                    Log.d(TAG, "Historial recibido: " + historial.size() + " mensajes");

                    listaMensajes.clear();
                    // Recorremos al revés para que el más nuevo quede abajo
                    for (int i = historial.size() - 1; i >= 0; i--) {
                        ChatMessageDto dto = historial.get(i);

                        // FILTRO CRÍTICO: Si el mensaje tiene ticketId o el tipo es TICKET, NO es para el chat social.
                        String tId = dto.getTicketId();
                        boolean tieneTicketId = tId != null && !tId.isEmpty() && !tId.equalsIgnoreCase("null") && !tId.equals("0");
                        boolean esTipoTicket = "TICKET".equalsIgnoreCase(dto.tipo);

                        if (tieneTicketId || esTipoTicket) {
                            Log.d(TAG, "Omitiendo mensaje de historial (Ticket detectado). ID: " + dto.id + ", TicketId: " + tId + ", Tipo: " + dto.tipo);
                            continue;
                        }

                        // Lógica robusta para determinar si el mensaje es mío
                        boolean esMio = false;
                        if (dto.usuarioId != null && dto.usuarioId != 0 && dto.usuarioId != -1L) {
                            esMio = dto.usuarioId.equals(usuarioId);
                        }
                        
                        if (!esMio && dto.remitente != null && nombreUsuario != null) {
                            String msgUser = dto.remitente.trim().toLowerCase();
                            String currentUser = nombreUsuario.trim().toLowerCase();
                            esMio = msgUser.equals(currentUser) || msgUser.contains(currentUser) || currentUser.contains(msgUser);
                        }

                        MensajeChat mensaje = new MensajeChat(dto.id, dto.getRemitenteNombre(), dto.usuarioId, dto.contenido, dto.getHoraFormateada());
                        mensaje.setEsMio(esMio);
                        listaMensajes.add(mensaje);
                    }
                    adapter.notifyDataSetChanged();
                    if (!listaMensajes.isEmpty()) {
                        rvChat.post(() -> rvChat.smoothScrollToPosition(listaMensajes.size() - 1));
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta del historial: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessageDto>> call, Throwable t) {
                Log.e(TAG, "Error cargando historial", t);
            }
        });
    }

    private void initStomp(String token) {
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "No hay token de autenticación, cancelando conexión Stomp");
            return;
        }

        Log.d(TAG, "Iniciando Stomp en URL: " + WS_URL);

        // Se eliminan los handshakeHeaders para evitar el error HTTP 500 durante el handshake.
        // La autenticación se maneja a nivel de protocolo STOMP en connectHeaders.
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);

        List<StompHeader> connectHeaders = new ArrayList<>();
        connectHeaders.add(new StompHeader("Authorization", "Bearer " + token));
        connectHeaders.add(new StompHeader("userId", String.valueOf(usuarioId)));

        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG, "¡Conexión Stomp ABIERTA!");
                            break;
                        case ERROR:
                            Log.e(TAG, "Error en el ciclo de vida de Stomp", lifecycleEvent.getException());
                            if (lifecycleEvent.getException() != null && lifecycleEvent.getException().getMessage().contains("500")) {
                                Log.e(TAG, "El servidor rechazó la conexión con un error 500. Revisa los logs del backend.");
                            }
                            break;
                        case CLOSED:
                            Log.w(TAG, "Conexión Stomp CERRADA");
                            break;
                    }
                }, throwable -> Log.e(TAG, "Error crítico en lifecycle", throwable)));

        // Suscribirse a mensajes nuevos en el canal1 según el nuevo backend
        compositeDisposable.add(mStompClient.topic("/topic/canal1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Mensaje recibido RAW: " + topicMessage.getPayload());
                    try {
                        ChatMessageDto dto = gson.fromJson(topicMessage.getPayload(), ChatMessageDto.class);
                        if (dto != null) {
                            Log.d(TAG, "Procesando mensaje STOMP. Tipo: " + dto.tipo + ", TicketId: " + dto.getTicketId());
                            
                            // FILTRO CRÍTICO: Ignorar si es de ticket
                            String tId = dto.getTicketId();
                            boolean tieneTicketId = tId != null && !tId.isEmpty() && !tId.equalsIgnoreCase("null") && !tId.equals("0");
                            boolean esTipoTicket = "TICKET".equalsIgnoreCase(dto.tipo);

                            if (tieneTicketId || esTipoTicket) {
                                Log.d(TAG, "BLOQUEADO: Mensaje de ticket detectado en canal social. Contenido: " + dto.contenido);
                                return;
                            }

                            // Lógica robusta para determinar si el mensaje es mío
                            boolean esMio = false;
                            if (dto.usuarioId != null && dto.usuarioId != 0 && dto.usuarioId != -1L) {
                                esMio = dto.usuarioId.equals(usuarioId);
                            } 
                            
                            if (!esMio && dto.remitente != null && nombreUsuario != null) {
                                String msgUser = dto.remitente.trim().toLowerCase();
                                String currentUser = nombreUsuario.trim().toLowerCase();
                                esMio = msgUser.equals(currentUser) || msgUser.contains(currentUser) || currentUser.contains(msgUser);
                            }
                            
                            Log.d(TAG, "Determinado esMio: " + esMio + " para usuarioId: " + dto.usuarioId + " (Yo: " + usuarioId + ")");

                            MensajeChat mensaje = new MensajeChat(dto.id, dto.getRemitenteNombre(), dto.usuarioId, dto.contenido, dto.getHoraFormateada());
                            mensaje.setEsMio(esMio);

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    listaMensajes.add(mensaje);
                                    adapter.notifyItemInserted(listaMensajes.size() - 1);
                                    rvChat.scrollToPosition(listaMensajes.size() - 1);
                                });
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando mensaje social", e);
                    }
                }, throwable -> Log.e(TAG, "Error en suscripción a mensajes", throwable)));

        // Suscribirse a borrados
        compositeDisposable.add(mStompClient.topic("/topic/canal1.borrados")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Notificación de borrado recibida: " + topicMessage.getPayload());
                    PeticionBorrado peticion = gson.fromJson(topicMessage.getPayload(), PeticionBorrado.class);
                    if (peticion != null) {
                        eliminarMensajeDeLista(peticion.id);
                    }
                }, throwable -> Log.e(TAG, "Error en suscripción a borrados", throwable)));

        // Suscribirse a ediciones
        compositeDisposable.add(mStompClient.topic("/topic/canal1.editados")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Notificación de edición recibida: " + topicMessage.getPayload());
                    ChatMessageDto dto = gson.fromJson(topicMessage.getPayload(), ChatMessageDto.class);
                    if (dto != null) {
                        actualizarMensajeEnLista(dto.id, dto.contenido);
                    }
                }, throwable -> Log.e(TAG, "Error en suscripción a ediciones", throwable)));

        mStompClient.connect(connectHeaders);
    }

    private void actualizarMensajeEnLista(String id, String nuevoContenido) {
        if (id == null) return;
        getActivity().runOnUiThread(() -> {
            for (int i = 0; i < listaMensajes.size(); i++) {
                if (id.equals(listaMensajes.get(i).getId())) {
                    listaMensajes.get(i).setTexto(nuevoContenido);
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        });
    }

    private void eliminarMensajeDeLista(Long id) {
        if (id == null) return;
        String idStr = String.valueOf(id);
        getActivity().runOnUiThread(() -> {
            for (int i = 0; i < listaMensajes.size(); i++) {
                if (idStr.equals(listaMensajes.get(i).getId())) {
                    listaMensajes.remove(i);
                    adapter.notifyItemRemoved(i);
                    break;
                }
            }
        });
    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (!texto.isEmpty() && mStompClient.isConnected()) {
            // El nuevo backend usa ChatRequestDto que espera contenido y tipo
            Map<String, Object> payload = new HashMap<>();
            payload.put("contenido", texto);
            payload.put("tipo", "CHAT"); // Cambiado a CHAT para coincidir con el backend

            // Ajustado para usar el MessageMapping "/chat1" del nuevo backend
            compositeDisposable.add(mStompClient.send("/app/chat1", gson.toJson(payload))
                    .subscribe(() -> {
                        Log.d(TAG, "Mensaje enviado a /chat1");
                        getActivity().runOnUiThread(() -> etMensaje.setText(""));
                    }, throwable -> Log.e(TAG, "Error enviando mensaje", throwable)));
        }
    }

    @Override
    public void onEliminar(int position) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_custom_exit);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView titulo = dialog.findViewById(R.id.tv_titulo_exit);
        TextView mensajeText = dialog.findViewById(R.id.tv_mensaje_exit);
        com.google.android.material.button.MaterialButton confirm = dialog.findViewById(R.id.btn_confirmar_exit);
        
        titulo.setText("ELIMINAR MENSAJE");
        mensajeText.setText("¿Estás seguro de que deseas eliminar este mensaje?");
        confirm.setText("SÍ");

        View cancel = dialog.findViewById(R.id.btn_cancelar_exit);

        cancel.setOnClickListener(v -> dialog.dismiss());
        confirm.setOnClickListener(v -> {
            MensajeChat mensaje = listaMensajes.get(position);
            if (mensaje.getId() != null) {
                PeticionBorrado peticion = new PeticionBorrado();
                try {
                    peticion.id = Long.parseLong(mensaje.getId());
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error al convertir ID de mensaje a Long: " + mensaje.getId());
                }
                peticion.usuario = nombreUsuario; // Coincide con 'String usuario' del backend
                
                compositeDisposable.add(mStompClient.send("/app/chat1.borrar", gson.toJson(peticion)).subscribe(() -> {
                    Log.d(TAG, "Borrado enviado a /app/chat1.borrar para mensaje ID: " + peticion.id);
                }, throwable -> Log.e(TAG, "Error enviando borrado", throwable)));
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onEditar(int position) {
        MensajeChat mensaje = listaMensajes.get(position);
        
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_editar_mensaje);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText input = dialog.findViewById(R.id.et_editar_mensaje_input);
        input.setText(mensaje.getTexto());

        View cancel = dialog.findViewById(R.id.btn_cancelar_edicion_mensaje);
        View save = dialog.findViewById(R.id.btn_guardar_edicion_mensaje);

        cancel.setOnClickListener(v -> dialog.dismiss());
        save.setOnClickListener(v -> {
            String nuevoTexto = input.getText().toString().trim();
            if (!nuevoTexto.isEmpty() && !nuevoTexto.equals(mensaje.getTexto())) {
                PeticionEdicion peticion = new PeticionEdicion();
                try {
                    peticion.id = Long.parseLong(mensaje.getId());
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error al convertir ID de mensaje a Long: " + mensaje.getId());
                }
                peticion.contenido = nuevoTexto;
                peticion.usuario = nombreUsuario; // Añadido para validación si el DTO lo requiere
                
                compositeDisposable.add(mStompClient.send("/app/chat1.editar", gson.toJson(peticion)).subscribe(() -> {
                    Log.d(TAG, "Edición enviada a /app/chat1.editar para mensaje ID: " + peticion.id);
                }, throwable -> Log.e(TAG, "Error enviando edición", throwable)));
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onCopiar(int position) {
        MensajeChat mensaje = listaMensajes.get(position);
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Mensaje", mensaje.getTexto());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mStompClient != null) mStompClient.disconnect();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }

    // Clases DTO internas para coincidir con el Backend
    public static class ChatMessageDto {
        public String id;
        public String contenido;
        public Long usuarioId;
        public String tipo;

        @SerializedName(value = "ticketId", alternate = {"ticket_id"})
        private Object ticketId; // Usamos Object para manejar String o Number

        @SerializedName(value = "remitente", alternate = {"emisor"})
        public String remitente;

        @SerializedName(value = "hora", alternate = {"fechaEnvio"})
        public String hora;

        public String getTicketId() {
            if (ticketId == null) return null;
            return String.valueOf(ticketId);
        }

        public String getRemitenteNombre() {
            return remitente != null ? remitente : "Desconocido";
        }

        public String getHoraFormateada() {
            if (hora != null && hora.contains("T")) {
                try {
                    // Formato ISO del servidor: 2026-04-18T22:18:44.619494
                    LocalDateTime dt = LocalDateTime.parse(hora);
                    return dt.format(DateTimeFormatter.ofPattern("HH:mm"));
                } catch (Exception e) {
                    return hora;
                }
            }
            return hora;
        }
    }

    private static class PeticionBorrado {
        Long id;         // ID del mensaje (Long para coincidir con el backend)
        String usuario;  // Nombre de usuario para validación
    }

    private static class PeticionEdicion {
        Long id;         // ID del mensaje (Long para coincidir con el backend)
        String contenido;
        String usuario;  // Quién edita (opcional, por simetría con borrado)
    }
}