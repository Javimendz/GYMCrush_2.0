package com.example.skynet.ui.clases;

import android.text.InputType;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skynet.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import com.example.skynet.ui.actividades.ActividadesFragment;
import com.example.skynet.ui.actividades.ActividadesAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.skynet.data.model.Clase;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ActividadRequestDto;
import com.example.skynet.data.remote.dto.ActividadResponseDto;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.CategoriaRequestDto;
import com.example.skynet.data.remote.dto.CategoriaResponseDto;
import com.example.skynet.data.remote.dto.HorarioRequestDto;
import com.example.skynet.data.remote.dto.HorarioResponseDto;
import com.example.skynet.data.remote.dto.PerfilResponseDto;
import com.example.skynet.data.remote.dto.ReservaResponseDto;
import com.example.skynet.data.remote.dto.SalaRequestDto;
import com.example.skynet.data.remote.dto.SalaResponseDto;
import com.example.skynet.data.repository.ReservasRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClasesFragment extends Fragment implements ClasesAdapter.OnClaseClickListener, ActividadesAdapter.OnActividadClickListener {

    private RecyclerView rvClases, rvActividades;
    private ClasesAdapter adapter;
    private ActividadesAdapter actividadesAdapter;
    private ClasesViewModel viewModel;
    private List<MaterialButton> botonesDias;
    private FloatingActionButton fabAddClase;
    private String diaActual = "LUNES";
    private List<HorarioResponseDto> listaHorariosOriginal = new ArrayList<>();
    private List<ActividadResponseDto> todasLasActividades = new ArrayList<>();
    
    private View layoutTabHorario, layoutTabActividades;
    private TabLayout tabLayout;
    private SearchView searchViewActividades;
    private boolean modoGestion = false;

    public static ClasesFragment newInstance(boolean modoGestion) {
        ClasesFragment fragment = new ClasesFragment();
        Bundle args = new Bundle();
        args.putBoolean("modo_gestion", modoGestion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modoGestion = getArguments().getBoolean("modo_gestion", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases, container, false);

        viewModel = new ViewModelProvider(this).get(ClasesViewModel.class);

        // UI HORARIO
        rvClases = view.findViewById(R.id.rvClases);
        int spanCount = getSpanCount();
        rvClases.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), spanCount));
        adapter = new ClasesAdapter(new ArrayList<>(), this);
        rvClases.setAdapter(adapter);

        // UI ACTIVIDADES
        rvActividades = view.findViewById(R.id.rvActividades);
        rvActividades.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), spanCount));
        actividadesAdapter = new ActividadesAdapter(new ArrayList<>(), this);
        rvActividades.setAdapter(actividadesAdapter);

        layoutTabHorario = view.findViewById(R.id.layoutTabHorario);
        layoutTabActividades = view.findViewById(R.id.layoutTabActividades);
        tabLayout = view.findViewById(R.id.tabLayoutClases);
        searchViewActividades = view.findViewById(R.id.searchViewActividades);

        view.findViewById(R.id.btnVolverClases).setOnClickListener(v -> {
            if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).mostrarHome();
            }
        });

        configurarTabs();
        configurarBotonesDias(view);
        setupObservers();
        setupSearchActividades();

        fabAddClase = view.findViewById(R.id.fabAddClase);
        checkAdminRole();
        fabAddClase.setOnClickListener(v -> {
            if (tabLayout.getSelectedTabPosition() == 0) {
                mostrarOpcionesGestionHorario();
            } else {
                mostrarOpcionesGestionActividades();
            }
        });

        diaActual = getDiaDeHoy();
        marcarBotonDiaActual(view);
        viewModel.cargarHorariosPorDia(diaActual);
        cargarActividades();

        return view;
    }

    private void configurarTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    layoutTabHorario.setVisibility(View.VISIBLE);
                    layoutTabActividades.setVisibility(View.GONE);
                    fabAddClase.setOnClickListener(v -> mostrarOpcionesGestionHorario());
                } else {
                    layoutTabHorario.setVisibility(View.GONE);
                    layoutTabActividades.setVisibility(View.VISIBLE);
                    fabAddClase.setOnClickListener(v -> mostrarOpcionesGestionActividades());
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearchActividades() {
        if (searchViewActividades != null) {
            // Prevenir crash de OneSearchSuggestProvider
            searchViewActividades.setSuggestionsAdapter(null);
            searchViewActividades.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            // Evitar que el teclado intente aprender de este campo, lo que puede disparar AppSearch
            searchViewActividades.setImeOptions(searchViewActividades.getImeOptions() | EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING);

            searchViewActividades.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    buscarActividad(query);
                    return true;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.isEmpty()) {
                        actividadesAdapter.setActividades(todasLasActividades);
                    } else {
                        List<ActividadResponseDto> filtradas = todasLasActividades.stream()
                                .filter(a -> a.getNombre().toLowerCase().contains(newText.toLowerCase()))
                                .collect(Collectors.toList());
                        actividadesAdapter.setActividades(filtradas);
                    }
                    return true;
                }
            });
        }
    }

    private void cargarActividades() {
        RetrofitClient.getApiService().getTodasLasActividades().enqueue(new Callback<ApiResponseDto<List<ActividadResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<ActividadResponseDto>>> call, Response<ApiResponseDto<List<ActividadResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todasLasActividades = response.body().getDatos();
                    actividadesAdapter.setActividades(todasLasActividades);
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<ActividadResponseDto>>> call, Throwable t) {}
        });
    }

    private void buscarActividad(String nombre) {
        RetrofitClient.getApiService().buscarActividadPorNombre(nombre).enqueue(new Callback<ApiResponseDto<List<ActividadResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<ActividadResponseDto>>> call, Response<ApiResponseDto<List<ActividadResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    actividadesAdapter.setActividades(response.body().getDatos());
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<ActividadResponseDto>>> call, Throwable t) {}
        });
    }

    private String getDiaDeHoy() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int day = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        switch (day) {
            case java.util.Calendar.MONDAY: return "LUNES";
            case java.util.Calendar.TUESDAY: return "MARTES";
            case java.util.Calendar.WEDNESDAY: return "MIERCOLES";
            case java.util.Calendar.THURSDAY: return "JUEVES";
            case java.util.Calendar.FRIDAY: return "VIERNES";
            case java.util.Calendar.SATURDAY: return "SABADO";
            case java.util.Calendar.SUNDAY: return "DOMINGO";
            default: return "LUNES";
        }
    }

    private void marcarBotonDiaActual(View view) {
        String diaSimplificado = diaActual;
        int btnId = R.id.btnLunes;
        if (diaSimplificado.equals("MARTES")) btnId = R.id.btnMartes;
        else if (diaSimplificado.equals("MIERCOLES")) btnId = R.id.btnMiercoles;
        else if (diaSimplificado.equals("JUEVES")) btnId = R.id.btnJueves;
        else if (diaSimplificado.equals("VIERNES")) btnId = R.id.btnViernes;
        else if (diaSimplificado.equals("SABADO")) btnId = R.id.btnSabado;
        else if (diaSimplificado.equals("DOMINGO")) btnId = R.id.btnDomingo;
        
        MaterialButton btn = view.findViewById(btnId);
        if (btn != null) actualizarEstiloBotones(btn);
    }

    private void checkAdminRole() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        
        if (isAdmin) {
            fabAddClase.setVisibility(View.VISIBLE);
        } else {
            fabAddClase.setVisibility(View.GONE);
        }
        
        if (adapter != null) adapter.setAdminMode(isAdmin);
        if (actividadesAdapter != null) actividadesAdapter.setAdminMode(isAdmin);
    }

    private void setupObservers() {
        viewModel.getHorarios().observe(getViewLifecycleOwner(), horarios -> {
            if (horarios != null) {
                listaHorariosOriginal = horarios;
                aplicarHorarios();
            } else {
                listaHorariosOriginal = new ArrayList<>();
                adapter.setClases(new ArrayList<>());
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aplicarHorarios() {
        if (listaHorariosOriginal == null) return;

        List<Clase> listaMapeada = new ArrayList<>();
        for (HorarioResponseDto h : listaHorariosOriginal) {
            // USA EL CAMPO DIRECTAMENTE
            int disponibles = h.getPlazasLibres();
            int aforo = h.getAforoMax() != null ? h.getAforoMax() : 0;

            listaMapeada.add(new Clase(
                    h.getId(),
                    h.getNombreActividad(),
                    h.getHoraInicio() + " - " + h.getHoraFin(),
                    h.getNombreSala(),
                    h.getNombreEntrenador(),
                    disponibles, // <--- No más restas aquí
                    aforo,
                    h.getDiaSemana()
            ));
        }
        adapter.setClases(listaMapeada);
    }

    private void configurarBotonesDias(View view) {
        botonesDias = new ArrayList<>();
        botonesDias.add(view.findViewById(R.id.btnLunes));
        botonesDias.add(view.findViewById(R.id.btnMartes));
        botonesDias.add(view.findViewById(R.id.btnMiercoles));
        botonesDias.add(view.findViewById(R.id.btnJueves));
        botonesDias.add(view.findViewById(R.id.btnViernes));
        botonesDias.add(view.findViewById(R.id.btnSabado));
        botonesDias.add(view.findViewById(R.id.btnDomingo));

        String[] codigosDias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};

        for (int i = 0; i < botonesDias.size(); i++) {
            final int index = i;
            if (botonesDias.get(i) != null) {
                botonesDias.get(i).setOnClickListener(v -> {
                    actualizarEstiloBotones(botonesDias.get(index));
                    diaActual = codigosDias[index];
                    viewModel.cargarHorariosPorDia(diaActual);
                });
            }
        }
    }

    private void actualizarEstiloBotones(MaterialButton botonSeleccionado) {
        for (MaterialButton btn : botonesDias) {
            if (btn == null) continue;
            if (btn == botonSeleccionado) {
                btn.setStrokeColorResource(R.color.accent_color);
                btn.setAlpha(1.0f);
            } else {
                btn.setStrokeColorResource(android.R.color.transparent);
                btn.setAlpha(0.6f);
            }
            btn.setTextColor(Color.WHITE);
        }
    }

    private void mostrarOpcionesGestionHorario() {
        String[] opciones = {"Nueva Clase (Horario)", "Gestionar Salas (Crear/Eliminar)"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Gestión de Horarios")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        abrirDialogoNuevaClase();
                    } else {
                        abrirDialogoGestionSalas();
                    }
                })
                .show();
    }

    private void mostrarOpcionesGestionActividades() {
        String[] opciones = {"Nueva Actividad"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Gestión de Actividades")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogoNuevaActividad();
                    }
                })
                .show();
    }

    private void mostrarDialogoNuevaActividad() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nueva_sala, null);
        android.widget.EditText etNombre = dialogView.findViewById(R.id.etNombreSala);
        android.widget.EditText etPrecio = dialogView.findViewById(R.id.etCapacidadSala);
        android.widget.Spinner spinnerCategoria = new android.widget.Spinner(getContext());
        android.widget.Spinner spinnerSala = new android.widget.Spinner(getContext());

        com.google.android.material.textfield.TextInputLayout tilNombre = (com.google.android.material.textfield.TextInputLayout) etNombre.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilCapacidad = (com.google.android.material.textfield.TextInputLayout) etPrecio.getParent().getParent();
        tilNombre.setHint("Nombre de la actividad");
        tilCapacidad.setHint("Precio");

        android.widget.LinearLayout layout = dialogView.findViewById(R.id.layoutContainer);
        layout.addView(crearLabel("Seleccionar Categoría:"));
        if (spinnerCategoria.getParent() != null) ((android.view.ViewGroup) spinnerCategoria.getParent()).removeView(spinnerCategoria);
        layout.addView(spinnerCategoria);
        layout.addView(crearLabel("Seleccionar Sala:"));
        if (spinnerSala.getParent() != null) ((android.view.ViewGroup) spinnerSala.getParent()).removeView(spinnerSala);
        layout.addView(spinnerSala);

        // Cargar Salas
        RetrofitClient.getApiService().getTodasLasSalas().enqueue(new Callback<ApiResponseDto<List<SalaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<SalaResponseDto>>> call, Response<ApiResponseDto<List<SalaResponseDto>>> response) {
                List<Object> items = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    items.addAll(response.body().getDatos());
                }
                items.add(" + Nueva Sala");
                ArrayAdapter<Object> adapterSala = new ArrayAdapter<Object>(getContext(), android.R.layout.simple_spinner_item, items) {
                    @Override public View getView(int p, View c, ViewGroup parent) {
                        TextView v = (TextView) super.getView(p, c, parent);
                        v.setTextColor(Color.WHITE);
                        Object item = getItem(p);
                        if (item instanceof SalaResponseDto) v.setText(((SalaResponseDto)item).getNombre());
                        else if (item != null) v.setText(item.toString());
                        return v;
                    }
                    @Override public View getDropDownView(int p, View c, ViewGroup parent) {
                        TextView v = (TextView) super.getDropDownView(p, c, parent);
                        v.setTextColor(Color.BLACK);
                        Object item = getItem(p);
                        if (item instanceof SalaResponseDto) v.setText(((SalaResponseDto)item).getNombre());
                        else if (item != null) v.setText(item.toString());
                        return v;
                    }
                };
                adapterSala.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSala.setAdapter(adapterSala);
            }
            @Override public void onFailure(Call<ApiResponseDto<List<SalaResponseDto>>> call, Throwable t) {}
        });

        // Cargar categorías
        RetrofitClient.getApiService().getCategoriasActividad().enqueue(new Callback<ApiResponseDto<List<CategoriaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Response<ApiResponseDto<List<CategoriaResponseDto>>> response) {
                List<Object> items = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    items.addAll(response.body().getDatos());
                }
                items.add(" + Nueva Categoría");
                ArrayAdapter<Object> adapterCat = new ArrayAdapter<Object>(getContext(), android.R.layout.simple_spinner_item, items) {
                    @Override
                    public View getView(int pos, View convert, ViewGroup parent) {
                        TextView v = (TextView) super.getView(pos, convert, parent);
                        v.setTextColor(Color.WHITE);
                        Object item = getItem(pos);
                        if (item instanceof CategoriaResponseDto) v.setText(((CategoriaResponseDto)item).getNombre());
                        else if (item != null) v.setText(item.toString());
                        return v;
                    }
                    @Override
                    public View getDropDownView(int pos, View convert, ViewGroup parent) {
                        TextView v = (TextView) super.getDropDownView(pos, convert, parent);
                        v.setTextColor(Color.BLACK);
                        Object item = getItem(pos);
                        if (item instanceof CategoriaResponseDto) v.setText(((CategoriaResponseDto)item).getNombre());
                        else if (item != null) v.setText(item.toString());
                        return v;
                    }
                };
                adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategoria.setAdapter(adapterCat);
            }
            @Override public void onFailure(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Throwable t) {}
        });

        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle("Nueva Actividad")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = etNombre.getText().toString();
                    String precioStr = etPrecio.getText().toString();
                    Object selectedCat = spinnerCategoria.getSelectedItem();
                    Object selectedSala = spinnerSala.getSelectedItem();

                    if (nombre.isEmpty() || precioStr.isEmpty() || selectedCat == null || selectedSala == null) {
                        Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selectedSala instanceof String && selectedSala.equals(" + Nueva Sala")) {
                        mostrarDialogoCrearSala(nombreNuevaSala -> {
                            if (selectedCat instanceof String && selectedCat.equals(" + Nueva Categoría")) {
                                mostrarDialogoCrearCategoria(nombreNuevaCat -> {
                                    crearActividadConTodoNuevo(nombre, precioStr, nombreNuevaCat, nombreNuevaSala);
                                });
                            } else if (selectedCat instanceof CategoriaResponseDto) {
                                crearActividadConSalaNueva(nombre, precioStr, ((CategoriaResponseDto) selectedCat).getId(), nombreNuevaSala);
                            }
                        });
                    } else if (selectedCat instanceof String && selectedCat.equals(" + Nueva Categoría")) {
                        mostrarDialogoCrearCategoria(nombreNuevaCat -> {
                            crearActividadConCategoriaNueva(nombre, precioStr, nombreNuevaCat, ((SalaResponseDto) selectedSala).getNombre());
                        });
                    } else if (selectedCat instanceof CategoriaResponseDto && selectedSala instanceof SalaResponseDto) {
                        CategoriaResponseDto cat = (CategoriaResponseDto) selectedCat;
                        SalaResponseDto sala = (SalaResponseDto) selectedSala;
                        ActividadRequestDto request = new ActividadRequestDto(nombre, "Actividad de " + cat.getNombre(), 60, sala.getNombre(), Integer.parseInt(precioStr), cat.getId());
                        enviarPeticionCrearActividad(request);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoCrearSala(ActividadesFragment.OnNombreRecibidoListener listener) {
        EditText et = new EditText(getContext());
        et.setHint("Nombre de la nueva sala");
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Crear Sala")
                .setView(et)
                .setPositiveButton("Crear", (d, w) -> {
                    String nombre = et.getText().toString();
                    if (!nombre.isEmpty()) {
                        // Nota: Asegúrate que el DTO del request use 'capacidad' si lo cambiaste en el backend
                        SalaRequestDto req = new SalaRequestDto(nombre, 20, "Sala nueva", "Planta 1", "Eándar", true);
                        RetrofitClient.getApiService().crearSala(req).enqueue(new Callback<ApiResponseDto<SalaResponseDto>>() {
                            @Override
                            public void onResponse(Call<ApiResponseDto<SalaResponseDto>> call, Response<ApiResponseDto<SalaResponseDto>> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Sala " + nombre + " lista.", Toast.LENGTH_SHORT).show();
                                    listener.onNombreRecibido(nombre);
                                    // IMPORTANTE: Podrías disparar un refresco de la lista de salas aquí
                                }
                            }
                            @Override public void onFailure(Call<ApiResponseDto<SalaResponseDto>> call, Throwable t) {}
                        });
                    }
                }).show();
    }

    private void crearActividadConSalaNueva(String nombre, String precioStr, Long categoriaId, String nombreSala) {
        ActividadRequestDto request = new ActividadRequestDto(nombre, "Actividad en " + nombreSala, 60, nombreSala, Integer.parseInt(precioStr), categoriaId);
        enviarPeticionCrearActividad(request);
    }

    private void crearActividadConTodoNuevo(String nombre, String precioStr, String nombreCat, String nombreSala) {
        RetrofitClient.getApiService().getCategoriasActividad().enqueue(new Callback<ApiResponseDto<List<CategoriaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Response<ApiResponseDto<List<CategoriaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (CategoriaResponseDto c : response.body().getDatos()) {
                        if (c.getNombre().equalsIgnoreCase(nombreCat)) {
                            ActividadRequestDto request = new ActividadRequestDto(nombre, "Actividad de " + nombreCat, 60, nombreSala, Integer.parseInt(precioStr), c.getId());
                            enviarPeticionCrearActividad(request);
                            break;
                        }
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Throwable t) {}
        });
    }

    private void mostrarDialogoCrearCategoria(ActividadesFragment.OnNombreRecibidoListener listener) {
        EditText et = new EditText(getContext());
        et.setHint("Nombre de la nueva categoría");
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Crear Categoría")
                .setView(et)
                .setPositiveButton("Crear", (d, w) -> {
                    String nombre = et.getText().toString();
                    if (!nombre.isEmpty()) {
                        CategoriaRequestDto req = new CategoriaRequestDto(nombre, "Categoría auto-creada");
                        RetrofitClient.getApiService().crearCategoriaActividad(req).enqueue(new Callback<ApiResponseDto<CategoriaResponseDto>>() {
                            @Override
                            public void onResponse(Call<ApiResponseDto<CategoriaResponseDto>> call, Response<ApiResponseDto<CategoriaResponseDto>> response) {
                                if (response.isSuccessful()) listener.onNombreRecibido(nombre);
                            }
                            @Override public void onFailure(Call<ApiResponseDto<CategoriaResponseDto>> call, Throwable t) {}
                        });
                    }
                }).show();
    }

    private void crearActividadConCategoriaNueva(String nombre, String precioStr, String nombreCat, String nombreSala) {
        RetrofitClient.getApiService().getCategoriasActividad().enqueue(new Callback<ApiResponseDto<List<CategoriaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Response<ApiResponseDto<List<CategoriaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (CategoriaResponseDto c : response.body().getDatos()) {
                        if (c.getNombre().equalsIgnoreCase(nombreCat)) {
                            ActividadRequestDto request = new ActividadRequestDto(nombre, "Actividad de " + nombreCat, 60, nombreSala, Integer.parseInt(precioStr), c.getId());
                            enviarPeticionCrearActividad(request);
                            break;
                        }
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Throwable t) {}
        });
    }

    private void enviarPeticionCrearActividad(ActividadRequestDto request) {
        RetrofitClient.getApiService().crearActividad(request).enqueue(new Callback<ApiResponseDto<ActividadResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<ActividadResponseDto>> call, Response<ApiResponseDto<ActividadResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Actividad creada con éxito", Toast.LENGTH_SHORT).show();
                    
                    if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                        ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).crearNotificacionManual(
                                "Nueva Actividad",
                                "Se ha creado la actividad: " + request.getNombre()
                        );
                    }

                    cargarActividades();
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<ActividadResponseDto>> call, Throwable t) {}
        });
    }

    private void abrirDialogoNuevaClase() {
        Dialog dialog = new Dialog(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nuevo_horario, null);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        Spinner spinnerDia = dialogView.findViewById(R.id.spinnerDiaSemana);
        Spinner spinnerSala = dialogView.findViewById(R.id.spinnerSala);
        Spinner spinnerEntrenador = dialogView.findViewById(R.id.spinnerEntrenador);
        Button btnHoraInicio = dialogView.findViewById(R.id.btnHoraInicio);
        Button btnHoraFin = dialogView.findViewById(R.id.btnHoraFin);
        EditText etAforo = dialogView.findViewById(R.id.etAforoMax);

        // Añadir spinner de Actividad dinámicamente o buscar si existe
        LinearLayout layoutPrincipal = (LinearLayout) ((ViewGroup) dialogView).getChildAt(0); // El LinearLayout dentro del Card
        Spinner spinnerActividad = new Spinner(getContext());
        layoutPrincipal.addView(crearLabel("Seleccionar Actividad:"), 1);
        if (spinnerActividad.getParent() != null) ((android.view.ViewGroup) spinnerActividad.getParent()).removeView(spinnerActividad);
        layoutPrincipal.addView(spinnerActividad, 2);

        // Configurar Spinner Días
        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};
        ArrayAdapter<String> adapterDias = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, dias) {
            @Override public View getView(int p, View c, ViewGroup parent) {
                TextView v = (TextView) super.getView(p, c, parent);
                v.setTextColor(Color.WHITE);
                return v;
            }
        };
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDia.setAdapter(adapterDias);

        // Cargar Actividades
        RetrofitClient.getApiService().getTodasLasActividades().enqueue(new Callback<ApiResponseDto<List<ActividadResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<ActividadResponseDto>>> call, Response<ApiResponseDto<List<ActividadResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ActividadResponseDto> lista = response.body().getDatos();
                    ArrayAdapter<ActividadResponseDto> adapterAct = new ArrayAdapter<ActividadResponseDto>(getContext(), android.R.layout.simple_spinner_item, lista) {
                        @Override public View getView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            v.setTextColor(Color.WHITE);
                            return v;
                        }
                        @Override public View getDropDownView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getDropDownView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            return v;
                        }
                    };
                    spinnerActividad.setAdapter(adapterAct);
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<ActividadResponseDto>>> call, Throwable t) {}
        });

        // Cargar Salas
        RetrofitClient.getApiService().getTodasLasSalas().enqueue(new Callback<ApiResponseDto<List<SalaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<SalaResponseDto>>> call, Response<ApiResponseDto<List<SalaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SalaResponseDto> lista = response.body().getDatos();
                    ArrayAdapter<SalaResponseDto> adapterSala = new ArrayAdapter<SalaResponseDto>(getContext(), android.R.layout.simple_spinner_item, lista) {
                        @Override public View getView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            v.setTextColor(Color.WHITE);
                            return v;
                        }
                        @Override public View getDropDownView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getDropDownView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            return v;
                        }
                    };
                    spinnerSala.setAdapter(adapterSala);
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<SalaResponseDto>>> call, Throwable t) {}
        });

        // Cargar Entrenadores (Filtrado solo por aquellos con rol de entrenador)
        RetrofitClient.getApiService().findEntrenadores().enqueue(new Callback<ApiResponseDto<List<PerfilResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Response<ApiResponseDto<List<PerfilResponseDto>>> response) {
                List<PerfilResponseDto> listaAMostrar = new ArrayList<>();
                
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null && !response.body().getDatos().isEmpty()) {
                    listaAMostrar = response.body().getDatos();
                    android.util.Log.d("ClasesFragment", "Entrenadores filtrados cargados: " + listaAMostrar.size());
                } else {
                    android.util.Log.w("ClasesFragment", "El endpoint de entrenadores falló o está vacío (Código: " + response.code() + "). Cargando todos los perfiles como respaldo...");
                    // PLAN B: Si no hay entrenadores filtrados, cargamos todos para no dejar el spinner vacío durante las pruebas
                    cargarTodosLosPerfilesComoRespaldo(spinnerEntrenador);
                    return;
                }

                configurarAdapterEntrenador(spinnerEntrenador, listaAMostrar);
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Throwable t) {
                android.util.Log.e("ClasesFragment", "Error de red. Cargando respaldo...", t);
                cargarTodosLosPerfilesComoRespaldo(spinnerEntrenador);
            }
        });

        final String[] horaInicio = {"10:00"};
        final String[] horaFin = {"11:00"};

        btnHoraInicio.setOnClickListener(v -> {
            new TimePickerDialog(getContext(), (tp, h, m) -> {
                horaInicio[0] = String.format(java.util.Locale.getDefault(), "%02d:%02d", h, m);
                btnHoraInicio.setText("Inicio: " + horaInicio[0]);
            }, 10, 0, true).show();
        });

        btnHoraFin.setOnClickListener(v -> {
            new TimePickerDialog(getContext(), (tp, h, m) -> {
                horaFin[0] = String.format(java.util.Locale.getDefault(), "%02d:%02d", h, m);
                btnHoraFin.setText("Fin: " + horaFin[0]);
            }, 11, 0, true).show();
        });

        dialogView.findViewById(R.id.btnCancelarHorario).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnCrearHorario).setOnClickListener(v -> {
            String dia = (String) spinnerDia.getSelectedItem();
            ActividadResponseDto act = (ActividadResponseDto) spinnerActividad.getSelectedItem();
            SalaResponseDto sala = (SalaResponseDto) spinnerSala.getSelectedItem();
            PerfilResponseDto entrenador = (PerfilResponseDto) spinnerEntrenador.getSelectedItem();
            String aforoStr = etAforo.getText().toString();

            if (act == null || sala == null || entrenador == null || aforoStr.isEmpty()) {
                Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            HorarioRequestDto request = new HorarioRequestDto(
                    dia, horaInicio[0], horaFin[0], Integer.parseInt(aforoStr), act.getId(), sala.getId(), entrenador.getUsuarioId()
            );

            RetrofitClient.getApiService().crearHorario(request).enqueue(new Callback<ApiResponseDto<HorarioResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<HorarioResponseDto>> call, Response<ApiResponseDto<HorarioResponseDto>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Horario creado con éxito", Toast.LENGTH_SHORT).show();
                        
                        if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                            ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).crearNotificacionManual(
                                    "Nueva Clase Programada",
                                    "Se ha añadido " + act.getNombre() + " al horario del " + dia
                            );
                        }

                        diaActual = dia;
                        for (MaterialButton btn : botonesDias) {
                            if (btn.getText().toString().equalsIgnoreCase(diaActual)) {
                                actualizarEstiloBotones(btn);
                                break;
                            }
                        }
                        viewModel.cargarHorariosPorDia(diaActual);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<ApiResponseDto<HorarioResponseDto>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private TextView crearLabel(String texto) {
        TextView tv = new TextView(getContext());
        tv.setText(texto);
        tv.setPadding(0, 20, 0, 5);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(14);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        return tv;
    }

    private void abrirDialogoGestionSalas() {
        RetrofitClient.getApiService().getTodasLasSalas().enqueue(new Callback<ApiResponseDto<List<SalaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<SalaResponseDto>>> call, Response<ApiResponseDto<List<SalaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mostrarListaSalas(response.body().getDatos());
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<SalaResponseDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar salas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarListaSalas(List<SalaResponseDto> salas) {
        String[] nombres = new String[salas.size() + 1];
        for (int i = 0; i < salas.size(); i++) {
            nombres[i] = salas.get(i).getNombre() + " (" + salas.get(i).getCapacidadMax() + "p)";
        }
        nombres[salas.size()] = "➕ Añadir Nueva Sala";

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Gestión de Salas")
                .setItems(nombres, (dialog, which) -> {
                    if (which == salas.size()) {
                        abrirDialogoCrearSala();
                    } else {
                        confirmarEliminarSala(salas.get(which));
                    }
                })
                .show();
    }

    private void abrirDialogoCrearSala() {
        Dialog dialog = new Dialog(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nueva_sala, null);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        android.widget.EditText etNombre = dialogView.findViewById(R.id.etNombreSala);
        android.widget.EditText etCapacidad = dialogView.findViewById(R.id.etCapacidadSala);

        dialogView.findViewById(R.id.btnCancelarSala).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnGuardarSala).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String capStr = etCapacidad.getText().toString();
            if (nombre.isEmpty() || capStr.isEmpty()) {
                Toast.makeText(getContext(), "Completa los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int cap = Integer.parseInt(capStr);
            SalaRequestDto request = new SalaRequestDto(nombre, cap, "Sala de entrenamiento", "Planta 1", "Equipamiento estándar", true);

            RetrofitClient.getApiService().crearSala(request).enqueue(new Callback<ApiResponseDto<SalaResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<SalaResponseDto>> call, Response<ApiResponseDto<SalaResponseDto>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Sala creada", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponseDto<SalaResponseDto>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void confirmarEliminarSala(SalaResponseDto sala) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar Sala")
                .setMessage("¿Estás seguro de eliminar la sala: " + sala.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    RetrofitClient.getApiService().eliminarSala(sala.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Sala eliminada", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onApuntarseClick(Clase clase) {
        if (clase.getCuposDisponibles() <= 0) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("¡Llegas tarde, guerrero!")
                    .setMessage("Esta clase de " + clase.getNombre() + " ya está a tope. No te quedes lamentándote, ¡busca otra sesión y dale duro!")
                    .setPositiveButton("Entendido", null)
                    .show();
            return;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "Identifícate primero si quieres entrenar conmigo.", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_confirmar_reserva);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvMensaje = dialog.findViewById(R.id.tv_mensaje_reserva);
        String mensaje = "¿Listo para sudar en " + clase.getNombre() + "?";
        if (clase.getCuposDisponibles() <= 3) {
            mensaje = "¡Solo quedan " + clase.getCuposDisponibles() + " plazas! ¿Vas a dejar que te la quiten?";
        }
        tvMensaje.setText(mensaje);

        dialog.findViewById(R.id.btn_cancelar_reserva).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_confirmar_reserva).setOnClickListener(v -> {
            viewModel.realizarReserva(userId, clase.getId(), new ReservasRepository.RepositoryCallback<ReservaResponseDto>() {
                public void onSuccess(ReservaResponseDto result) {
                    Toast.makeText(getContext(), "¡Dentro! Te veo en la sala.", Toast.LENGTH_SHORT).show();
                    
                    if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                        ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).crearNotificacionManual(
                                "Reserva Confirmada",
                                "Te has apuntado a " + clase.getNombre() + " (" + clase.getHora() + ")"
                        );
                    }

                    dialog.dismiss();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onActividadClick(ActividadResponseDto actividad) {
        String mensaje = "Categoría: " + (actividad.getCategoria() != null ? actividad.getCategoria().getNombre() : "N/A") + "\n" +
                         "Precio: " + actividad.getPrecio() + "€\n" +
                         "Duración: " + actividad.getDuracion() + " min\n" +
                         "Sala: " + actividad.getSala();

        if (actividad.getDescripcion() != null && !actividad.getDescripcion().isEmpty()) {
            mensaje += "\n\n" + actividad.getDescripcion();
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(actividad.getNombre())
                .setMessage(mensaje)
                .setPositiveButton("Cerrar", null);

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        if (isAdmin) {
            builder.setNeutralButton("Eliminar", (dialog, which) -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("¿Eliminar actividad?")
                        .setMessage("Esta acción no se puede deshacer.")
                        .setPositiveButton("Eliminar", (d, w) -> {
                            RetrofitClient.getApiService().eliminarActividad(actividad.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
                                @Override
                                public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Actividad eliminada", Toast.LENGTH_SHORT).show();
                                        cargarActividades();
                                    }
                                }
                                @Override
                                public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {}
                            });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });

            builder.setNegativeButton("Editar", (dialog, which) -> {
                mostrarDialogoEditarActividad(actividad);
            });
        }

        builder.show();
    }

    private void mostrarDialogoEditarActividad(ActividadResponseDto actividad) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nueva_sala, null);
        android.widget.EditText etNombre = dialogView.findViewById(R.id.etNombreSala);
        android.widget.EditText etPrecio = dialogView.findViewById(R.id.etCapacidadSala);
        android.widget.Spinner spinnerCategoria = new android.widget.Spinner(getContext());
        android.widget.Spinner spinnerSala = new android.widget.Spinner(getContext());

        etNombre.setText(actividad.getNombre());
        etPrecio.setText(String.valueOf(actividad.getPrecio()));

        com.google.android.material.textfield.TextInputLayout tilNombre = (com.google.android.material.textfield.TextInputLayout) etNombre.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilCapacidad = (com.google.android.material.textfield.TextInputLayout) etPrecio.getParent().getParent();
        tilNombre.setHint("Nombre de la actividad");
        tilCapacidad.setHint("Precio");

        android.widget.LinearLayout layout = dialogView.findViewById(R.id.layoutContainer);
        layout.addView(crearLabel("Seleccionar Categoría:"));
        if (spinnerCategoria.getParent() != null) ((android.view.ViewGroup) spinnerCategoria.getParent()).removeView(spinnerCategoria);
        layout.addView(spinnerCategoria);
        layout.addView(crearLabel("Seleccionar Sala:"));
        if (spinnerSala.getParent() != null) ((android.view.ViewGroup) spinnerSala.getParent()).removeView(spinnerSala);
        layout.addView(spinnerSala);

        // Ocultar botones internos del layout ya que usaremos los del AlertDialog
        View btnGuardarXml = dialogView.findViewById(R.id.btnGuardarSala);
        View btnCancelarXml = dialogView.findViewById(R.id.btnCancelarSala);
        if (btnGuardarXml != null) btnGuardarXml.setVisibility(View.GONE);
        if (btnCancelarXml != null) btnCancelarXml.setVisibility(View.GONE);

        // Cargar Salas
        RetrofitClient.getApiService().getTodasLasSalas().enqueue(new Callback<ApiResponseDto<List<SalaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<SalaResponseDto>>> call, Response<ApiResponseDto<List<SalaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SalaResponseDto> salas = response.body().getDatos();
                    ArrayAdapter<SalaResponseDto> adapterSala = new ArrayAdapter<SalaResponseDto>(getContext(), android.R.layout.simple_spinner_item, salas) {
                        @Override public View getView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            return v;
                        }
                        @Override public View getDropDownView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getDropDownView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            return v;
                        }
                    };
                    spinnerSala.setAdapter(adapterSala);
                    for (int i = 0; i < salas.size(); i++) {
                        if (salas.get(i).getNombre().equals(actividad.getSala())) {
                            spinnerSala.setSelection(i);
                            break;
                        }
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<SalaResponseDto>>> call, Throwable t) {}
        });

        // Cargar categorías
        RetrofitClient.getApiService().getCategoriasActividad().enqueue(new Callback<ApiResponseDto<List<CategoriaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Response<ApiResponseDto<List<CategoriaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoriaResponseDto> categorias = response.body().getDatos();
                    ArrayAdapter<CategoriaResponseDto> adapterCat = new ArrayAdapter<CategoriaResponseDto>(getContext(), android.R.layout.simple_spinner_item, categorias) {
                        @Override public View getView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            return v;
                        }
                        @Override public View getDropDownView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getDropDownView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            return v;
                        }
                    };
                    spinnerCategoria.setAdapter(adapterCat);
                    if (actividad.getCategoria() != null) {
                        for (int i = 0; i < categorias.size(); i++) {
                            if (categorias.get(i).getId().equals(actividad.getCategoria().getId())) {
                                spinnerCategoria.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Throwable t) {}
        });

        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle("Editar Actividad")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString();
                    String precioStr = etPrecio.getText().toString();
                    CategoriaResponseDto selectedCat = (CategoriaResponseDto) spinnerCategoria.getSelectedItem();
                    SalaResponseDto selectedSala = (SalaResponseDto) spinnerSala.getSelectedItem();

                    if (nombre.isEmpty() || precioStr.isEmpty() || selectedCat == null || selectedSala == null) {
                        Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ActividadRequestDto request = new ActividadRequestDto(
                            nombre, actividad.getDescripcion(), actividad.getDuracion(), selectedSala.getNombre(), Integer.parseInt(precioStr), selectedCat.getId()
                    );

                    RetrofitClient.getApiService().actualizarActividad(actividad.getId(), request).enqueue(new Callback<ApiResponseDto<ActividadResponseDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<ActividadResponseDto>> call, Response<ApiResponseDto<ActividadResponseDto>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Actividad actualizada", Toast.LENGTH_SHORT).show();
                                cargarActividades();
                            }
                        }
                        @Override public void onFailure(Call<ApiResponseDto<ActividadResponseDto>> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onEditClick(ActividadResponseDto actividad) {
        mostrarDialogoEditarActividad(actividad);
    }

    @Override
    public void onEliminarClick(Clase clase) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar Clase")
                .setMessage("¿Estás seguro de eliminar esta clase?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    RetrofitClient.getApiService().eliminarHorario(clase.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Clase eliminada", Toast.LENGTH_SHORT).show();
                                viewModel.cargarHorariosPorDia(diaActual);
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cargarTodosLosPerfilesComoRespaldo(android.widget.Spinner spinner) {
        RetrofitClient.getApiService().findAllPerfiles().enqueue(new Callback<ApiResponseDto<List<PerfilResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Response<ApiResponseDto<List<PerfilResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    configurarAdapterEntrenador(spinner, response.body().getDatos());
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Throwable t) {}
        });
    }

    private int getSpanCount() {
        android.util.DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float widthDp = displayMetrics.widthPixels / displayMetrics.density;
        if (widthDp >= 900) return 3; // Tablets grandes
        if (widthDp >= 600) return 2; // Tablets pequeñas / Móviles en horizontal
        return 1; // Móviles estándar
    }

    private void configurarAdapterEntrenador(android.widget.Spinner spinner, List<PerfilResponseDto> lista) {
        if (lista == null) lista = new ArrayList<>();
        ArrayAdapter<PerfilResponseDto> adapter = new ArrayAdapter<PerfilResponseDto>(getContext(), android.R.layout.simple_spinner_item, lista) {
            @Override public View getView(int p, View c, ViewGroup parent) {
                TextView v = (TextView) super.getView(p, c, parent);
                v.setText(getItem(p).getNombreCompleto());
                v.setTextColor(Color.WHITE);
                return v;
            }
            @Override public View getDropDownView(int p, View c, ViewGroup parent) {
                TextView v = (TextView) super.getDropDownView(p, c, parent);
                v.setText(getItem(p).getNombreCompleto());
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
