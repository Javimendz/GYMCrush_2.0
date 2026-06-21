package com.example.skynet.ui.actividades;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.InputType;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
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
import com.example.skynet.data.remote.dto.SalaResponseDto;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadesFragment extends Fragment implements ActividadesAdapter.OnActividadClickListener {

    private RecyclerView rvActividades;
    private ChipGroup chipGroupCategorias;
    private ActividadesAdapter adapter;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddActividad;
    private SearchView searchView;
    private List<ActividadResponseDto> todasLasActividades = new ArrayList<>();
    private boolean modoGestion = false;

    public static ActividadesFragment newInstance(boolean modoGestion) {
        ActividadesFragment fragment = new ActividadesFragment();
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
        View view = inflater.inflate(R.layout.fragment_actividades, container, false);

        rvActividades = view.findViewById(R.id.rvActividades);
        chipGroupCategorias = view.findViewById(R.id.chipGroupCategorias);
        progressBar = view.findViewById(R.id.progressBar);
        fabAddActividad = view.findViewById(R.id.fabAddActividad);
        searchView = view.findViewById(R.id.searchView);

        rvActividades.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), 2));
        adapter = new ActividadesAdapter(new ArrayList<>(), this);
        rvActividades.setAdapter(adapter);

        setupSearch();

        view.findViewById(R.id.btnVolverActividades).setOnClickListener(v -> {
            if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).mostrarHome();
            } else if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
        
        if (modoGestion) {
            checkAdminRole();
            fabAddActividad.setVisibility(View.VISIBLE);
        } else {
            fabAddActividad.setVisibility(View.GONE);
        }
        
        fabAddActividad.setOnClickListener(v -> mostrarDialogoNuevaActividad());

        cargarCategoriasYActividades();

        return view;
    }

    private void cargarCategoriasYActividades() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        RetrofitClient.getApiService().getCategoriasActividad().enqueue(new Callback<ApiResponseDto<List<CategoriaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Response<ApiResponseDto<List<CategoriaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupChips(response.body().getDatos());
                }
                cargarActividades();
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Throwable t) {
                cargarActividades();
            }
        });
    }

    private void setupChips(List<CategoriaResponseDto> categorias) {
        chipGroupCategorias.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        
        // Chip "Todas"
        Chip chipTodas = (Chip) inflater.inflate(R.layout.layout_chip_filtro, chipGroupCategorias, false);
        chipTodas.setText("Todas");
        chipTodas.setCheckable(true);
        chipTodas.setChecked(true);
        chipTodas.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                adapter.setActividades(new ArrayList<>(todasLasActividades)); // Usa una copia
            }
        });
        chipGroupCategorias.addView(chipTodas);

        for (CategoriaResponseDto cat : categorias) {
            Chip chip = (Chip) inflater.inflate(R.layout.layout_chip_filtro, chipGroupCategorias, false);
            chip.setText(cat.getNombre());
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked) {
                    filtrarPorCategoria(cat);
                }
            });
            chipGroupCategorias.addView(chip);
        }
    }

    private void filtrarPorCategoria(CategoriaResponseDto categoria) {
        if (categoria == null || categoria.getNombre() == null) return;

        // Normalizamos el nombre del Chip (ej: "Cardio")
        String nombreBuscado = categoria.getNombre().trim().toLowerCase();
        List<ActividadResponseDto> filtradas = new ArrayList<>();

        for (ActividadResponseDto act : todasLasActividades) {
            // Log para ver qué tiene la actividad por dentro en el momento del fallo
            if (act.getCategoria() != null) {
                String nombreActividadCat = act.getCategoria().getNombre().trim().toLowerCase();

                android.util.Log.d("FILTRO_DETALLE", "Comparando buscado: [" + nombreBuscado + "] con categoria de la actividad: [" + nombreActividadCat + "]");

                if (nombreActividadCat.equals(nombreBuscado)) {
                    filtradas.add(act);
                }
            } else {
                android.util.Log.e("FILTRO_DETALLE", "La actividad " + act.getNombre() + " tiene la categoria NULA");
            }
        }

        adapter.setActividades(filtradas);
        rvActividades.scrollToPosition(0);

        if (filtradas.isEmpty()) {
            Toast.makeText(getContext(), "No hay actividades en: " + categoria.getNombre(), Toast.LENGTH_SHORT).show();
        }
    }
    private void checkAdminRole() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        
        if (isAdmin) {
            fabAddActividad.setVisibility(View.VISIBLE);
        } else {
            fabAddActividad.setVisibility(View.GONE);
        }
        if (adapter != null) {
            adapter.setAdminMode(isAdmin);
        }
    }

    private void setupSearch() {
        if (searchView != null) {
            // Deshabilitar sugerencias del sistema para evitar OneSearchSuggestProvider crash
            searchView.setSuggestionsAdapter(null);
            searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    buscarActividad(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.isEmpty()) cargarActividades();
                    return true;
                }
            });
        }
    }

    private void cargarActividades() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService().getTodasLasActividades().enqueue(new Callback<ApiResponseDto<List<ActividadResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<ActividadResponseDto>>> call, Response<ApiResponseDto<List<ActividadResponseDto>>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    // Actualizamos nuestra "fuente de verdad"
                    todasLasActividades = new ArrayList<>(response.body().getDatos());
                    adapter.setActividades(todasLasActividades);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<ActividadResponseDto>>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar actividades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarActividad(String nombre) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService().buscarActividadPorNombre(nombre).enqueue(new Callback<ApiResponseDto<List<ActividadResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<ActividadResponseDto>>> call, Response<ApiResponseDto<List<ActividadResponseDto>>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setActividades(response.body().getDatos());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<ActividadResponseDto>>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void mostrarDialogoNuevaActividad() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nueva_sala, null);
        android.widget.EditText etNombre = dialogView.findViewById(R.id.etNombreSala);
        android.widget.EditText etPrecio = dialogView.findViewById(R.id.etCapacidadSala);
        android.widget.Spinner spinnerCategoria = new android.widget.Spinner(getContext());
        android.widget.Spinner spinnerSala = new android.widget.Spinner(getContext());

        // Ocultar botones internos para usar los del Dialog
        View btnGuardarXml = dialogView.findViewById(R.id.btnGuardarSala);
        View btnCancelarXml = dialogView.findViewById(R.id.btnCancelarSala);
        if (btnGuardarXml != null) btnGuardarXml.setVisibility(View.GONE);
        if (btnCancelarXml != null) btnCancelarXml.setVisibility(View.GONE);

        // Ocultar título interno si existe para no duplicar con el setTitle del Builder
        View tvTitulo = dialogView.findViewById(R.id.tvTituloDialogo);
        if (tvTitulo != null) tvTitulo.setVisibility(View.GONE);

        // Configurar hints en los contenedores para evitar solapamiento
        com.google.android.material.textfield.TextInputLayout tilNombre = (com.google.android.material.textfield.TextInputLayout) etNombre.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilCapacidad = (com.google.android.material.textfield.TextInputLayout) etPrecio.getParent().getParent();
        tilNombre.setHint("Nombre de la actividad");
        tilCapacidad.setHint("Precio (€)");

        android.widget.LinearLayout layout = dialogView.findViewById(R.id.layoutContainer);
        if (layout == null) layout = (android.widget.LinearLayout) dialogView;

        android.widget.TextView tvCategoria = new android.widget.TextView(getContext());
        tvCategoria.setText("Seleccionar Categoría:");
        tvCategoria.setTextColor(android.graphics.Color.WHITE);
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 30, 0, 0);
        tvCategoria.setLayoutParams(lp);
        if (spinnerCategoria.getParent() != null) ((android.view.ViewGroup) spinnerCategoria.getParent()).removeView(spinnerCategoria);
        layout.addView(tvCategoria);
        layout.addView(spinnerCategoria);

        android.widget.TextView tvSala = new android.widget.TextView(getContext());
        tvSala.setText("Seleccionar Sala:");
        tvSala.setTextColor(android.graphics.Color.WHITE);
        android.widget.LinearLayout.LayoutParams lpSala = new android.widget.LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lpSala.setMargins(0, 30, 0, 0);
        tvSala.setLayoutParams(lpSala);
        if (spinnerSala.getParent() != null) ((android.view.ViewGroup) spinnerSala.getParent()).removeView(spinnerSala);
        layout.addView(tvSala);
        layout.addView(spinnerSala);

        // Cargar Salas
        RetrofitClient.getApiService().getTodasLasSalas().enqueue(new Callback<ApiResponseDto<List<com.example.skynet.data.remote.dto.SalaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<com.example.skynet.data.remote.dto.SalaResponseDto>>> call, Response<ApiResponseDto<List<com.example.skynet.data.remote.dto.SalaResponseDto>>> response) {
                List<Object> items = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    for (com.example.skynet.data.remote.dto.SalaResponseDto s : response.body().getDatos()) items.add(s.getNombre());
                }
                items.add(" + Nueva Sala");
                android.widget.ArrayAdapter<Object> adapterSala = new android.widget.ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
                adapterSala.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSala.setAdapter(adapterSala);
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<com.example.skynet.data.remote.dto.SalaResponseDto>>> call, Throwable t) {}
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
                android.widget.ArrayAdapter<Object> adapterCat = new android.widget.ArrayAdapter<Object>(getContext(), android.R.layout.simple_spinner_item, items) {
                    @Override
                    public View getView(int pos, View convert, ViewGroup parent) {
                        View v = super.getView(pos, convert, parent);
                        Object item = getItem(pos);
                        if (item instanceof CategoriaResponseDto) ((android.widget.TextView)v).setText(((CategoriaResponseDto)item).getNombre());
                        else if (item != null) ((android.widget.TextView)v).setText(item.toString());
                        ((android.widget.TextView)v).setTextColor(android.graphics.Color.WHITE);
                        return v;
                    }
                    @Override
                    public View getDropDownView(int pos, View convert, ViewGroup parent) {
                        View v = super.getDropDownView(pos, convert, parent);
                        Object item = getItem(pos);
                        if (item instanceof CategoriaResponseDto) ((android.widget.TextView)v).setText(((CategoriaResponseDto)item).getNombre());
                        else if (item != null) ((android.widget.TextView)v).setText(item.toString());
                        return v;
                    }
                };
                adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategoria.setAdapter(adapterCat);
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Throwable t) {}
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

                    if (selectedCat.equals(" + Nueva Categoría")) {
                        mostrarDialogoCrearCategoria((nombreCat) -> {
                            if (selectedSala.equals(" + Nueva Sala")) {
                                mostrarDialogoCrearSala((nombreSala) -> {
                                    crearActividadFinal(nombre, precioStr, nombreCat, nombreSala);
                                });
                            } else {
                                crearActividadFinal(nombre, precioStr, nombreCat, (String) selectedSala);
                            }
                        });
                    } else if (selectedSala.equals(" + Nueva Sala")) {
                        mostrarDialogoCrearSala((nombreSala) -> {
                            String catName = (selectedCat instanceof CategoriaResponseDto) ? ((CategoriaResponseDto) selectedCat).getNombre() : selectedCat.toString();
                            crearActividadFinal(nombre, precioStr, catName, nombreSala);
                        });
                    } else {
                        String catName = (selectedCat instanceof CategoriaResponseDto) ? ((CategoriaResponseDto) selectedCat).getNombre() : selectedCat.toString();
                        crearActividadFinal(nombre, precioStr, catName, (String) selectedSala);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoCrearCategoria(OnNombreRecibidoListener listener) {
        android.widget.EditText et = new android.widget.EditText(getContext());
        et.setHint("Nombre de la nueva categoría");
        et.setTextColor(Color.WHITE);
        et.setHintTextColor(Color.GRAY);
        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
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
                            @Override
                            public void onFailure(Call<ApiResponseDto<CategoriaResponseDto>> call, Throwable t) {}
                        });
                    }
                }).show();
    }

    private void mostrarDialogoCrearSala(OnNombreRecibidoListener listener) {
        android.widget.EditText et = new android.widget.EditText(getContext());
        et.setHint("Nombre de la nueva sala");
        et.setTextColor(Color.WHITE);
        et.setHintTextColor(Color.GRAY);
        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle("Crear Sala")
                .setView(et)
                .setPositiveButton("Crear", (d, w) -> {
                    String nombre = et.getText().toString();
                    if (!nombre.isEmpty()) {
                        com.example.skynet.data.remote.dto.SalaRequestDto req = new com.example.skynet.data.remote.dto.SalaRequestDto(nombre, 30, "Sala auto-creada", "Planta 1", "Básico", true);
                        RetrofitClient.getApiService().crearSala(req).enqueue(new Callback<ApiResponseDto<com.example.skynet.data.remote.dto.SalaResponseDto>>() {
                            @Override
                            public void onResponse(Call<ApiResponseDto<com.example.skynet.data.remote.dto.SalaResponseDto>> call, Response<ApiResponseDto<com.example.skynet.data.remote.dto.SalaResponseDto>> response) {
                                if (response.isSuccessful()) listener.onNombreRecibido(nombre);
                            }
                            @Override
                            public void onFailure(Call<ApiResponseDto<com.example.skynet.data.remote.dto.SalaResponseDto>> call, Throwable t) {}
                        });
                    }
                }).show();
    }

    private void mostrarDialogoEditarActividad(ActividadResponseDto actividad) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nueva_sala, null);
        android.widget.EditText etNombre = dialogView.findViewById(R.id.etNombreSala);
        android.widget.EditText etPrecio = dialogView.findViewById(R.id.etCapacidadSala);
        android.widget.Spinner spinnerCategoria = new android.widget.Spinner(getContext());
        android.widget.Spinner spinnerSala = new android.widget.Spinner(getContext());

        etNombre.setText(actividad.getNombre());
        etPrecio.setText(String.valueOf(actividad.getPrecio()));

        // Ocultar botones del layout XML que no usaremos aquí ya que usaremos los del AlertDialog
        View btnGuardarXml = dialogView.findViewById(R.id.btnGuardarSala);
        View btnCancelarXml = dialogView.findViewById(R.id.btnCancelarSala);
        if (btnGuardarXml != null) btnGuardarXml.setVisibility(View.GONE);
        if (btnCancelarXml != null) btnCancelarXml.setVisibility(View.GONE);

        // Ocultar título interno
        View tvTitulo = dialogView.findViewById(R.id.tvTituloDialogo);
        if (tvTitulo != null) tvTitulo.setVisibility(View.GONE);

        android.widget.LinearLayout layout = dialogView.findViewById(R.id.layoutContainer);
        if (layout == null) return;
        layout.setPadding(40, 20, 40, 20);

        // Configurar hints
        com.google.android.material.textfield.TextInputLayout tilNombre = (com.google.android.material.textfield.TextInputLayout) etNombre.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilPrecio = (com.google.android.material.textfield.TextInputLayout) etPrecio.getParent().getParent();
        tilNombre.setHint("Nombre de la actividad");
        tilPrecio.setHint("Precio (€)");

        android.widget.TextView tvCategoria = new android.widget.TextView(getContext());
        tvCategoria.setText("Seleccionar Categoría:");
        tvCategoria.setTextColor(android.graphics.Color.WHITE);
        tvCategoria.setPadding(0, 20, 0, 0);
        if (spinnerCategoria.getParent() != null) ((android.view.ViewGroup) spinnerCategoria.getParent()).removeView(spinnerCategoria);
        layout.addView(tvCategoria);
        layout.addView(spinnerCategoria);

        android.widget.TextView tvSala = new android.widget.TextView(getContext());
        tvSala.setText("Seleccionar Sala:");
        tvSala.setTextColor(android.graphics.Color.WHITE);
        tvSala.setPadding(0, 20, 0, 0);
        if (spinnerSala.getParent() != null) ((android.view.ViewGroup) spinnerSala.getParent()).removeView(spinnerSala);
        layout.addView(tvSala);
        layout.addView(spinnerSala);

        // Cargar Salas
        RetrofitClient.getApiService().getTodasLasSalas().enqueue(new Callback<ApiResponseDto<List<com.example.skynet.data.remote.dto.SalaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<com.example.skynet.data.remote.dto.SalaResponseDto>>> call, Response<ApiResponseDto<List<com.example.skynet.data.remote.dto.SalaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> items = new ArrayList<>();
                    int selectedIndex = -1;
                    List<com.example.skynet.data.remote.dto.SalaResponseDto> salas = response.body().getDatos();
                    for (int i = 0; i < salas.size(); i++) {
                        items.add(salas.get(i).getNombre());
                        if (salas.get(i).getNombre().equals(actividad.getSala())) selectedIndex = i;
                    }
                    android.widget.ArrayAdapter<String> adapterSala = new android.widget.ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
                    adapterSala.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSala.setAdapter(adapterSala);
                    if (selectedIndex != -1) spinnerSala.setSelection(selectedIndex);
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<com.example.skynet.data.remote.dto.SalaResponseDto>>> call, Throwable t) {}
        });

        // Cargar categorías
        RetrofitClient.getApiService().getCategoriasActividad().enqueue(new Callback<ApiResponseDto<List<CategoriaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Response<ApiResponseDto<List<CategoriaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoriaResponseDto> items = response.body().getDatos();
                    int selectedIndex = -1;
                    for (int i = 0; i < items.size(); i++) {
                        if (actividad.getCategoria() != null && items.get(i).getId().equals(actividad.getCategoria().getId())) {
                            selectedIndex = i;
                        }
                    }
                    android.widget.ArrayAdapter<CategoriaResponseDto> adapterCat = new android.widget.ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
                    adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategoria.setAdapter(adapterCat);
                    if (selectedIndex != -1) spinnerCategoria.setSelection(selectedIndex);
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Throwable t) {}
        });

        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle("Editar Actividad")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString();
                    String precioStr = etPrecio.getText().toString();
                    CategoriaResponseDto selectedCat = (CategoriaResponseDto) spinnerCategoria.getSelectedItem();
                    String selectedSala = (String) spinnerSala.getSelectedItem();

                    if (nombre.isEmpty() || precioStr.isEmpty() || selectedCat == null || selectedSala == null) {
                        Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ActividadRequestDto request = new ActividadRequestDto(
                            nombre,
                            actividad.getDescripcion(),
                            actividad.getDuracion(),
                            selectedSala,
                            Integer.parseInt(precioStr),
                            selectedCat.getId()
                    );

                    RetrofitClient.getApiService().actualizarActividad(actividad.getId(), request).enqueue(new Callback<ApiResponseDto<ActividadResponseDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<ActividadResponseDto>> call, Response<ApiResponseDto<ActividadResponseDto>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Actividad actualizada", Toast.LENGTH_SHORT).show();
                                cargarActividades();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponseDto<ActividadResponseDto>> call, Throwable t) {
                            Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearActividadFinal(String nombre, String precioStr, String nombreCat, String nombreSala) {
        RetrofitClient.getApiService().getCategoriasActividad().enqueue(new Callback<ApiResponseDto<List<CategoriaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Response<ApiResponseDto<List<CategoriaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long catId = null;
                    for (CategoriaResponseDto c : response.body().getDatos()) {
                        if (c.getNombre().equalsIgnoreCase(nombreCat)) {
                            catId = c.getId();
                            break;
                        }
                    }
                    if (catId != null) {
                        ActividadRequestDto request = new ActividadRequestDto(nombre, "Actividad de " + nombreCat, 60, nombreSala, Integer.parseInt(precioStr), catId);
                        RetrofitClient.getApiService().crearActividad(request).enqueue(new Callback<ApiResponseDto<ActividadResponseDto>>() {
                            @Override
                            public void onResponse(Call<ApiResponseDto<ActividadResponseDto>> call, Response<ApiResponseDto<ActividadResponseDto>> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Actividad creada con éxito", Toast.LENGTH_SHORT).show();
                                    cargarActividades();
                                }
                            }
                            @Override
                            public void onFailure(Call<ApiResponseDto<ActividadResponseDto>> call, Throwable t) {}
                        });
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<CategoriaResponseDto>>> call, Throwable t) {}
        });
    }

    private void mostrarDialogoNuevoHorario(ActividadResponseDto actividad) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nuevo_horario, null);
        Spinner spinnerDia = view.findViewById(R.id.spinnerDiaSemana);
        Spinner spinnerSala = view.findViewById(R.id.spinnerSala);
        Spinner spinnerEntrenador = view.findViewById(R.id.spinnerEntrenador);
        Button btnHoraInicio = view.findViewById(R.id.btnHoraInicio);
        Button btnHoraFin = view.findViewById(R.id.btnHoraFin);
        EditText etAforo = view.findViewById(R.id.etAforoMax);

        // Ocultar botones internos
        View btnCrearXml = view.findViewById(R.id.btnCrearHorario);
        View btnCancelarXml = view.findViewById(R.id.btnCancelarHorario);
        if (btnCrearXml != null) btnCrearXml.setVisibility(View.GONE);
        if (btnCancelarXml != null) btnCancelarXml.setVisibility(View.GONE);

        // 1. Configurar Spinner Días
        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};
        ArrayAdapter<String> adapterDias = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, dias) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((android.widget.TextView) v).setTextColor(Color.WHITE);
                return v;
            }
        };
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDia.setAdapter(adapterDias);

        // 2. Cargar Salas
        RetrofitClient.getApiService().getSalasActivas().enqueue(new Callback<ApiResponseDto<List<SalaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<SalaResponseDto>>> call, Response<ApiResponseDto<List<SalaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SalaResponseDto> salas = response.body().getDatos();
                    ArrayAdapter<SalaResponseDto> adapterSalas = new ArrayAdapter<SalaResponseDto>(requireContext(), android.R.layout.simple_spinner_item, salas) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);
                            ((android.widget.TextView) v).setText(getItem(position).getNombre());
                            ((android.widget.TextView) v).setTextColor(Color.WHITE);
                            return v;
                        }
                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View v = super.getDropDownView(position, convertView, parent);
                            ((android.widget.TextView) v).setText(getItem(position).getNombre());
                            return v;
                        }
                    };
                    spinnerSala.setAdapter(adapterSalas);
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<SalaResponseDto>>> call, Throwable t) {}
        });

        // 2.1 Cargar Entrenadores (Perfiles)
        RetrofitClient.getApiService().findAllPerfiles().enqueue(new Callback<ApiResponseDto<List<PerfilResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Response<ApiResponseDto<List<PerfilResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PerfilResponseDto> perfiles = response.body().getDatos();
                    ArrayAdapter<PerfilResponseDto> adapterPerf = new ArrayAdapter<PerfilResponseDto>(requireContext(), android.R.layout.simple_spinner_item, perfiles) {
                        @Override
                        public View getView(int pos, View v, ViewGroup p) {
                            View view = super.getView(pos, v, p);
                            ((android.widget.TextView)view).setText(getItem(pos).getNombreCompleto());
                            ((android.widget.TextView)view).setTextColor(Color.WHITE);
                            return view;
                        }
                        @Override
                        public View getDropDownView(int pos, View v, ViewGroup p) {
                            View view = super.getDropDownView(pos, v, p);
                            ((android.widget.TextView)view).setText(getItem(pos).getNombreCompleto());
                            return view;
                        }
                    };
                    spinnerEntrenador.setAdapter(adapterPerf);
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Throwable t) {}
        });

        // 3. Time Pickers
        final String[] horaInicio = {"08:00"};
        final String[] horaFin = {"09:00"};

        btnHoraInicio.setOnClickListener(v -> {
            new TimePickerDialog(getContext(), (tp, h, m) -> {
                horaInicio[0] = String.format(java.util.Locale.getDefault(), "%02d:%02d", h, m);
                btnHoraInicio.setText("Inicio: " + horaInicio[0]);
            }, 8, 0, true).show();
        });

        btnHoraFin.setOnClickListener(v -> {
            new TimePickerDialog(getContext(), (tp, h, m) -> {
                horaFin[0] = String.format(java.util.Locale.getDefault(), "%02d:%02d", h, m);
                btnHoraFin.setText("Fin: " + horaFin[0]);
            }, 9, 0, true).show();
        });

        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle("Programar " + actividad.getNombre())
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    String dia = spinnerDia.getSelectedItem().toString();
                    SalaResponseDto sala = (SalaResponseDto) spinnerSala.getSelectedItem();
                    PerfilResponseDto entrenador = (PerfilResponseDto) spinnerEntrenador.getSelectedItem();
                    String aforoStr = etAforo.getText().toString();

                    if (sala == null || entrenador == null || aforoStr.isEmpty()) {
                        Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    HorarioRequestDto request = new HorarioRequestDto(
                            dia, horaInicio[0], horaFin[0], 
                            Integer.parseInt(aforoStr), actividad.getId(), sala.getId(), entrenador.getId()
                    );

                    RetrofitClient.getApiService().crearHorario(request).enqueue(new Callback<ApiResponseDto<HorarioResponseDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<HorarioResponseDto>> call, Response<ApiResponseDto<HorarioResponseDto>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Horario creado con éxito", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = "Error al crear horario";
                                try {
                                    String errorBody = response.errorBody().string();
                                    com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(errorBody).getAsJsonObject();
                                    if (json.has("mensaje")) {
                                        errorMsg = json.get("mensaje").getAsString();
                                    }
                                } catch (Exception ignored) {}
                                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override public void onFailure(Call<ApiResponseDto<HorarioResponseDto>> call, Throwable t) {
                            Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public interface OnNombreRecibidoListener {
        void onNombreRecibido(String nombre);
    }

    @Override
    public void onEditClick(ActividadResponseDto actividad) {
        mostrarDialogoEditarActividad(actividad);
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

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle(actividad.getNombre())
                .setMessage(mensaje)
                .setPositiveButton("Cerrar", null);

        if (modoGestion) {
            // Vista Gestión: Editar, Eliminar y Añadir Horario
            builder.setNeutralButton("Eliminar", (dialog, which) -> {
                new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
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

            builder.setItems(new String[]{"Añadir Horario / Sesión"}, (dialog, which) -> {
                mostrarDialogoNuevoHorario(actividad);
            });
        } else {
            // Vista Usuario: Botón Reservar prominente
            builder.setNegativeButton("Reservar", (dialog, which) -> {
                mostrarDialogoVerHorarios(actividad);
            });
            
            builder.setNeutralButton("Mis Reservas", (dialog, which) -> {
                mostrarDialogoMisReservas();
            });
        }

        builder.show();
    }

    private void mostrarDialogoVerHorarios(ActividadResponseDto actividad) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        RetrofitClient.getApiService().getTodosLosHorarios().enqueue(new Callback<ApiResponseDto<List<HorarioResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<HorarioResponseDto>>> call, Response<ApiResponseDto<List<HorarioResponseDto>>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<HorarioResponseDto> horariosFiltrados = new ArrayList<>();
                    for (HorarioResponseDto h : response.body().getDatos()) {
                        if (h.getActividadId() != null && h.getActividadId().equals(actividad.getId())) {
                            horariosFiltrados.add(h);
                        }
                    }
                    
                    if (horariosFiltrados.isEmpty()) {
                        Toast.makeText(getContext(), "No hay horarios programados para esta actividad", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] items = new String[horariosFiltrados.size()];
                    for (int i = 0; i < horariosFiltrados.size(); i++) {
                        HorarioResponseDto h = horariosFiltrados.get(i);
                        items[i] = h.getDiaSemana() + " " + h.getHoraInicio() + " - " + h.getNombreSala() + 
                                   " (Plazas: " + h.getPlazasLibres() + " / " + h.getAforoMax() + ")";
                    }

                    new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                            .setTitle("Horarios disponibles: " + actividad.getNombre())
                            .setItems(items, (dialog, which) -> {
                                HorarioResponseDto seleccionado = horariosFiltrados.get(which);
                                realizarReserva(seleccionado);
                            })
                            .setNegativeButton("Cerrar", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<HorarioResponseDto>>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar horarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void realizarReserva(HorarioResponseDto horario) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Long usuarioId = prefs.getLong("user_id", -1L);

        if (usuarioId == -1L) {
            Toast.makeText(getContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getApiService().crearReserva(usuarioId, horario.getId()).enqueue(new Callback<ApiResponseDto<ReservaResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<ReservaResponseDto>> call, Response<ApiResponseDto<ReservaResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMensaje(), Toast.LENGTH_LONG).show();
                } else {
                    String errorMsg = "Error al reservar";
                    try {
                        String errorBody = response.errorBody().string();
                        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(errorBody).getAsJsonObject();
                        if (json.has("mensaje")) {
                            errorMsg = json.get("mensaje").getAsString();
                        }
                    } catch (Exception e) {
                        if (response.message() != null && !response.message().isEmpty()) {
                            errorMsg += ": " + response.message();
                        }
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<ReservaResponseDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoMisReservas() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Long usuarioId = prefs.getLong("user_id", -1L);

        if (usuarioId == -1L) {
            Toast.makeText(getContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService().getMisReservas(usuarioId).enqueue(new Callback<ApiResponseDto<List<ReservaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<ReservaResponseDto>>> call, Response<ApiResponseDto<List<ReservaResponseDto>>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<ReservaResponseDto> reservas = response.body().getDatos();
                    
                    if (reservas == null || reservas.isEmpty()) {
                        Toast.makeText(getContext(), "No tienes reservas activas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] items = new String[reservas.size()];
                    for (int i = 0; i < reservas.size(); i++) {
                        ReservaResponseDto r = reservas.get(i);
                        String info = r.getNombreActividad() + "\n" + r.getFecha() + " (" + r.getDiaSemana() + ") a las " + r.getHoraInicio();
                        if ("ASISTIDA".equalsIgnoreCase(r.getEstado())) {
                            info += " [✓ ASISTIDO]";
                        } else {
                            info += " [" + r.getEstado() + "]";
                        }
                        items[i] = info;
                    }

                    new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                            .setTitle("Mis Reservas")
                            .setItems(items, (dialog, which) -> {
                                ReservaResponseDto seleccionada = reservas.get(which);
                                mostrarOpcionesReserva(seleccionada);
                            })
                            .setNegativeButton("Cerrar", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<ReservaResponseDto>>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar reservas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarOpcionesReserva(ReservaResponseDto reserva) {
        List<String> opciones = new ArrayList<>();
        if (!"CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
            opciones.add("Cancelar Reserva");
            if (!"ASISTIDA".equalsIgnoreCase(reserva.getEstado())) {
                opciones.add("Confirmar Asistencia (Check-in)");
            }
        }

        if (opciones.isEmpty()) {
            Toast.makeText(getContext(), "Reserva finalizada", Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle(reserva.getNombreActividad())
                .setItems(opciones.toArray(new String[0]), (dialog, which) -> {
                    String opcion = opciones.get(which);
                    if (opcion.equals("Cancelar Reserva")) {
                        confirmarCancelacion(reserva);
                    } else if (opcion.equals("Confirmar Asistencia (Check-in)")) {
                        realizarCheckIn(reserva);
                    }
                })
                .show();
    }

    private void realizarCheckIn(ReservaResponseDto reserva) {
        RetrofitClient.getApiService().confirmarAsistencia(reserva.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    String msg = (response.body() != null && response.body().getMensaje() != null) 
                                 ? response.body().getMensaje() : "¡Asistencia confirmada!";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                } else {
                    String errorMsg = "No se pudo confirmar la asistencia";
                    try {
                        String errorBody = response.errorBody().string();
                        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(errorBody).getAsJsonObject();
                        if (json.has("mensaje")) {
                            errorMsg = json.get("mensaje").getAsString();
                        }
                    } catch (Exception ignored) {}
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarCancelacion(ReservaResponseDto reserva) {
        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle("Cancelar Reserva")
                .setMessage("¿Estás seguro de que deseas cancelar tu reserva para " + reserva.getNombreActividad() + "?\n\nRecuerda: Solo puedes cancelar hasta 2 horas antes.")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    RetrofitClient.getApiService().cancelarReserva(reserva.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                            if (response.isSuccessful()) {
                                String msg = (response.body() != null && response.body().getMensaje() != null) 
                                             ? response.body().getMensaje() : "Reserva cancelada";
                                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                cargarActividades(); // Refrescar si es necesario
                            } else {
                                String errorMsg = "No se pudo cancelar la reserva";
                                try {
                                    String errorBody = response.errorBody().string();
                                    com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(errorBody).getAsJsonObject();
                                    if (json.has("mensaje")) {
                                        errorMsg = json.get("mensaje").getAsString();
                                    }
                                } catch (Exception ignored) {}
                                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                            Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }
}
