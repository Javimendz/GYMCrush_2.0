package com.example.skynet.ui.notificaciones;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.NotificacionResponseDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificacionesFragment extends Fragment {

    private RecyclerView rv;
    private View layoutEmpty;
    private NotificacionesAdapter adapter;
    private List<NotificacionResponseDto> listaNotificaciones = new ArrayList<>();
    private long usuarioId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        usuarioId = prefs.getLong("user_id", -1);

        initViews(view);
        cargarNotificaciones();

        // Ocultar el badge de la campana al entrar
        if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
            ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).ocultarBadgeNotificacion();
        }

        return view;
    }

    private void initViews(View view) {
        View btnVolver = view.findViewById(R.id.btnVolverNotificaciones);
        btnVolver.setOnClickListener(v -> {
            if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).mostrarHome();
            } else if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        rv = view.findViewById(R.id.rvNotificaciones);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificacionesAdapter(listaNotificaciones, this::marcarYBorrar);
        rv.setAdapter(adapter);
    }

    private void cargarNotificaciones() {
        if (usuarioId == -1) return;

        RetrofitClient.getApiService().listarNotificaciones(usuarioId).enqueue(new Callback<List<NotificacionResponseDto>>() {
            @Override
            public void onResponse(Call<List<NotificacionResponseDto>> call, Response<List<NotificacionResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaNotificaciones.clear();

                    for (NotificacionResponseDto notif : response.body()) {
                        if (notif.getTitulo() == null || notif.getTitulo().trim().isEmpty()) {
                            notif.setTitulo("Reserva Exitosa");
                        }
                        if (notif.getMensaje() == null || notif.getMensaje().trim().isEmpty()) {
                            notif.setMensaje("¡Plaza confirmada! Revisa tu agenda para más detalles.");
                        }
                        listaNotificaciones.add(notif);
                    }

                    if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                        List<NotificacionResponseDto> locales = ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).getNotificacionesSesion();
                        for (NotificacionResponseDto localNotif : locales) {
                            boolean existe = false;
                            for (NotificacionResponseDto n : listaNotificaciones) {
                                // Comparación simple por mensaje y fecha para evitar duplicados
                                if (n.getMensaje().equals(localNotif.getMensaje()) && n.getFecha().equals(localNotif.getFecha())) {
                                    existe = true;
                                    break;
                                }
                            }
                            if (!existe) {
                                listaNotificaciones.add(0, localNotif);
                            }
                        }
                    }

                    actualizarUI();
                }
            }

            @Override
            public void onFailure(Call<List<NotificacionResponseDto>> call, Throwable t) {
                Log.e("NOTIF_ERROR", "Error al cargar: " + t.getMessage());
            }
        });
    }
    private void marcarYBorrar(NotificacionResponseDto notif) {
        // 1. Eliminar de la lista visual inmediatamente (UI optimista)
        int position = listaNotificaciones.indexOf(notif);
        if (position != -1) {
            listaNotificaciones.remove(position);
            adapter.notifyItemRemoved(position);
            actualizarUI();
        }

        // 2. Eliminar de la lista de sesión de DesarrolloActivity si existe (para notificaciones locales)
        if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
            ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).getNotificacionesSesion().remove(notif);
        }

        // 3. Procesar en el servidor si tiene un ID válido (para notificaciones persistidas)
        if (notif.getId() != null) {
            // Primero marcamos como leída (opcional según lógica de negocio, pero solicitado)
            RetrofitClient.getApiService().marcarNotificacionComoLeida(notif.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    // Después eliminamos definitivamente
                    RetrofitClient.getApiService().eliminarNotificacion(notif.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("NOTIF", "Notificación eliminada en servidor");
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("NOTIF", "Error al borrar en servidor");
                        }
                    });
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("NOTIF", "Error al marcar como leída");
                }
            });
        }
    }

    public void onNuevaNotificacionRecibida(NotificacionResponseDto notif) {
        if (listaNotificaciones != null && adapter != null) {
            // Validación extra para reservas
            if (notif.getMensaje() == null || notif.getMensaje().isEmpty()) {
                notif.setMensaje("Tu reserva se ha realizado correctamente."); // Mensaje por defecto
            }
            if (notif.getTitulo() == null) notif.setTitulo("Nueva Reserva");

            getActivity().runOnUiThread(() -> {
                listaNotificaciones.add(0, notif);
                adapter.notifyItemInserted(0); // Notifica la inserción en la primera posición
                rv.scrollToPosition(0);        // Desplaza la lista al inicio
                actualizarUI();
            });
        }
    }

    private void actualizarUI() {
        if (listaNotificaciones.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private interface OnNotificacionClickListener {
        void onClick(NotificacionResponseDto notif);
    }

    private static class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.ViewHolder> {
        private final List<NotificacionResponseDto> items;
        private final OnNotificacionClickListener listener;

        NotificacionesAdapter(List<NotificacionResponseDto> items, OnNotificacionClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NotificacionResponseDto n = items.get(position);
            holder.tvTitulo.setText(n.getTitulo());
            holder.tvMensaje.setText(n.getMensaje());
            holder.tvFecha.setText(n.getFecha());

            // Estilo según si está leída o no
            if (n.isLeido()) {
                holder.itemView.setAlpha(0.6f);
                holder.tvTitulo.setTextColor(holder.itemView.getContext().getColor(android.R.color.darker_gray));
            } else {
                holder.itemView.setAlpha(1.0f);
                holder.tvTitulo.setTextColor(holder.itemView.getContext().getColor(R.color.white));
            }

            holder.itemView.setOnClickListener(v -> listener.onClick(n));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitulo, tvMensaje, tvFecha;
            ViewHolder(View v) {
                super(v);
                tvTitulo = v.findViewById(R.id.tvTituloNotificacion);
                tvMensaje = v.findViewById(R.id.tvMensajeNotificacion);
                tvFecha = v.findViewById(R.id.tvFechaNotificacion);
            }
        }
    }
}
