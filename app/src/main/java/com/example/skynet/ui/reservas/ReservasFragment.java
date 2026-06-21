package com.example.skynet.ui.reservas;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.model.Clase;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.HorarioResponseDto;
import com.example.skynet.data.remote.dto.PerfilResponseDto;
import com.example.skynet.data.remote.dto.ReservaResponseDto;
import com.example.skynet.data.repository.ReservasRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservasFragment extends Fragment implements ReservasAdapter.OnReservaClickListener {

    private RecyclerView rvReservas;
    private ReservasAdapter adapter;
    private LinearLayout layoutWeeklyDays;
    private String diaSeleccionadoCodigo = "LUNES"; // Por defecto hoy o Lunes

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservas, container, false);

        rvReservas = view.findViewById(R.id.rvReservas);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutWeeklyDays = view.findViewById(R.id.layoutWeeklyDays);

        View btnVolver = view.findViewById(R.id.btnVolverReservas);
        btnVolver.setOnClickListener(v -> {
            if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).mostrarHome();
            } else if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        // En lugar de cargar hoy directamente, buscamos el primer día con actividad
        determinarDiaInicialYRefrescar(inflater);

        checkRoleAndSetupUI(view);

        return view;
    }

    private void checkRoleAndSetupUI(View view) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        if (isAdmin) {
            view.findViewById(R.id.tvTituloCitas).setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.tvTituloCitas)).setText("Gestión de Reservas Global");
        }
    }

    private void abrirDialogoNuevaReserva() {
        // Al pulsar "+", mostramos los horarios disponibles para el día seleccionado
        // para que el usuario pueda elegir uno y reservar.
        
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_seleccionar_clase, null);
        RecyclerView rvClasesDisponibles = dialogView.findViewById(R.id.rvClasesDisponibles);
        rvClasesDisponibles.setLayoutManager(new LinearLayoutManager(getContext()));
        
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Reservar en " + diaSeleccionadoCodigo)
                .setView(dialogView)
                .setNegativeButton("Cerrar", null)
                .create();

        // Cargar horarios del día
        ReservasRepository.getInstance().getHorariosPorDia(diaSeleccionadoCodigo, new ReservasRepository.RepositoryCallback<List<HorarioResponseDto>>() {
            @Override
            public void onSuccess(List<HorarioResponseDto> result) {
                List<Clase> clases = new ArrayList<>();
                for (HorarioResponseDto h : result) {
                    // Usa directamente el campo que calculamos en el backend
                    int disponibles = h.getPlazasLibres();
                    int aforo = (h.getAforoMax() != null ? h.getAforoMax() : 0);

                    clases.add(new Clase(
                            h.getId(),
                            h.getNombreActividad(),
                            h.getHoraInicio() + " - " + h.getHoraFin(),
                            h.getNombreSala(),
                            h.getNombreEntrenador(),
                            disponibles,
                            aforo,
                            h.getDiaSemana()
                    ));
                }
                com.example.skynet.ui.clases.ClasesAdapter clasesAdapter = new com.example.skynet.ui.clases.ClasesAdapter(clases, new com.example.skynet.ui.clases.ClasesAdapter.OnClaseClickListener() {
                    @Override
                    public void onApuntarseClick(Clase clase) {
                        confirmarReserva(clase, dialog);
                    }

                    @Override
                    public void onEliminarClick(Clase clase) {
                        // El admin podría eliminar desde aquí también si quisiéramos
                    }
                });
                rvClasesDisponibles.setAdapter(clasesAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error al cargar horarios: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void confirmarReserva(Clase clase, android.app.AlertDialog parentDialog) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        ReservasRepository.getInstance().crearReserva(userId, clase.getId(), new ReservasRepository.RepositoryCallback<ReservaResponseDto>() {
            @Override
            public void onSuccess(ReservaResponseDto result) {
                Toast.makeText(getContext(), "Reserva confirmada!", Toast.LENGTH_SHORT).show();
                
                if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                    ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).crearNotificacionManual(
                            "Reserva Realizada",
                            "Has reservado plaza en " + clase.getNombre() + " (" + clase.getHora() + ")"
                    );
                }

                parentDialog.dismiss();
                actualizarListaPorDia(diaSeleccionadoCodigo);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getDiaDeHoy() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY: return "LUNES";
            case Calendar.TUESDAY: return "MARTES";
            case Calendar.WEDNESDAY: return "MIERCOLES";
            case Calendar.THURSDAY: return "JUEVES";
            case Calendar.FRIDAY: return "VIERNES";
            case Calendar.SATURDAY: return "SABADO";
            case Calendar.SUNDAY: return "DOMINGO";
            default: return "LUNES";
        }
    }

    private void configurarCalendarioSemanal(LayoutInflater inflater) {
        layoutWeeklyDays.removeAllViews();
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Empezamos en Lunes

        String[] nombresDiasCortos = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"};
        String[] codigosDias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};

        for (int i = 0; i < 7; i++) {
            View diaView = inflater.inflate(R.layout.item_calendario_dia, layoutWeeklyDays, false);
            TextView tvNombre = diaView.findViewById(R.id.tvNombreDia);
            TextView tvNumero = diaView.findViewById(R.id.tvNumeroDia);
            
            tvNombre.setText(nombresDiasCortos[i]);
            tvNumero.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

            final String codigo = codigosDias[i];
            final View container = diaView.findViewById(R.id.layoutDiaContainer);

            // Estilo inicial
            if (codigo.equals(diaSeleccionadoCodigo)) {
                resaltarDia(tvNumero);
            }

            diaView.setOnClickListener(v -> {
                // Limpiar otros resaltados
                for (int j = 0; j < layoutWeeklyDays.getChildCount(); j++) {
                    View vChild = layoutWeeklyDays.getChildAt(j);
                    TextView tvNum = vChild.findViewById(R.id.tvNumeroDia);
                    tvNum.setBackgroundResource(android.R.color.transparent);
                    tvNum.setTextColor(Color.parseColor("#333333"));
                }
                
                resaltarDia(tvNumero);
                diaSeleccionadoCodigo = codigo;
                actualizarListaPorDia(codigo);
            });

            layoutWeeklyDays.addView(diaView);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void resaltarDia(TextView tvNumero) {
        tvNumero.setBackgroundResource(R.drawable.dot_active); // Reutilizamos tu círculo neón
        tvNumero.setTextColor(Color.WHITE);
    }

    private void actualizarListaPorDia(String codigoDia) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        if (userId == -1) return;

        // Convertimos a mayúsculas para asegurar compatibilidad con el backend
        final String diaBusqueda = codigoDia.toUpperCase();

        if (isAdmin) {
            ReservasRepository.getInstance().getTodasLasReservasPorDia(diaBusqueda, new ReservasRepository.RepositoryCallback<List<ReservaResponseDto>>() {
                @Override
                public void onSuccess(List<ReservaResponseDto> result) {
                    // Log de depuración
                    if (result != null) {
                        for (ReservaResponseDto r : result) {
                            android.util.Log.d("DEBUG_RESERVA", "ID: " + r.getId() + " | UsuarioID: " + r.getUsuarioId());
                        }
                    }
                    actualizarAdapter(result, true);
                }

                @Override
                public void onError(String errorMessage) {
                    // Si el error es un 404 o 500, notificamos que falta el endpoint
                    String msg = errorMessage.contains("404") ? "Endpoint no encontrado en backend" : errorMessage;
                    Toast.makeText(getContext(), "Error Admin: " + msg, Toast.LENGTH_SHORT).show();
                    actualizarAdapter(new ArrayList<>(), true);
                }
            });
        } else {
            ReservasRepository.getInstance().getMisReservas(userId, new ReservasRepository.RepositoryCallback<List<ReservaResponseDto>>() {
                @Override
                public void onSuccess(List<ReservaResponseDto> result) {
                    List<ReservaResponseDto> filtradas = new ArrayList<>();
                    for (ReservaResponseDto r : result) {
                        if (r.getDiaSemana() != null && r.getDiaSemana().equalsIgnoreCase(diaBusqueda)) {
                            filtradas.add(r);
                        }
                    }
                    actualizarAdapter(filtradas, false);
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void actualizarAdapter(List<ReservaResponseDto> lista, boolean isAdmin) {
        if (adapter == null) {
            adapter = new ReservasAdapter(lista, ReservasFragment.this);
            adapter.setAdminMode(isAdmin);
            rvReservas.setAdapter(adapter);
        } else {
            adapter.setAdminMode(isAdmin);
            adapter.setReservas(lista);
        }
    }

    @Override
    public void onCancelarClick(ReservaResponseDto reserva) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        if (isAdmin) {
            mostrarOpcionesGestionAdmin(reserva);
        } else {
            confirmarCancelacionUsuario(reserva);
        }
    }

    private void mostrarOpcionesGestionAdmin(ReservaResponseDto reserva) {
        String[] opciones = {"Confirmar Asistencia", "Cancelar Reserva (Admin)", "Ver Detalles Usuario"};
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Gestión de Reserva: " + reserva.getId())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        confirmarAsistencia(reserva);
                    } else if (which == 1) {
                        confirmarCancelacionUsuario(reserva);
                    } else {
                        obtenerYMostrarDetallesUsuario(reserva.getUsuarioId());
                    }
                })
                .show();
    }

    private void obtenerYMostrarDetallesUsuario(Long usuarioId) {
        if (usuarioId == null) {
            Toast.makeText(getContext(), "ID de usuario no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getApiService().getPerfilByUsuarioId(usuarioId).enqueue(new Callback<ApiResponseDto<PerfilResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PerfilResponseDto>> call, Response<ApiResponseDto<PerfilResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    PerfilResponseDto perfil = response.body().getDatos();
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("👤 Usuario: ").append(perfil.getUsername()).append("\n");
                    sb.append("📛 Nombre: ").append(perfil.getNombreCompleto()).append("\n");
                    if (perfil.getDni() != null && !perfil.getDni().isEmpty()) 
                        sb.append("🆔 DNI: ").append(perfil.getDni()).append("\n");
                    if (perfil.getCorreo() != null && !perfil.getCorreo().isEmpty())
                        sb.append("📧 Correo: ").append(perfil.getCorreo()).append("\n");
                    if (perfil.getTelefono() != null && !perfil.getTelefono().isEmpty())
                        sb.append("📞 Teléfono: ").append(perfil.getTelefono());

                    new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Detalles del Cliente")
                            .setMessage(sb.toString())
                            .setPositiveButton("Cerrar", null)
                            .show();
                } else {
                    Toast.makeText(getContext(), "No se pudieron obtener los detalles del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PerfilResponseDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void determinarDiaInicialYRefrescar(LayoutInflater inflater) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        String hoy = getDiaDeHoy();
        String[] codigosDias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};

        if (isAdmin) {
            buscarPrimerDiaAdmin(inflater, hoy, codigosDias, 0);
        } else {
            ReservasRepository.getInstance().getMisReservas(userId, new ReservasRepository.RepositoryCallback<List<ReservaResponseDto>>() {
                @Override
                public void onSuccess(List<ReservaResponseDto> result) {
                    String diaEncontrado = encontrarPrimerDiaConReserva(result, hoy, codigosDias);
                    diaSeleccionadoCodigo = (diaEncontrado != null) ? diaEncontrado : hoy;
                    configurarCalendarioSemanal(inflater);
                    actualizarListaPorDia(diaSeleccionadoCodigo);
                }
                @Override
                public void onError(String errorMessage) {
                    diaSeleccionadoCodigo = hoy;
                    configurarCalendarioSemanal(inflater);
                    actualizarListaPorDia(diaSeleccionadoCodigo);
                }
            });
        }
    }

    private void buscarPrimerDiaAdmin(LayoutInflater inflater, String hoy, String[] todos, int offset) {
        if (offset >= 7) {
            diaSeleccionadoCodigo = hoy;
            configurarCalendarioSemanal(inflater);
            actualizarListaPorDia(diaSeleccionadoCodigo);
            return;
        }

        int indexHoy = 0;
        for(int i=0; i<7; i++) if(todos[i].equals(hoy)) indexHoy = i;
        int targetIndex = (indexHoy + offset) % 7;
        String diaABuscar = todos[targetIndex];

        ReservasRepository.getInstance().getTodasLasReservasPorDia(diaABuscar, new ReservasRepository.RepositoryCallback<List<ReservaResponseDto>>() {
            @Override
            public void onSuccess(List<ReservaResponseDto> result) {
                if (result != null && !result.isEmpty()) {
                    diaSeleccionadoCodigo = diaABuscar;
                    configurarCalendarioSemanal(inflater);
                    actualizarListaPorDia(diaSeleccionadoCodigo);
                } else {
                    buscarPrimerDiaAdmin(inflater, hoy, todos, offset + 1);
                }
            }
            @Override
            public void onError(String errorMessage) {
                buscarPrimerDiaAdmin(inflater, hoy, todos, offset + 1);
            }
        });
    }

    private String encontrarPrimerDiaConReserva(List<ReservaResponseDto> reservas, String hoy, String[] todos) {
        int indexHoy = 0;
        for(int i=0; i<7; i++) if(todos[i].equals(hoy)) indexHoy = i;
        for (int i = 0; i < 7; i++) {
            int targetIndex = (indexHoy + i) % 7;
            String dia = todos[targetIndex];
            for (ReservaResponseDto r : reservas) {
                if (r.getDiaSemana() != null && r.getDiaSemana().equalsIgnoreCase(dia)) return dia;
            }
        }
        return null;
    }

    private void confirmarAsistencia(ReservaResponseDto reserva) {
        RetrofitClient.getApiService().confirmarAsistencia(reserva.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Asistencia confirmada", Toast.LENGTH_SHORT).show();
                    actualizarListaPorDia(diaSeleccionadoCodigo);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            com.google.gson.JsonObject jsonObject = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                            String mensaje = jsonObject.has("mensaje") ? jsonObject.get("mensaje").getAsString() : "Error al confirmar asistencia";
                            Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarCancelacionUsuario(ReservaResponseDto reserva) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancelar Reserva")
                .setMessage("¿Estás seguro de que deseas cancelar esta reserva?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    ReservasRepository.getInstance().cancelarReserva(reserva.getId(), new ReservasRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(getContext(), "Reserva cancelada con éxito", Toast.LENGTH_SHORT).show();
                            actualizarListaPorDia(diaSeleccionadoCodigo);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(getContext(), "Error al cancelar: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }
}