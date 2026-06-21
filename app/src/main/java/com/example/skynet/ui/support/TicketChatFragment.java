package com.example.skynet.ui.support;

import com.example.skynet.Config;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Dialog;
    import androidx.appcompat.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.TicketResponseDto;
import com.example.skynet.ui.social.ChatAdapter;
import com.example.skynet.ui.social.MensajeChat;
import com.example.skynet.ui.social.SocialFragment.ChatMessageDto;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class TicketChatFragment extends Fragment implements ChatAdapter.OnMessageOptionsListener {

    private static final String TAG = "TicketChatFragment";
    // Usamos la configuración centralizada para el WebSocket
    private static final String WS_URL = Config.WS_URL;

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<MensajeChat> listaMensajes;
    private EditText etMensaje;
    private TextView tvSubtitle;
    private ImageView btnOptions;

    private Long ticketId;
    private String ticketAsunto;
    private String ticketEstado;
    private String nombreUsuario;
    private Long usuarioId;
    private boolean isAdmin;

    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    private Gson gson;
    private ApiService apiService;

    public static TicketChatFragment newInstance(Long ticketId, String asunto, String estado) {
        TicketChatFragment fragment = new TicketChatFragment();
        Bundle args = new Bundle();
        args.putLong("ticket_id", ticketId);
        args.putString("asunto", asunto);
        args.putString("estado", estado);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ticketId = getArguments().getLong("ticket_id");
            ticketAsunto = getArguments().getString("asunto");
            ticketEstado = getArguments().getString("estado");
        }
        gson = new Gson();
        compositeDisposable = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_chat, container, false);

        rvChat = view.findViewById(R.id.rvChat);
        etMensaje = view.findViewById(R.id.etChatMessage);
        tvSubtitle = view.findViewById(R.id.tvChatSubtitle);
        btnOptions = view.findViewById(R.id.btnChatOptions);
        TextView tvTitle = view.findViewById(R.id.tvChatTitle);

        apiService = RetrofitClient.getApiService();

        tvTitle.setText("Ticket #" + ticketId);
        tvSubtitle.setText(ticketAsunto != null ? ticketAsunto : "Cargando...");

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        nombreUsuario = prefs.getString("nombre_usuario", "Usuario");
        usuarioId = prefs.getLong("user_id", -1L);
        String token = prefs.getString("auth_token", "");
        java.util.Set<String> roles = prefs.getStringSet("roles", new java.util.HashSet<>());
        isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        listaMensajes = new ArrayList<>();
        adapter = new ChatAdapter(listaMensajes, nombreUsuario, usuarioId, this);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        view.findViewById(R.id.btnBackChat).setOnClickListener(v -> {
            if (getActivity() != null) {
                // Simplemente damos un paso atrás en el historial.
                // Como ContactoFragment nos guardó aquí, volverá a Contacto mágicamente.
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.btnSendChat).setOnClickListener(v -> enviarMensaje());

        if ("CERRADO".equalsIgnoreCase(ticketEstado)) {
            desactivarChat();
        }

        cargarHistorial();
        setupOptionsButton();
        initStomp(token);

        return view;
    }

    private void cargarHistorial() {
        Log.d(TAG, "Cargando historial para ticket: " + ticketId);
        apiService.getHistorialTicket(String.valueOf(ticketId)).enqueue(new retrofit2.Callback<List<ChatMessageDto>>() {
            @Override
            public void onResponse(retrofit2.Call<List<ChatMessageDto>> call, retrofit2.Response<List<ChatMessageDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMensajes.clear();
                    for (ChatMessageDto dto : response.body()) {
                        boolean esMio = false;
                        if (dto.usuarioId != null && dto.usuarioId != 0) {
                            esMio = dto.usuarioId.equals(usuarioId);
                        } else if (dto.remitente != null && nombreUsuario != null) {
                            esMio = dto.remitente.equalsIgnoreCase(nombreUsuario);
                        }

                        MensajeChat mensaje = new MensajeChat(dto.id, dto.getRemitenteNombre(), dto.usuarioId, dto.contenido, dto.getHoraFormateada());
                        mensaje.setEsMio(esMio);
                        listaMensajes.add(mensaje);
                    }
                    adapter.notifyDataSetChanged();
                    if (!listaMensajes.isEmpty()) {
                        rvChat.scrollToPosition(listaMensajes.size() - 1);
                    }
                } else {
                    Log.e(TAG, "Error al cargar historial: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<ChatMessageDto>> call, Throwable t) {
                Log.e(TAG, "Error de red cargando historial", t);
            }
        });
    }

    private void setupOptionsButton() {
        btnOptions.setVisibility(View.VISIBLE);
        btnOptions.setOnClickListener(v -> {
            if (isAdmin) {
                mostrarDialogoEstadosAdmin();
            } else {
                mostrarDialogoEstadosUsuario();
            }
        });
    }

    private void mostrarDialogoEstadosAdmin() {
        // Ajustado para coincidir con EnumEstadoTicket del backend (ABIERTO, CERRADO, PENDIENTE, RESUELTO)
        String[] opcionesMostrar = {"Pendiente", "Abierto", "Resuelto", "Cerrado"};
        String[] estadosBackend = {"PENDIENTE", "ABIERTO", "RESUELTO", "CERRADO"};

        new AlertDialog.Builder(getContext())
                .setTitle("Cambiar Estado del Ticket")
                .setItems(opcionesMostrar, (dialog, which) -> {
                    actualizarEstado(estadosBackend[which]);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEstadosUsuario() {
        new AlertDialog.Builder(getContext())
                .setTitle("Gestión de Ticket")
                .setMessage("¿Quieres marcar este ticket como resuelto?")
                .setPositiveButton("Sí, Resuelto", (dialog, which) -> {
                    actualizarEstado("RESUELTO");
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void actualizarEstado(String nuevoEstado) {
        tvSubtitle.setText("Actualizando estado...");
        apiService.resolverTicket(ticketId, nuevoEstado).enqueue(new retrofit2.Callback<ApiResponseDto<TicketResponseDto>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponseDto<TicketResponseDto>> call, retrofit2.Response<ApiResponseDto<TicketResponseDto>> response) {
                if (response.isSuccessful()) {
                    ticketEstado = nuevoEstado;
                    Toast.makeText(getContext(), "Estado actualizado a " + nuevoEstado, Toast.LENGTH_SHORT).show();
                    tvSubtitle.setText("Estado: " + nuevoEstado + " - " + ticketAsunto);
                    
                    if ("CERRADO".equalsIgnoreCase(nuevoEstado)) {
                        desactivarChat();
                    }
                } else {
                    Log.e(TAG, "Error 400/500 al actualizar estado: " + response.code());
                    Toast.makeText(getContext(), "Error al actualizar (Enum mismatch?)", Toast.LENGTH_SHORT).show();
                    tvSubtitle.setText("Error - " + ticketAsunto);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResponseDto<TicketResponseDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                tvSubtitle.setText("Error de red");
            }
        });
    }

    private void desactivarChat() {
        if (etMensaje != null) {
            etMensaje.setEnabled(false);
            etMensaje.setHint("Ticket cerrado - Solo lectura");
        }
        View btnSend = getView() != null ? getView().findViewById(R.id.btnSendChat) : null;
        if (btnSend != null) {
            btnSend.setEnabled(false);
            btnSend.setAlpha(0.5f);
        }
    }

    private void initStomp(String token) {
        if (token == null || token.isEmpty()) return;

        // Se eliminan los handshakeHeaders para evitar el error HTTP 500 durante el handshake.
        // La autenticación se maneja a nivel de protocolo STOMP en connectHeaders.
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);

        List<StompHeader> connectHeaders = new ArrayList<>();
        connectHeaders.add(new StompHeader("Authorization", "Bearer " + token));
        connectHeaders.add(new StompHeader("userId", String.valueOf(usuarioId)));

        compositeDisposable.add(mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG, "Conexión chat ticket abierta");
                            tvSubtitle.setText("En línea - " + ticketAsunto);
                            break;
                        case ERROR:
                            Log.e(TAG, "Error Stomp", lifecycleEvent.getException());
                            tvSubtitle.setText("Error de conexión");
                            break;
                        case CLOSED:
                            tvSubtitle.setText("Desconectado");
                            break;
                    }
                }));

        // Suscripción robusta con logs mejorados
        compositeDisposable.add(mStompClient.topic("/topic/tickets." + ticketId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.i(TAG, "NUEVO MENSAJE RECIBIDO: " + topicMessage.getPayload());
                    try {
                        ChatMessageDto dto = gson.fromJson(topicMessage.getPayload(), ChatMessageDto.class);
                        if (dto != null) {
                            // Uso de las variables correctas: usuarioId y nombreUsuario
                            boolean esMio = false;
                            if (dto.usuarioId != null && dto.usuarioId != 0) {
                                esMio = dto.usuarioId.equals(usuarioId);
                            } else if (dto.remitente != null && nombreUsuario != null) {
                                esMio = dto.remitente.equalsIgnoreCase(nombreUsuario);
                            }

                            Log.d(TAG, "Mensaje procesado - Es mío: " + esMio + " | Emisor: " + dto.remitente);

                            MensajeChat mensaje = new MensajeChat(
                                    dto.id,
                                    dto.remitente != null ? dto.remitente : "Soporte",
                                    dto.usuarioId,
                                    dto.contenido,
                                    dto.getHoraFormateada()
                            );
                            mensaje.setEsMio(esMio);

                            getActivity().runOnUiThread(() -> {
                                listaMensajes.add(mensaje);
                                adapter.notifyItemInserted(listaMensajes.size() - 1);
                                rvChat.scrollToPosition(listaMensajes.size() - 1);
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando mensaje: " + topicMessage.getPayload(), e);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error crítico en la suscripción al ticket #" + ticketId, throwable);
                    // Intentar reconectar si la suscripción muere
                    Toast.makeText(getContext(), "Error de conexión en el chat", Toast.LENGTH_SHORT).show();
                }));

        // Suscribirse a borrados de tickets
        compositeDisposable.add(mStompClient.topic("/topic/tickets." + ticketId + ".borrados")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    PeticionBorrado peticion = gson.fromJson(topicMessage.getPayload(), PeticionBorrado.class);
                    eliminarMensajeDeLista(peticion.id);
                }, throwable -> Log.e(TAG, "Error en suscripción a borrados ticket", throwable)));

        // Suscribirse a ediciones de tickets
        compositeDisposable.add(mStompClient.topic("/topic/tickets." + ticketId + ".editados")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    ChatMessageDto dto = gson.fromJson(topicMessage.getPayload(), ChatMessageDto.class);
                    actualizarMensajeEnLista(dto.id, dto.contenido);
                }, throwable -> Log.e(TAG, "Error en suscripción a ediciones ticket", throwable)));

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

    private void eliminarMensajeDeLista(String id) {
        if (id == null) return;
        getActivity().runOnUiThread(() -> {
            for (int i = 0; i < listaMensajes.size(); i++) {
                if (id.equals(listaMensajes.get(i).getId())) {
                    listaMensajes.remove(i);
                    adapter.notifyItemRemoved(i);
                    break;
                }
            }
        });
    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (!texto.isEmpty() && mStompClient != null && mStompClient.isConnected()) {
            // El nuevo backend usa ChatRequestDto que espera contenido y tipo
            Map<String, Object> payload = new HashMap<>();
            payload.put("contenido", texto);
            payload.put("ticketId", ticketId); // Enviamos el ID del ticket como Long (coincidiendo con backend)
            payload.put("tipo", "TICKET"); 

            Log.d(TAG, "Enviando mensaje de ticket: " + gson.toJson(payload));

            // Ajustado para usar el MessageMapping "/chat.ticket.{ticketId}"
            compositeDisposable.add(mStompClient.send("/app/chat.ticket." + ticketId, gson.toJson(payload))
                    .subscribe(() -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> etMensaje.setText(""));
                        }
                    }, throwable -> Log.e(TAG, "Error al enviar mensaje a ticket", throwable)));
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
                peticion.id = mensaje.getId();
                peticion.usuarioId = usuarioId;
                peticion.ticketId = ticketId;

                compositeDisposable.add(mStompClient.send("/app/chat.ticket." + ticketId + ".borrar", gson.toJson(peticion)).subscribe(() -> {
                    Log.d(TAG, "Borrado enviado para mensaje ticket: " + mensaje.getId());
                }, throwable -> Log.e(TAG, "Error enviando borrado ticket", throwable)));
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
                peticion.id = mensaje.getId();
                peticion.contenido = nuevoTexto;
                peticion.usuarioId = usuarioId;
                peticion.ticketId = ticketId;

                compositeDisposable.add(mStompClient.send("/app/chat.ticket." + ticketId + ".editar", gson.toJson(peticion)).subscribe(() -> {
                    Log.d(TAG, "Edición enviada para mensaje ticket: " + mensaje.getId());
                }, throwable -> Log.e(TAG, "Error enviando edición ticket", throwable)));
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

    private static class PeticionBorrado {
        String id;
        Long usuarioId;
        Long ticketId;
    }

    private static class PeticionEdicion {
        String id;
        String contenido;
        Long usuarioId;
        Long ticketId;
    }
}