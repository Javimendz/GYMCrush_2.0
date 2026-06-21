package com.example.skynet.ui.rutinas;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.EntrenamientoRequestDto;
import com.example.skynet.data.remote.dto.EntrenamientoResponseDto;
import com.example.skynet.data.remote.dto.RutinaResponseDto;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import com.google.gson.Gson;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RutinasFragment extends Fragment {

    private RecyclerView rvRutinas, rvEjercicios, rvRutinasGuardadas;
    private androidx.viewpager2.widget.ViewPager2 vpRutinasFavoritas;
    private com.google.android.material.tabs.TabLayout dotsIndicator;
    private TextView tvLabelFavoritos;
    private RutinaApiAdapter rutinasAdapter;
    private EntrenamientoAdapter ejerciciosAdapter;
    private RutinaGuardadaAdapter rutinasGuardadasAdapter;
    private List<EntrenamientoResponseDto> listaEjerciciosCompleta = new ArrayList<>();

    private TabLayout tabLayoutPrincipal, tabLayoutCategorias;
    private NestedScrollView containerRutinas, containerEjercicios, containerRutinasGuardadas;
    private FrameLayout containerPlanes, containerBiblioteca;
    private LinearLayout layoutVacio, layoutSelectorFecha;
    private android.widget.EditText etBuscador;
    private LineChart graphEvolucion;
    private TextView tvStatTotal, tvStatMedia, tvStatEfectividad, tvFechaSeleccionada;
    private TextView tvRacha, tvEstadoRutinaDia, tvProgresoTexto;
    private android.widget.ImageView imgFondoCardResumen;
    private com.google.android.material.progressindicator.LinearProgressIndicator progressRutinaDetalle;
    private MaterialButton btnCambiarFecha;
    private com.google.android.material.floatingactionbutton.FloatingActionButton btnIrACrear;
    private long userId;
    private boolean isAdmin;
    private ExtendedFloatingActionButton fabAddEjercicio;
    private LocalDate fechaSeleccionada = LocalDate.now();

    private boolean isDataLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.rutina_fragment_lista, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        // Cargar asociaciones de ejercicios guardadas localmente
        RepositorioRutinas.cargarDatosDesdeLocal(requireContext());

        // Referencias
        rvRutinas = vista.findViewById(R.id.rvMisRutinas);
        rvEjercicios = vista.findViewById(R.id.rvEjerciciosRegistrados);
        rvRutinasGuardadas = vista.findViewById(R.id.rvRutinasGuardadas);
        vpRutinasFavoritas = vista.findViewById(R.id.vpRutinasFavoritas);
        dotsIndicator = vista.findViewById(R.id.dotsIndicator);
        tvLabelFavoritos = vista.findViewById(R.id.tvLabelFavoritos);

        btnIrACrear = vista.findViewById(R.id.btnIrACrear);

        tabLayoutPrincipal = vista.findViewById(R.id.tabLayoutRutinas);
        tabLayoutCategorias = vista.findViewById(R.id.tabLayoutCategorias);
        containerRutinas = vista.findViewById(R.id.containerRutinas);
        containerEjercicios = vista.findViewById(R.id.containerEntrenamientos);
        containerPlanes = vista.findViewById(R.id.containerPlanes);
        containerBiblioteca = vista.findViewById(R.id.containerBiblioteca);
        containerRutinasGuardadas = vista.findViewById(R.id.containerRutinasGuardadas);
        layoutVacio = vista.findViewById(R.id.layoutEjerciciosVacio);
        fabAddEjercicio = vista.findViewById(R.id.fabAddEjercicioAdmin);

        layoutSelectorFecha = vista.findViewById(R.id.layoutSelectorFecha);
        tvFechaSeleccionada = vista.findViewById(R.id.tvFechaSeleccionada);

        graphEvolucion = vista.findViewById(R.id.graphEvolucionRutinas);
        setupGraphEvolucion();
        tvStatTotal = vista.findViewById(R.id.tvStatTotal);
        tvStatMedia = vista.findViewById(R.id.tvStatMedia);
        tvStatEfectividad = vista.findViewById(R.id.tvStatEfectividad);
        tvRacha = vista.findViewById(R.id.tvRacha);
        etBuscador = vista.findViewById(R.id.etBuscarEjercicio);

        // Referencias Resumen Diario
        tvEstadoRutinaDia = vista.findViewById(R.id.tvEstadoRutinaDia);
        tvProgresoTexto = vista.findViewById(R.id.tvProgresoTexto);
        progressRutinaDetalle = vista.findViewById(R.id.progressRutinaDetalle);
        imgFondoCardResumen = vista.findViewById(R.id.imgFondoCardResumen);

        // Nuevo: Barra de progreso y Calendario Horizontal
        com.google.android.material.progressindicator.LinearProgressIndicator progressRutina = vista.findViewById(R.id.progressRutina);
        RecyclerView rvCalendario = vista.findViewById(R.id.rvCalendarioHorizontal);
        setupCalendarioHorizontal(rvCalendario);

        checkAdminRole();

        // 1. Configurar Rutinas de la Agenda (RecyclerView inferior)
        rvRutinas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRutinas.setNestedScrollingEnabled(false);
        rutinasAdapter = new RutinaApiAdapter(new ArrayList<>(), new RutinaApiAdapter.OnRutinaClickListener() {
            @Override
            public void onCompletar(RutinaResponseDto rutina) {
                marcarRutinaCompletada(rutina.getId());
            }

            @Override
            public void onEliminar(RutinaResponseDto rutina) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Quitar de la agenda")
                        .setMessage("¿Estás seguro de que quieres eliminar '" + rutina.getNombreEntrenamiento() + "' de tu agenda de hoy?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            eliminarRutinaDeAgenda(rutina.getId());
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }

            @Override
            public void onClick(RutinaResponseDto rutina) {
                // Buscamos el entrenamiento completo en el catálogo (para coger categoría, descripción, etc.)
                EntrenamientoResponseDto entrenamientoCompleto = listaEjerciciosCompleta.stream()
                        .filter(e -> e.getId().equals(rutina.getEntrenamientoId()))
                        .findFirst()
                        .orElse(null);

                String titulo = rutina.getNombreEntrenamiento();
                String urlVideo = rutina.getUrlVideo();
                String descripcion = "Sin descripción detallada.";
                Integer duracionMin = rutina.getDuracion();
                String intensidad = rutina.getIntensidad();

                if (entrenamientoCompleto != null) {
                    if (entrenamientoCompleto.getDescripcion() != null && !entrenamientoCompleto.getDescripcion().isEmpty()) {
                        descripcion = entrenamientoCompleto.getDescripcion();
                    }
                    if (entrenamientoCompleto.getUrlVideo() != null && !entrenamientoCompleto.getUrlVideo().isEmpty()) {
                        urlVideo = entrenamientoCompleto.getUrlVideo();
                    }
                }

                // --- ABRIMOS LA ACTIVIDAD DE DETALLE EN LUGAR DEL REPRODUCTOR ---
                android.content.Intent intent = new android.content.Intent(getContext(), DetalleRutinaActivity.class);
                intent.putExtra("RUTINA_ID", rutina.getId());
                intent.putExtra("TUTORIAL_ID", rutina.getEntrenamientoId());
                intent.putExtra("IS_FROM_AGENDA", true);
                intent.putExtra("NOMBRE_RUTINA", titulo);
                intent.putExtra("URL_VIDEO", urlVideo);
                intent.putExtra("DESCRIPCION", descripcion);
                intent.putExtra("INTENSIDAD", intensidad);
                intent.putExtra("DURACION", duracionMin);
                startActivity(intent);
            }
        });
        rvRutinas.setAdapter(rutinasAdapter);

        // 2. Configurar Carrusel de Rutinas (ViewPager2 superior)
        setupCarruselRutinas();

        // 3. Configurar Entrenamientos
        rvEjercicios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEjercicios.setNestedScrollingEnabled(false);
        ejerciciosAdapter = new EntrenamientoAdapter(new ArrayList<>());
        ejerciciosAdapter.setAdmin(isAdmin, userId);
        ejerciciosAdapter.setOnItemClickListener(new EntrenamientoAdapter.OnItemClickListener() {
            @Override
            public void onStartWorkout(EntrenamientoResponseDto entrenamiento) {
                // AQUÍ TAMBIÉN SE PUEDE ABRIR EL REPRODUCTOR SI QUIERES, PERO DE MOMENTO LO DEJAMOS
                Intent intent = new Intent(getActivity(), DetalleRutinaActivity.class);
                intent.putExtra("RUTINA_ID", entrenamiento.getId());
                intent.putExtra("TUTORIAL_ID", entrenamiento.getId());
                intent.putExtra("NOMBRE_RUTINA", entrenamiento.getNombre());
                intent.putExtra("URL_VIDEO", entrenamiento.getUrlVideo());
                intent.putExtra("DESCRIPCION", entrenamiento.getDescripcion());
                intent.putExtra("DURACION", entrenamiento.getDuracion());
                intent.putExtra("INTENSIDAD", entrenamiento.getIntensidad());
                startActivity(intent);
            }

            @Override
            public void onDeleteWorkout(EntrenamientoResponseDto entrenamiento) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Eliminar Entrenamiento")
                        .setMessage("¿Estás seguro de que quieres eliminar '" + entrenamiento.getNombre() + "' del catálogo?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            eliminarEntrenamientoMaestro(entrenamiento.getId());
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
        rvEjercicios.setAdapter(ejerciciosAdapter);

        // 4. Configurar Rutinas Guardadas (Pestaña "MIS RUTINAS")
        rvRutinasGuardadas.setLayoutManager(new LinearLayoutManager(getContext()));
        rutinasGuardadasAdapter = new RutinaGuardadaAdapter(RepositorioRutinas.getRutinas(), new RutinaGuardadaAdapter.OnRutinaGuardadaClickListener() {
            @Override
            public void onDelete(RepositorioRutinas.RutinaModel rutina) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Eliminar rutina")
                        .setMessage("¿Deseas eliminar definitivamente '" + rutina.nombre + "'?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            eliminarRutinaPersonalizada(rutina);
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
        rvRutinasGuardadas.setAdapter(rutinasGuardadasAdapter);

        setupBuscador();

        if (btnIrACrear != null) {
            btnIrACrear.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CrearRutinaActivity.class);
                startActivity(intent);
            });
        }

        setupTabs();
        setupDatePicker();

        if (fabAddEjercicio != null) {
            fabAddEjercicio.setOnClickListener(v -> mostrarDialogoNuevoEjercicio());
        }

        actualizarUIFecha();
        // No cargamos aquí, dejamos que onResume lo haga para evitar duplicidad en el arranque
        // cargarRutinaPorFecha();
        // cargarEjerciciosDesdeServidor();
        // cargarEstadisticas();

        return vista;
    }

    private void setupCarruselRutinas() {
        if (vpRutinasFavoritas == null || dotsIndicator == null) return;

        List<RepositorioRutinas.RutinaModel> rutinas = RepositorioRutinas.getRutinas();

        if (rutinas.isEmpty()) {
            vpRutinasFavoritas.setVisibility(View.GONE);
            dotsIndicator.setVisibility(View.GONE);
            if (tvLabelFavoritos != null) tvLabelFavoritos.setVisibility(View.GONE);
            return;
        }

        vpRutinasFavoritas.setVisibility(View.VISIBLE);
        dotsIndicator.setVisibility(View.VISIBLE);
        if (tvLabelFavoritos != null) tvLabelFavoritos.setVisibility(View.VISIBLE);

        if (vpRutinasFavoritas.getAdapter() == null) {
            RutinaGuardadaAdapter carruselAdapter = new RutinaGuardadaAdapter(rutinas, true);
            vpRutinasFavoritas.setAdapter(carruselAdapter);

            new com.google.android.material.tabs.TabLayoutMediator(dotsIndicator, vpRutinasFavoritas, (tab, position) -> {}).attach();
        } else {
            // Reciclamos el adaptador existente y actualizamos la lista para no romper la UI
            ((RutinaGuardadaAdapter) vpRutinasFavoritas.getAdapter()).updateList(rutinas);
        }
    }

    private void eliminarRutinaPersonalizada(RepositorioRutinas.RutinaModel rutina) {
        if (rutina.id != null) {
            RetrofitClient.getApiService().eliminarEntrenamiento(rutina.id).enqueue(new Callback<ApiResponseDto<Void>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                    if (response.isSuccessful()) {
                        RepositorioRutinas.eliminarRutina(requireContext(), rutina.nombre);
                        actualizarRutinasGuardadas();
                        Toast.makeText(getContext(), "Rutina eliminada", Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.code() == 400 || response.code() == 409) {
                            Toast.makeText(getContext(), "No se puede eliminar: Esta rutina está asignada a tu agenda o a un plan de entrenamiento.", Toast.LENGTH_LONG).show();
                        } else if (response.code() == 403) {
                            Toast.makeText(getContext(), "No tienes permisos para eliminar esta rutina.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Error al eliminar en el servidor (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Eliminar solo localmente si no tiene ID (rutinas hardcodeadas)
            RepositorioRutinas.eliminarRutina(requireContext(), rutina.nombre);
            rutinasGuardadasAdapter.updateList(RepositorioRutinas.getRutinas());
        }
    }

    private void eliminarRutinaDeAgenda(Long agendaId) {
        RetrofitClient.getApiService().eliminarRutina(agendaId).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ejercicio eliminado de la agenda", Toast.LENGTH_SHORT).show();
                    cargarRutinaPorFecha();
                    cargarEstadisticas();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar de la agenda (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatePicker() {
        if (btnCambiarFecha == null) return;
        btnCambiarFecha.setOnClickListener(v -> {
            android.app.DatePickerDialog datePicker = new android.app.DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        fechaSeleccionada = LocalDate.of(year, month + 1, dayOfMonth);
                        actualizarUIFecha();
                        cargarRutinaPorFecha();
                    },
                    fechaSeleccionada.getYear(),
                    fechaSeleccionada.getMonthValue() - 1,
                    fechaSeleccionada.getDayOfMonth());
            datePicker.show();
        });
    }

    private void actualizarUIFecha() {
        if (tvFechaSeleccionada != null) {
            String fechaStr = fechaSeleccionada.equals(LocalDate.now()) ? "Hoy" :
                    fechaSeleccionada.getDayOfMonth() + " " +
                            fechaSeleccionada.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("es", "ES"));
            tvFechaSeleccionada.setText(fechaStr);
        }
    }

    private void setupCalendarioHorizontal(RecyclerView rv) {
        List<LocalDate> dias = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        // Generar 14 días (semana pasada y semana que viene)
        for (int i = -7; i <= 7; i++) {
            dias.add(hoy.plusDays(i));
        }

        CalendarioAdapter adapter = new CalendarioAdapter(dias, fechaSeleccionada, date -> {
            fechaSeleccionada = date;
            actualizarUIFecha();
            cargarRutinaPorFecha();
            cargarEstadisticas();
        });

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
        // Hacer scroll hasta el día de hoy (posición 7)
        rv.scrollToPosition(7);
    }

    private void cargarRutinaPorFecha() {
        if (userId == -1) return;
        String fechaStr = fechaSeleccionada.toString();
        android.util.Log.d("FECHA_CHECK", "Cargando rutina para: " + fechaStr);
        RetrofitClient.getApiService().getRutinaDiaria(userId, fechaStr).enqueue(new Callback<ApiResponseDto<List<RutinaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<RutinaResponseDto>>> call, Response<ApiResponseDto<List<RutinaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RutinaResponseDto> datosRecibidos = response.body().getDatos();

                    // Log para depuración
                    android.util.Log.d("API_CHECK", "Ejercicios recibidos para " + fechaStr + ": " +
                            (datosRecibidos != null ? datosRecibidos.size() : 0));

                    // Actualizamos el adaptador con la lista completa
                    if (rutinasAdapter != null) {
                        rutinasAdapter.updateList(datosRecibidos);
                    }

                    // Actualizar Resumen Diario
                    actualizarResumenDiario(datosRecibidos);

                    // Si no hay datos, podrías mostrar un layout de "Agenda vacía"
                    rvRutinas.setVisibility(datosRecibidos == null || datosRecibidos.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<RutinaResponseDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarResumenDiario(List<RutinaResponseDto> lista) {
        TextView tvResumenTitulo = getView() != null ? getView().findViewById(R.id.tvResumenTitulo) : null;
        if (tvResumenTitulo != null) {
            if (fechaSeleccionada.equals(LocalDate.now())) {
                tvResumenTitulo.setText("RESUMEN DE HOY");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'DE' MMMM", new Locale("es", "ES"));
                tvResumenTitulo.setText("RESUMEN DEL " + fechaSeleccionada.format(formatter).toUpperCase());
            }
        }

        if (lista == null || lista.isEmpty()) {
            tvEstadoRutinaDia.setText("¡Día de descanso!");
            tvProgresoTexto.setText("Tómate un respiro hoy");
            progressRutinaDetalle.setProgress(0);
            if (imgFondoCardResumen != null) imgFondoCardResumen.setImageResource(R.drawable.cardio);

            // Resetear marcas de completado para el día sin actividad
            for (RepositorioRutinas.RutinaModel m : RepositorioRutinas.getRutinas()) {
                m.completada = false;
            }
            if (rutinasGuardadasAdapter != null) rutinasGuardadasAdapter.notifyDataSetChanged();
            if (vpRutinasFavoritas != null && vpRutinasFavoritas.getAdapter() != null) {
                vpRutinasFavoritas.getAdapter().notifyDataSetChanged();
            }
            return;
        }

        // Sincronizar estado de completado en "MIS RUTINAS" según la agenda del día seleccionado
        for (RepositorioRutinas.RutinaModel m : RepositorioRutinas.getRutinas()) {
            m.completada = false; // Reset inicial
        }
        for (RutinaResponseDto rDto : lista) {
            if (Boolean.TRUE.equals(rDto.getCompletado())) {
                RepositorioRutinas.marcarComoCompletada(getContext(), rDto.getNombreEntrenamiento());
            }
        }
        if (rutinasGuardadasAdapter != null) rutinasGuardadasAdapter.notifyDataSetChanged();
        if (vpRutinasFavoritas != null && vpRutinasFavoritas.getAdapter() != null) {
            vpRutinasFavoritas.getAdapter().notifyDataSetChanged();
        }

        long completadas = lista.stream().filter(r -> Boolean.TRUE.equals(r.getCompletado())).count();
        int porcentaje = (int) ((completadas * 100) / lista.size());

        progressRutinaDetalle.setProgress(porcentaje, true);
        tvProgresoTexto.setText(porcentaje + "% completado (" + completadas + "/" + lista.size() + ")");

        // Cargar imagen dinámica basada en el primer entrenamiento
        if (imgFondoCardResumen != null && !lista.isEmpty()) {
            String nombre = lista.get(0).getNombreEntrenamiento().toLowerCase();
            if (nombre.contains("spinning") || nombre.contains("bici") || nombre.contains("cycle")) {
                imgFondoCardResumen.setImageResource(R.drawable.spinning);
            } else if (nombre.contains("pesas") || nombre.contains("fuerza") || nombre.contains("musculacion")) {
                imgFondoCardResumen.setImageResource(R.drawable.musculacion);
            } else {
                imgFondoCardResumen.setImageResource(R.drawable.cardio);
            }
        }

        if (porcentaje == 100) {
            tvEstadoRutinaDia.setText("¡Objetivo cumplido! 🏆");
        } else if (porcentaje > 0) {
            tvEstadoRutinaDia.setText("¡Vas por buen camino!");
        } else {
            String nombre = lista.get(0).getNombreEntrenamiento();
            tvEstadoRutinaDia.setText("Próximo: " + nombre);
        }
    }

    private void cargarRutinaDiaria() {
        fechaSeleccionada = LocalDate.now();
        actualizarUIFecha();
        cargarRutinaPorFecha();
    }

    private void setupGraphEvolucion() {
        if (graphEvolucion == null) return;

        graphEvolucion.getDescription().setEnabled(false);
        graphEvolucion.setDrawGridBackground(false);
        graphEvolucion.setTouchEnabled(true);
        graphEvolucion.setDragEnabled(true);
        graphEvolucion.setScaleEnabled(true);
        graphEvolucion.setPinchZoom(true);
        graphEvolucion.getLegend().setEnabled(false);
        
        // Texto por defecto si no hay datos
        graphEvolucion.setNoDataText("No hay datos de evolución disponibles");
        graphEvolucion.setNoDataTextColor(Color.WHITE);

        XAxis xAxis = graphEvolucion.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGranularity(1f);

        YAxis leftAxis = graphEvolucion.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#1AFFFFFF"));
        leftAxis.setAxisMinimum(0f);

        graphEvolucion.getAxisRight().setEnabled(false);
    }

    private void cargarEstadisticas() {
        if (userId == -1) return;

        LocalDate finalVentana = fechaSeleccionada;
        LocalDate inicio = finalVentana.minusDays(6);

        RetrofitClient.getApiService().getRutinasPorRango(userId, inicio.toString(), finalVentana.toString()).enqueue(new Callback<List<RutinaResponseDto>>() {
            @Override
            public void onResponse(Call<List<RutinaResponseDto>> call, Response<List<RutinaResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RutinaResponseDto> todas = response.body();

                    Float[] puntos = new Float[7];
                    Integer[] totales = new Integer[7];
                    String[] etiquetas = new String[7];

                    for (int i = 0; i < 7; i++) {
                        LocalDate dia = inicio.plusDays(i);
                        String diaStr = dia.toString(); // YYYY-MM-DD
                        etiquetas[i] = dia.getDayOfMonth() + "/" + dia.getMonthValue();

                        List<RutinaResponseDto> delDia = todas.stream()
                                .filter(r -> r.getFechaAsignacion() != null && r.getFechaAsignacion().contains(diaStr))
                                .collect(Collectors.toList());

                        totales[i] = delDia.size();
                        puntos[i] = (float) delDia.stream()
                                .filter(r -> Boolean.TRUE.equals(r.getCompletado()))
                                .count();
                    }

                    actualizarGrafica(puntos, etiquetas);
                    calcularYMostrarRacha(puntos);
                    actualizarMiniStats(puntos, totales);
                } else {
                    android.util.Log.e("STATS", "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<RutinaResponseDto>> call, Throwable t) {
                android.util.Log.e("STATS", "Fallo al cargar estadísticas", t);
                if (isAdded()) {
                    actualizarGrafica(new Float[]{0f,0f,0f,0f,0f,0f,0f}, new String[]{"","","","","","",""});
                }
            }
        });
    }

    private void actualizarMiniStats(Float[] puntos, Integer[] totales) {
        float sumaCompletadas = 0;
        int sumaTotales = 0;
        int diasConEjercicios = 0;

        for (int i = 0; i < 7; i++) {
            sumaCompletadas += puntos[i];
            sumaTotales += totales[i];
            if (totales[i] > 0) diasConEjercicios++;
        }

        if (tvStatTotal != null) tvStatTotal.setText(String.format(java.util.Locale.getDefault(), "%.0f", sumaCompletadas));

        if (tvStatMedia != null) {
            float media = diasConEjercicios > 0 ? sumaCompletadas / diasConEjercicios : 0;
            tvStatMedia.setText(String.format(java.util.Locale.getDefault(), "%.1f", media));
        }

        if (tvStatEfectividad != null) {
            int efectividad = sumaTotales > 0 ? (int) ((sumaCompletadas * 100) / sumaTotales) : 0;
            tvStatEfectividad.setText(efectividad + "%");
        }
    }

    private void actualizarGrafica(Float[] puntos, String[] etiquetas) {
        if (graphEvolucion == null) return;

        ArrayList<Entry> entries = new ArrayList<>();
        boolean hasData = false;
        for (int i = 0; i < puntos.length; i++) {
            float val = (puntos[i] != null) ? puntos[i] : 0f;
            entries.add(new Entry(i, val));
            if (val > 0) hasData = true;
        }

        if (!hasData) {
            graphEvolucion.clear();
            graphEvolucion.setNoDataText("Sin datos de evolución esta semana");
            graphEvolucion.setNoDataTextColor(Color.parseColor("#94A3B8"));
            graphEvolucion.invalidate();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Ejercicios Completados");
        dataSet.setColor(Color.parseColor("#CD0277"));
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleColor(Color.parseColor("#CD0277"));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(8f);
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#4DCD0277"), Color.TRANSPARENT}
        ));
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        LineData lineData = new LineData(dataSet);
        graphEvolucion.setData(lineData);

        XAxis xAxis = graphEvolucion.getXAxis();
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(etiquetas));
        xAxis.setLabelCount(7);

        graphEvolucion.notifyDataSetChanged();
        graphEvolucion.invalidate();
    }

    private void calcularYMostrarRacha(Float[] puntos) {
        int racha = 0;
        boolean esFinalHoy = fechaSeleccionada.equals(LocalDate.now());

        // El índice 6 es el final de la ventana (fechaSeleccionada). Recorremos hacia atrás.
        for (int i = 6; i >= 0; i--) {
            if (puntos[i] > 0) {
                racha++;
            } else if (i == 6 && esFinalHoy) {
                // Si la ventana termina hoy y hoy es 0, la racha no se rompe aún (esperamos a que termine el día)
                continue;
            } else {
                // Se rompió la racha
                break;
            }
        }

        String labelDia = (racha == 1) ? getString(R.string.dia) : getString(R.string.dias);
        tvRacha.setText(getString(R.string.racha_dias, racha, labelDia));
    }

    private void marcarRutinaCompletada(Long rutinaId) {
        RetrofitClient.getApiService().completarRutina(rutinaId).enqueue(new Callback<ApiResponseDto<RutinaResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<RutinaResponseDto>> call, Response<ApiResponseDto<RutinaResponseDto>> response) {
                if (response.isSuccessful()) {
                    String mensaje = "Estado de rutina actualizado";
                    if (response.body() != null && response.body().getDatos() != null) {
                        boolean completado = response.body().getDatos().getCompletado();
                        mensaje = completado ? "¡Felicidades! Entrenamiento completado" : "Rutina marcada como pendiente";
                    }
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    cargarRutinaPorFecha();
                    cargarEstadisticas();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<RutinaResponseDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al actualizar estado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarEntrenamientoMaestro(Long id) {
        RetrofitClient.getApiService().eliminarEntrenamiento(id).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Entrenamiento eliminado", Toast.LENGTH_SHORT).show();
                    cargarEjerciciosDesdeServidor();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAdminRole() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        userId = prefs.getLong("user_id", -1);
        if (ejerciciosAdapter != null) ejerciciosAdapter.setAdmin(isAdmin, userId);
        actualizarVisibilidadFab(tabLayoutPrincipal.getSelectedTabPosition());
    }

    private void setupBuscador() {
        if (etBuscador == null) return;
        etBuscador.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPorNombre(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filtrarPorNombre(String query) {
        List<EntrenamientoResponseDto> filtrada = listaEjerciciosCompleta.stream()
                .filter(e -> e.getNombre().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        ejerciciosAdapter.updateLista(filtrada);
    }

    private void setupTabs() {
        tabLayoutPrincipal.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                actualizarVisibilidadFab(position);

                containerRutinas.setVisibility(View.GONE);
                containerPlanes.setVisibility(View.GONE);
                containerEjercicios.setVisibility(View.GONE);
                containerRutinasGuardadas.setVisibility(View.GONE);
                containerBiblioteca.setVisibility(View.GONE);
                layoutSelectorFecha.setVisibility(View.GONE);

                // Nuevo: Ocultar también el progreso y el calendario horizontal si no es Agenda
                View progress = getView().findViewById(R.id.progressRutina);
                View calHorizontal = getView().findViewById(R.id.rvCalendarioHorizontal);

                if (position == 0) { // MI AGENDA
                    containerRutinas.setVisibility(View.VISIBLE);
                    layoutSelectorFecha.setVisibility(View.VISIBLE);
                    if (progress != null) progress.setVisibility(View.VISIBLE);
                    if (calHorizontal != null) calHorizontal.setVisibility(View.VISIBLE);
                    setupCarruselRutinas(); // Verificar si mostrar u ocultar carrusel
                    cargarRutinaPorFecha();
                    cargarEstadisticas();
                } else if (position == 1) { // EXPLORAR
                    containerPlanes.setVisibility(View.VISIBLE);
                    mostrarFragmentPlanes();
                    if (progress != null) progress.setVisibility(View.GONE);
                    if (calHorizontal != null) calHorizontal.setVisibility(View.GONE);
                } else if (position == 2) { // MIS RUTINAS
                    containerRutinasGuardadas.setVisibility(View.VISIBLE);
                    actualizarRutinasGuardadas();
                    if (progress != null) progress.setVisibility(View.GONE);
                    if (calHorizontal != null) calHorizontal.setVisibility(View.GONE);
                } else if (position == 3) { // BIBLIOTECA
                    containerBiblioteca.setVisibility(View.VISIBLE);
                    mostrarFragmentBiblioteca();
                    if (progress != null) progress.setVisibility(View.GONE);
                    if (calHorizontal != null) calHorizontal.setVisibility(View.GONE);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        tabLayoutCategorias.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filtrarPorCategoria(tab.getText().toString());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void actualizarRutinasGuardadas() {
        if (userId == -1) return;

        RetrofitClient.getApiService().getEntrenamientosUsuario(userId).enqueue(new Callback<ApiResponseDto<List<EntrenamientoResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Response<ApiResponseDto<List<EntrenamientoResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EntrenamientoResponseDto> entrenamientos = response.body().getDatos();

                    // Sincronizar el repositorio central con los datos frescos del servidor
                    RepositorioRutinas.sincronizarConServidor(requireContext(), entrenamientos);

                    // Actualizar visibilidad del carrusel si estamos en la pestaña 0
                    if (tabLayoutPrincipal.getSelectedTabPosition() == 0) {
                        setupCarruselRutinas();
                    }

                    if (rutinasGuardadasAdapter != null) {
                        rutinasGuardadasAdapter.updateList(RepositorioRutinas.getRutinas());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Error al cargar tus rutinas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mostrarFragmentPlanes() {
        PlanesFragment fragment = new PlanesFragment();
        fragment.setOnPlanSubscriptionListener(() -> {
            // 1. Cambiamos a la pestaña de MI AGENDA
            if (tabLayoutPrincipal != null) {
                tabLayoutPrincipal.getTabAt(0).select();
            }

            fechaSeleccionada = LocalDate.now();
            actualizarUIFecha();

            // 2. Mostramos un aviso visual de que el plan se está cargando
            Toast.makeText(getContext(), "¡Plan Maestro Activado! Cargando tu agenda...", Toast.LENGTH_LONG).show();

            // 3. Forzamos la recarga de los datos del usuario para obtener el nuevo 'plan_activo_id'
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                if (isAdded()) {
                    // Recargamos la agenda para ver los nuevos ejercicios del plan
                    cargarRutinaPorFecha();
                    actualizarNombrePlanActivo();
                }
            }, 1000);
        });

        getChildFragmentManager().beginTransaction()
                .replace(R.id.containerPlanes, fragment)
                .commit();
    }

    private void mostrarFragmentBiblioteca() {
        com.example.skynet.ui.ejercicios.EjerciciosFragment fragment = new com.example.skynet.ui.ejercicios.EjerciciosFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.containerBiblioteca, fragment)
                .commit();
    }

    private void actualizarNombrePlanActivo() {
        android.util.Log.i("RutinasFragment", "Sincronizando interfaz con el nuevo Plan Maestro");
    }

    private void actualizarVisibilidadFab(int tabPosition) {
        if (tabPosition == 0) {
            // Agenda: Ocultar botones flotantes
            if (fabAddEjercicio != null) fabAddEjercicio.hide();
            if (btnIrACrear != null) btnIrACrear.hide();
        } else if (tabPosition == 1) {
            // Explorar: Ocultar FAB de ejercicio, el botón de Nuevo Plan se gestiona en PlanesFragment
            if (btnIrACrear != null) btnIrACrear.hide();
            if (fabAddEjercicio != null) fabAddEjercicio.hide();
        } else if (tabPosition == 2) {
            // Mis Rutinas: Botón para crear nueva plantilla (Entrenamiento)
            if (fabAddEjercicio != null) fabAddEjercicio.hide();
            if (btnIrACrear != null) {
                btnIrACrear.show();
                btnIrACrear.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), CrearRutinaActivity.class);
                    intent.putExtra("ES_PLANTILLA", true);
                    startActivity(intent);
                });
            }
        } else if (tabPosition == 3) {
            // Biblioteca: Botones ocultos (se gestionan dentro del fragmento de biblioteca)
            if (btnIrACrear != null) btnIrACrear.hide();
            if (fabAddEjercicio != null) fabAddEjercicio.hide();
        }
    }

    private void cargarEjerciciosDesdeServidor() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        // Cargar asociaciones de ejercicios guardadas localmente
        RepositorioRutinas.cargarDatosDesdeLocal(requireContext());

        // Usamos el endpoint del catálogo general en lugar del específico del usuario
        // para que todos los usuarios puedan ver los entrenamientos disponibles.
        RetrofitClient.getApiService().listarEntrenamientosCatalogo().enqueue(new Callback<ApiResponseDto<List<EntrenamientoResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Response<ApiResponseDto<List<EntrenamientoResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaEjerciciosCompleta = response.body().getDatos();
                    configurarFiltrosCategorias();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar entrenamientos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarFiltrosCategorias() {
        tabLayoutCategorias.removeAllTabs();
        tabLayoutCategorias.addTab(tabLayoutCategorias.newTab().setText("TODOS"));

        Set<String> categorias = new HashSet<>();
        for (EntrenamientoResponseDto e : listaEjerciciosCompleta) {
            if (e.getCategoria() != null && !e.getCategoria().isEmpty()) {
                categorias.add(e.getCategoria().toUpperCase());
            }
        }

        for (String cat : categorias) {
            tabLayoutCategorias.addTab(tabLayoutCategorias.newTab().setText(cat));
        }

        filtrarPorCategoria("TODOS");
    }

    private void filtrarPorCategoria(String categoria) {
        List<EntrenamientoResponseDto> filtrada;
        if (categoria.equals("TODOS")) {
            filtrada = new ArrayList<>(listaEjerciciosCompleta);
        } else {
            filtrada = listaEjerciciosCompleta.stream()
                    .filter(e -> e.getCategoria() != null && e.getCategoria().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }

        ejerciciosAdapter.updateLista(filtrada);

        if (filtrada.isEmpty()) {
            layoutVacio.setVisibility(View.VISIBLE);
            rvEjercicios.setVisibility(View.GONE);
        } else {
            layoutVacio.setVisibility(View.GONE);
            rvEjercicios.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDialogoNuevoEjercicio() {
        String[] opciones = {"Buscar en Biblioteca Externa", "Crear Manualmente"};
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Nuevo Entrenamiento")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) mostrarDialogoBusquedaExterna();
                    else mostrarDialogoNuevoEjercicioManual();
                })
                .show();
    }

    private void mostrarDialogoNuevoEjercicioManual() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_admin_nuevo_ejercicio);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextInputEditText etNombre = dialog.findViewById(R.id.etNombreEjercicio);
        TextInputEditText etDesc = dialog.findViewById(R.id.etDescripcionEjercicio);
        TextInputEditText etCat = dialog.findViewById(R.id.etCategoriaEjercicio);
        TextInputEditText etDur = dialog.findViewById(R.id.etDuracionEjercicio);
        TextInputEditText etInt = dialog.findViewById(R.id.etIntensidadEjercicio);
        MaterialButton btnGuardar = dialog.findViewById(R.id.btnGuardarEjercicio);
        View btnCancelar = dialog.findViewById(R.id.btnCancelarEjercicio);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String cat = etCat.getText().toString().trim();
            String durStr = etDur.getText().toString().trim();
            String intensidad = etInt.getText().toString().trim();

            if (nombre.isEmpty() || durStr.isEmpty() || cat.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, rellena los campos básicos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int duracion = Integer.parseInt(durStr);

                // Creamos el objeto Request usando SETTERS para evitar errores de constructor
                EntrenamientoRequestDto request = new EntrenamientoRequestDto(
                        nombre,
                        desc,
                        Integer.parseInt(durStr),
                        intensidad.isEmpty() ? "MEDIA" : intensidad,
                        cat,   // categoria como String — exactamente lo que el backend espera
                        1,     // cantidadEjercicios
                        "",    // urlVideo vacío
                        null   // urlImagen null
                );

                RetrofitClient.getApiService().crearEntrenamiento(request).enqueue(new Callback<ApiResponseDto<EntrenamientoResponseDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Response<ApiResponseDto<EntrenamientoResponseDto>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Ejercicio Manual Guardado", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            cargarEjerciciosDesdeServidor();
                        }
                    }
                    @Override public void onFailure(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Throwable t) {}
                });
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Número de duración no válido", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    private void mostrarDialogoBusquedaExterna() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_buscar_ejercicios, null);
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialogView);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialogView.findViewById(R.id.btnCerrarBuscador).setOnClickListener(v -> dialog.dismiss());

        TextInputEditText etSearch = dialogView.findViewById(R.id.etSearchExternal);
        View btnSearch = dialogView.findViewById(R.id.btnSearchExternal);
        android.widget.ProgressBar pb = dialogView.findViewById(R.id.pbLoadingExternal);
        RecyclerView rvExt = dialogView.findViewById(R.id.rvExternalExercises);

        List<com.example.skynet.data.remote.dto.ExerciseApiRequestDto> listaExt = new ArrayList<>();
        com.example.skynet.ui.ejercicios.AdminEjerciciosAdapter extAdapter = new com.example.skynet.ui.ejercicios.AdminEjerciciosAdapter(listaExt, exercise -> {
            guardarEntrenamientoDesdeExterno(exercise, dialog);
        });

        rvExt.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExt.setAdapter(extAdapter);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (query.isEmpty()) return;

            pb.setVisibility(View.VISIBLE);
            RetrofitClient.getApiService().buscarEnBiblioteca(query).enqueue(new Callback<ApiResponseDto<List<com.example.skynet.data.remote.dto.ExerciseApiRequestDto>>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<List<com.example.skynet.data.remote.dto.ExerciseApiRequestDto>>> call, Response<ApiResponseDto<List<com.example.skynet.data.remote.dto.ExerciseApiRequestDto>>> response) {
                    pb.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        listaExt.clear();
                        listaExt.addAll(response.body().getDatos());
                        extAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponseDto<List<com.example.skynet.data.remote.dto.ExerciseApiRequestDto>>> call, Throwable t) {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void guardarEntrenamientoDesdeExterno(com.example.skynet.data.remote.dto.ExerciseApiRequestDto exercise, Dialog parentDialog) {

        // 1. Limpiamos y preparamos la descripción (para que no sea NULL)
        String desc = exercise.getDescription() != null ? exercise.getDescription() : "Ejercicio de " + exercise.getTarget();
        if (exercise.getInstructions() != null && !exercise.getInstructions().isEmpty()) {
            desc += "\n\nInstrucciones:\n" + String.join("\n", exercise.getInstructions());
        }

        // 2. Creamos el objeto Request usando SETTERS
        // Esto asegura que cada campo del objeto de la API externa (exercise)
        // se mapee correctamente a tu base de datos.
        EntrenamientoRequestDto request = new EntrenamientoRequestDto(
                exercise.getName(),
                desc,
                10,
                "Media",
                exercise.getTarget().toUpperCase(), // categoria = músculo objetivo
                1,
                exercise.getGifUrl(), // urlVideo = link de YouTube que ya construyes
                null                  // urlImagen null
        );// ID de categoría por defecto (ajustar si tienes mapeo)

        // 3. Enviamos la petición al Backend
        RetrofitClient.getApiService().crearEntrenamiento(request).enqueue(new Callback<ApiResponseDto<EntrenamientoResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Response<ApiResponseDto<EntrenamientoResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Ejercicio añadido al catálogo!", Toast.LENGTH_SHORT).show();
                    parentDialog.dismiss(); // Cerramos el buscador
                    cargarEjerciciosDesdeServidor(); // Refrescamos tu lista de "Explorar"
                } else {
                    String mensaje = "Error al guardar: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponseDto<?> errorDto = new Gson().fromJson(errorJson, ApiResponseDto.class);
                            if (errorDto != null && errorDto.getMensaje() != null) {
                                mensaje = errorDto.getMensaje();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        // Cargamos los datos. El delay de 400ms se mantiene si se considera necesario por la transición de fragmentos,
        // pero eliminamos llamadas redundantes si ya se está cargando o es innecesario.
        refreshAllData();
    }

    private void refreshAllData() {
        if (!isAdded()) return;
        cargarRutinaPorFecha();
        cargarEstadisticas();
        cargarEjerciciosDesdeServidor();
        actualizarRutinasGuardadas();
    }
}