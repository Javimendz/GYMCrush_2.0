package com.example.skynet.data.remote;

import android.util.Log;
import com.example.skynet.data.remote.dto.NotificacionResponseDto;
import com.google.gson.Gson;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StompManager {
    private static final String TAG = "STOMP_MANAGER";
    private StompClient mStompClient;
    private Disposable mRestDisposable;
    private final Gson gson = new Gson();

    public interface OnNotificationReceivedListener {
        void onNotification(NotificacionResponseDto notification);
    }

    public void connect(String url, Long usuarioId, String token, OnNotificationReceivedListener listener) {
        // Configuración de OkHttpClient para mejorar la estabilidad de la conexión
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS) // Para WebSockets el timeout de lectura debe ser 0
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        // Se pueden añadir headers al handshake si el servidor lo requiere (ej: Authorization)
        Map<String, String> handshakeHeaders = new HashMap<>();
        if (token != null && !token.isEmpty()) {
            handshakeHeaders.put("Authorization", "Bearer " + token);
        }

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url, handshakeHeaders, httpClient);
        
        // Configurar Heartbeats para mantener la conexión viva (cada 10 segundos)
        mStompClient.withServerHeartbeat(10000).withClientHeartbeat(10000);

        List<StompHeader> headers = new ArrayList<>();
        if (token != null && !token.isEmpty()) {
            headers.add(new StompHeader("Authorization", "Bearer " + token));
        }

        mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d(TAG, "STOMP connection opened");
                    subscribeToNotifications(usuarioId, listener);
                    break;
                case ERROR:
                    Log.e(TAG, "STOMP connection error", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d(TAG, "STOMP connection closed");
                    break;
            }
        }, throwable -> Log.e(TAG, "Lifecycle error", throwable));

        mStompClient.connect(headers);
    }

    private void subscribeToNotifications(Long usuarioId, OnNotificationReceivedListener listener) {
        mRestDisposable = mStompClient.topic("/topic/notificaciones/" + usuarioId)
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Mensaje recibido: " + topicMessage.getPayload());
                    NotificacionResponseDto notification = gson.fromJson(topicMessage.getPayload(), NotificacionResponseDto.class);
                    if (listener != null) {
                        listener.onNotification(notification);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error en suscripción", throwable);
                });
    }

    public void disconnect() {
        if (mRestDisposable != null) mRestDisposable.dispose();
        if (mStompClient != null) mStompClient.disconnect();
    }
}
