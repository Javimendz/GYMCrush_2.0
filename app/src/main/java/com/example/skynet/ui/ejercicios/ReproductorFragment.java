package com.example.skynet.ui.ejercicios;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;

import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.VisualizacionRequestDto;
import com.example.skynet.data.remote.dto.VisualizacionResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReproductorFragment extends Fragment {

    private PlayerView playerView;
    private ExoPlayer player;
    private String videoUrl, titulo, descripcion, subtitulo, etiquetaExtra, parentRoutineName;
    private Long tutorialId, userId;
    private Integer duracionMin;

    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private Runnable progressRunnable;
    private long currentPosition = 0;

    // VARIABLES DE CONTROL
    private boolean puedeGuardarProgreso = false;
    private long progresoInicialMs = 0;
    private int ultimoProgresoGuardado = -1; // Filtro Anti-Spam para la BD

    @UnstableApi
    public static ReproductorFragment newInstance(Long tutorialId, String url, String titulo, String descripcion, String subtitulo, Integer duracionMin, String etiquetaExtra, String parentRoutineName) {
        ReproductorFragment fragment = new ReproductorFragment();
        Bundle args = new Bundle();
        args.putLong("tutorialId", tutorialId != null ? tutorialId : -1L);
        args.putString("url", url);
        args.putString("titulo", titulo);
        args.putString("descripcion", descripcion);
        args.putString("subtitulo", subtitulo);
        if (duracionMin != null) args.putInt("duracionMin", duracionMin);
        args.putString("etiquetaExtra", etiquetaExtra);
        args.putString("parentRoutineName", parentRoutineName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tutorialId = getArguments().getLong("tutorialId");
            videoUrl = getArguments().getString("url");
            titulo = getArguments().getString("titulo");
            descripcion = getArguments().getString("descripcion");
            subtitulo = getArguments().getString("subtitulo");
            duracionMin = getArguments().containsKey("duracionMin") ? getArguments().getInt("duracionMin") : null;
            etiquetaExtra = getArguments().getString("etiquetaExtra");
            parentRoutineName = getArguments().getString("parentRoutineName");
        }
        SharedPreferences prefs = requireActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1L);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reproductor, container, false);
        playerView = view.findViewById(R.id.player_view);

        TextView tvTitulo = view.findViewById(R.id.tvTituloVideo);
        TextView tvSubtitulo = view.findViewById(R.id.tvSubtituloVideo);
        TextView tvDuracion = view.findViewById(R.id.tvDuracionTag);
        TextView tvIntensidad = view.findViewById(R.id.tvIntensidadTag);
        TextView tvDescripcion = view.findViewById(R.id.tvDescripcionVideo);

        tvTitulo.setText(titulo != null ? titulo.toUpperCase() : "TUTORIAL");

        if (subtitulo != null && !subtitulo.isEmpty()) {
            tvSubtitulo.setText(subtitulo);
            tvSubtitulo.setVisibility(View.VISIBLE);
        } else {
            tvSubtitulo.setVisibility(View.GONE);
        }

        if (duracionMin != null && duracionMin > 0) {
            tvDuracion.setText(duracionMin + " min");
            tvDuracion.setVisibility(View.VISIBLE);
        } else {
            tvDuracion.setVisibility(View.GONE);
        }

        if (etiquetaExtra != null && !etiquetaExtra.isEmpty()) {
            tvIntensidad.setText(etiquetaExtra);
            tvIntensidad.setVisibility(View.VISIBLE);
        } else {
            tvIntensidad.setVisibility(View.GONE);
        }

        if (descripcion != null && !descripcion.isEmpty()) {
            tvDescripcion.setText(descripcion);
        } else {
            tvDescripcion.setText("Sin descripción disponible.");
        }

        // PASO 1: Pedimos los datos al servidor.
        recuperarProgresoDeServidor();

        view.findViewById(R.id.btnCerrarReproductor).setOnClickListener(v -> {
            if (getActivity() instanceof com.example.skynet.ui.rutinas.DetalleRutinaActivity) {
                getActivity().finish();
            } else {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void recuperarProgresoDeServidor() {
        if (userId == -1L || tutorialId == null || tutorialId == -1L) {
            iniciarReproductorSeguro();
            return;
        }

        RetrofitClient.getApiService().obtenerProgreso(userId, tutorialId).enqueue(new Callback<ApiResponseDto<VisualizacionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<VisualizacionResponseDto>> call, Response<ApiResponseDto<VisualizacionResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    VisualizacionResponseDto datos = response.body().getDatos();
                    
                    // Actualizamos el ojo con las visualizaciones al cargar el video
                    actualizarContadorUI(datos);

                    Integer segundosApi = (datos.getProgresoSegundos() != null) ? datos.getProgresoSegundos() : 0;
                    Boolean completado = datos.getCompletado();

                    // 1. LEER LA CACHÉ LOCAL
                    SharedPreferences prefs = requireContext().getSharedPreferences("VideoProgress", Context.MODE_PRIVATE);
                    int segundosLocales = prefs.getInt("progreso_" + tutorialId, 0);

                    // 2. USAR EL MAYOR VALOR (Nos protege del lag del servidor)
                    int segundosFinales = Math.max(segundosApi, segundosLocales);

                    if (completado != null && completado) {
                        progresoInicialMs = 0;
                        ultimoProgresoGuardado = 0;
                        prefs.edit().putInt("progreso_" + tutorialId, 0).apply(); // Limpiar caché si completó
                    } else if (segundosFinales > 0) {
                        progresoInicialMs = segundosFinales * 1000L;
                        ultimoProgresoGuardado = segundosFinales;
                        Log.d("ProgresoDetalle", "API: " + segundosApi + "s | Local: " + segundosLocales + "s | Usando: " + segundosFinales + "s");
                    }
                }

                puedeGuardarProgreso = true;

                // PASO 2: Ahora que tenemos la respuesta, CREAMOS el reproductor.
                if (player == null) iniciarReproductorSeguro();
            }

            @Override
            public void onFailure(Call<ApiResponseDto<VisualizacionResponseDto>> call, Throwable t) {
                Log.e("VideoProgreso", "Error API: " + t.getMessage());
                puedeGuardarProgreso = true;
                if (player == null) iniciarReproductorSeguro();
            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void iniciarReproductorSeguro() {
        if (!isAdded() || getContext() == null) return;

        String userAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36";
        DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(userAgent)
                .setAllowCrossProtocolRedirects(true);
        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(requireContext())
                .setDataSourceFactory(httpDataSourceFactory);

        // CONFIGURACIÓN PARA EVITAR ERRORES DE CODEC EN EMULADORES Y DISPOSITIVOS ESPECÍFICOS
        // 1. Habilitamos el fallback de decodificadores (si falla el de hardware, intenta el de software)
        // 2. Desactivamos el procesamiento asíncrono del MediaCodec que a veces falla con "start failed"
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(requireContext())
                .setEnableDecoderFallback(true)
                .forceDisableMediaCodecAsynchronousQueueing();

        player = new ExoPlayer.Builder(requireContext(), renderersFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .build();
        playerView.setPlayer(player);

        if (videoUrl != null && !videoUrl.isEmpty() && !videoUrl.equals("null")) {
            player.setMediaItem(MediaItem.fromUri(videoUrl));
            player.setSeekParameters(androidx.media3.exoplayer.SeekParameters.EXACT);
            player.setRepeatMode(Player.REPEAT_MODE_OFF);

            // 1. PREPARAMOS EL REPRODUCTOR
            player.prepare();
            player.setPlayWhenReady(true);
        }

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                // 2. EL SECRETO: Ejecutamos el salto SOLO cuando está STATE_READY
                if (playbackState == Player.STATE_READY && progresoInicialMs > 0) {
                    Log.d("VideoProgreso", "ExoPlayer listo. Saltando a: " + progresoInicialMs + " ms");
                    player.seekTo(progresoInicialMs);

                    // Reseteamos a 0 para evitar bucles de saltos
                    progresoInicialMs = 0;
                }
                else if (playbackState == Player.STATE_ENDED) {
                    Log.d("ProgresoDetalle", "Video Finalizado (STATE_ENDED). Marcando ejercicio.");
                    guardarProgreso(true);
                }
            }
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Log.e("VideoProgreso", "Error reproductor: " + error.getMessage());
            }
        });

        startTrackingProgress();

        }

    private void startTrackingProgress() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    guardarProgreso(false);
                }
                progressHandler.postDelayed(this, 10000);
            }
        };
        progressHandler.postDelayed(progressRunnable, 5000);
    }

    private void guardarProgreso(boolean completado) {
        // Marcado LOCAL - Debe funcionar SIEMPRE que tengamos los nombres
        if (completado && parentRoutineName != null && titulo != null) {
            Log.d("ProgresoDetalle", "Solicitando marcado local: Routine=" + parentRoutineName + ", Exercise=" + titulo);
            Context ctx = getContext();
            if (ctx != null) {
                com.example.skynet.ui.rutinas.RepositorioRutinas.setEjercicioCompletado(ctx, parentRoutineName, titulo, true);
                
                // Mostrar confirmación y cerrar reproductor para volver a la lista
                Toast.makeText(ctx, "¡Ejercicio completado!", Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (isAdded() && getActivity() != null) {
                        getActivity().finish();
                    }
                }, 1200);
            }
        }

        if (!puedeGuardarProgreso || userId == -1L || tutorialId == null || tutorialId == -1L) return;

        long pos = (player != null) ? player.getCurrentPosition() : currentPosition;
        int seconds = (int) (pos / 1000);

        if (seconds <= 0) {
            Log.w("ProgresoDetalle", "Intento de guardar 0 segundos bloqueado.");
            return;
        }

        // Filtro Anti-Spam y Anti-Retroceso:
        // No guardamos si el progreso es igual o menor al ya guardado (evita que loops o seeks accidentales sobrescriban)
        // a menos que sea una marca de "completado" enviada desde STATE_ENDED.
        if (seconds <= ultimoProgresoGuardado && !completado) return;

        if (completado && parentRoutineName != null) {
            com.example.skynet.ui.rutinas.RepositorioRutinas.setEjercicioCompletado(requireContext(), parentRoutineName, titulo, true);
        }

        ultimoProgresoGuardado = seconds;
        currentPosition = pos;

        SharedPreferences prefs = requireContext().getSharedPreferences("VideoProgress", Context.MODE_PRIVATE);
        prefs.edit().putInt("progreso_" + tutorialId, seconds).apply();
        Log.d("ProgresoDetalle", "Enviando POST: " + seconds + " segundos");
        VisualizacionRequestDto dto = new VisualizacionRequestDto(userId, tutorialId, seconds, completado);

        RetrofitClient.getApiService().guardarProgreso(dto).enqueue(new Callback<ApiResponseDto<VisualizacionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<VisualizacionResponseDto>> call, Response<ApiResponseDto<VisualizacionResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ProgresoDetalle", "Progreso guardado correctamente en BD");
                    actualizarContadorUI(response.body().getDatos());
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<VisualizacionResponseDto>> call, Throwable t) {
                Log.e("ProgresoDetalle", "Fallo al guardar: " + t.getMessage());
            }
        });
    }

    private void actualizarContadorUI(VisualizacionResponseDto datos) {
        if (getView() != null && datos != null && datos.getContadorReproducciones() != null) {
            TextView tvReproducciones = getView().findViewById(R.id.tvReproducciones);
            if (tvReproducciones != null) {
                tvReproducciones.setText(String.valueOf(datos.getContadorReproducciones()));
                tvReproducciones.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            guardarProgreso(false);
            player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (progressRunnable != null) progressHandler.removeCallbacks(progressRunnable);
        if (player != null) {
            player.release();
            player = null;
        }
    }
}