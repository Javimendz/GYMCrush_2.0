package com.example.skynet.ui.rutinas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.DetallePlanRequestDto;
import com.example.skynet.data.remote.dto.EntrenamientoRequestDto;
import com.example.skynet.data.remote.dto.EntrenamientoResponseDto;
import com.example.skynet.data.remote.dto.PlanResponseDto;
import com.example.skynet.data.remote.dto.RutinaRequestDto;
import com.example.skynet.data.remote.dto.RutinaResponseDto;
import com.example.skynet.ui.ejercicios.Ejercicio;
import com.example.skynet.ui.ejercicios.RepositorioEjercicios;

import com.example.skynet.data.remote.dto.CategoriaTutorialResponseDto;
import com.example.skynet.data.remote.dto.ExerciseApiRequestDto;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearRutinaActivity extends AppCompatActivity {

    private Long defaultCategoriaId = null;
    private EditText etNombreRutina;
    private TextInputEditText etBuscarBiblioteca;
    private Button btnGuardar, btnBuscarBiblioteca;
    private RecyclerView rvEjercicios;
    private EjercicioSeleccionAdapter adapter;
    private List<Ejercicio> listaEjercicios = new ArrayList<>();

    private List<PlanResponseDto> listaPlanes = new ArrayList<>();
    private Spinner spinnerPlanes;
    private CheckBox cbAnadirAPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rutina_activity_crear);

        // 1. Inicializar vistas
        etNombreRutina = findViewById(R.id.etNombreRutina);
        rvEjercicios = findViewById(R.id.rvSeleccionEjercicios);
        btnGuardar = findViewById(R.id.btnGuardarRutina);
        etBuscarBiblioteca = findViewById(R.id.etBuscarBiblioteca);
        btnBuscarBiblioteca = findViewById(R.id.btnBuscarBiblioteca);
        spinnerPlanes = findViewById(R.id.spinnerPlanes);
        cbAnadirAPlan = findViewById(R.id.cbAnadirAPlan);

        // Ocultar spinner si no se marca el checkbox
        if (cbAnadirAPlan != null) {
            cbAnadirAPlan.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (spinnerPlanes != null) spinnerPlanes.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            });
        }

        // 2. Configurar el RecyclerView con el Adapter (vacío inicialmente)
        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EjercicioSeleccionAdapter(listaEjercicios);
        rvEjercicios.setAdapter(adapter);

        // 3. Cargar datos
        cargarEjerciciosDelServidor();
        cargarCategorias();
        cargarPlanesDisponibles();

        // 4. Configurar búsqueda en biblioteca externa
        btnBuscarBiblioteca.setOnClickListener(v -> buscarEnBibliotecaExterna());

        // 5. Configurar el botón Guardar
        btnGuardar.setOnClickListener(v -> {
            guardarRutina();
        });
    }

    private void cargarPlanesDisponibles() {
        RetrofitClient.getApiService().getPlanes().enqueue(new Callback<ApiResponseDto<List<PlanResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<PlanResponseDto>>> call, Response<ApiResponseDto<List<PlanResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaPlanes = response.body().getDatos();
                    if (spinnerPlanes != null && !listaPlanes.isEmpty()) {
                        List<String> nombres = listaPlanes.stream().map(p -> p.getNombre()).collect(Collectors.toList());
                        android.widget.ArrayAdapter<String> adapterPlanes = new android.widget.ArrayAdapter<>(CrearRutinaActivity.this, android.R.layout.simple_spinner_item, nombres);
                        adapterPlanes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPlanes.setAdapter(adapterPlanes);
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<PlanResponseDto>>> call, Throwable t) {}
        });
    }

    private void cargarCategorias() {
        RetrofitClient.getApiService().getCategorias().enqueue(new Callback<ApiResponseDto<List<CategoriaTutorialResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<CategoriaTutorialResponseDto>>> call, Response<ApiResponseDto<List<CategoriaTutorialResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null && !response.body().getDatos().isEmpty()) {
                    defaultCategoriaId = response.body().getDatos().get(0).getId();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<CategoriaTutorialResponseDto>>> call, Throwable t) { }
        });
    }

    private void buscarEnBibliotecaExterna() {
        String query = etBuscarBiblioteca.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Escribe algo para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getApiService().buscarEnBiblioteca(query).enqueue(new Callback<ApiResponseDto<List<ExerciseApiRequestDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<ExerciseApiRequestDto>>> call, Response<ApiResponseDto<List<ExerciseApiRequestDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    List<ExerciseApiRequestDto> resultados = response.body().getDatos();
                    if (resultados.isEmpty()) {
                        Toast.makeText(CrearRutinaActivity.this, "No se encontraron ejercicios", Toast.LENGTH_SHORT).show();
                    } else {
                        for (ExerciseApiRequestDto dto : resultados) {
                            boolean existe = listaEjercicios.stream()
                                    .anyMatch(e -> dto.getName() != null && dto.getName().equalsIgnoreCase(e.getNombre()));
                            if (!existe) {
                                Ejercicio nuevo = new Ejercicio(
                                        null, 
                                        dto.getName(),
                                        dto.getBodyPart(),
                                        dto.getTarget() + " - " + dto.getEquipment(),
                                        "N/A",
                                        dto.getGifUrl(),
                                        R.drawable.ic_launcher_foreground,
                                        dto.getDifficulty() != null ? dto.getDifficulty() : "Media"
                                );
                                // Por defecto, los de la API externa se consideran personales del usuario si los añade
                                nuevo.setEsGlobal(false);
                                listaEjercicios.add(0, nuevo);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(CrearRutinaActivity.this, "Se añadieron " + resultados.size() + " ejercicios", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<ExerciseApiRequestDto>>> call, Throwable t) { }
        });
    }

    private void cargarEjerciciosDelServidor() {
        RepositorioEjercicios.getTodosAsync(new RepositorioEjercicios.RepositorioCallback() {
            @Override
            public void onLoaded(List<Ejercicio> ejercicios) {
                listaEjercicios.clear();
                listaEjercicios.addAll(ejercicios);
                adapter.notifyDataSetChanged();
            }
            @Override public void onError(String mensaje) { }
        });
    }

    private void guardarRutina() {
        // 1. Validaciones iniciales
        String nombreRaw = etNombreRutina.getText().toString().trim();
        if (nombreRaw.isEmpty()) {
            Toast.makeText(this, "Por favor, ponle un nombre a la rutina", Toast.LENGTH_SHORT).show();
            return;
        }

        // Normalizar: Primera letra Mayúscula, resto minúsculas
        String nombre = nombreRaw.substring(0, 1).toUpperCase() + nombreRaw.substring(1).toLowerCase();

        // 2. Filtrar ejercicios seleccionados
        List<Ejercicio> seleccionados = listaEjercicios.stream()
                .filter(Ejercicio::isSeleccionado)
                .collect(Collectors.toList());

        if (seleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Extraer IDs de Tutoriales para la relación en Base de Datos
        List<Long> tutorialesIds = seleccionados.stream()
                .map(Ejercicio::getId)
                .filter(id -> id != null)
                .collect(Collectors.toList());

        // 4. Calcular Descripción y Duración acumulada
        StringBuilder sbDescripcion = new StringBuilder();
        int acumuladorDuracion = 0;

        for (Ejercicio ej : seleccionados) {
            if (sbDescripcion.length() > 0) sbDescripcion.append(", ");
            sbDescripcion.append(ej.getNombre());

            try {
                // Extraer números de la duración (ej: "15 min" -> 15)
                String d = ej.getDuracion().replaceAll("[^0-9]", "");
                acumuladorDuracion += d.isEmpty() ? 10 : Integer.parseInt(d);
            } catch (Exception e) {
                acumuladorDuracion += 10; // Valor por defecto
            }
        }

        // --- VARIABLES FINAL/EFFECTIVELY FINAL PARA EL CALLBACK ---
        final int duracionFinal = acumuladorDuracion;
        final String descripcionFinal = sbDescripcion.toString();
        final String urlPortada = seleccionados.isEmpty() ? null : seleccionados.get(0).getUrlVideo();

        // 5. Crear una copia de los ejercicios para la rutina con el estado 'seleccionado' en false
        // ya que en el detalle de la rutina este campo se usa para marcar como COMPLETADO.
        List<Ejercicio> ejerciciosParaPersistir = new ArrayList<>();
        for (Ejercicio e : seleccionados) {
            Ejercicio copia = new Ejercicio(e.getId(), e.getNombre(), e.getCategoria(), e.getDescripcion(),
                    e.getDuracion(), e.getUrlVideo(), e.getImagenResId(), e.getDificultad());
            copia.setEsGlobal(e.isEsGlobal());
            copia.setSeries(e.getSeries());
            copia.setRepeticiones(e.getRepeticiones());
            copia.setSeleccionado(false); // Resetear para que no aparezcan como completados al inicio
            ejerciciosParaPersistir.add(copia);
        }

        // 6. Crear el Request DTO (Constructor de 10 parámetros)
        EntrenamientoRequestDto entrenamientoReq = new EntrenamientoRequestDto(
                nombre,               // nombre
                descripcionFinal,     // descripcion
                duracionFinal,        // duracion
                "MEDIA",              // intensidad
                "GENERAL",            // categoria
                tutorialesIds,        // tutorialesIds
                defaultCategoriaId,   // categoriaId
                urlPortada,           // urlVideo
                seleccionados.size(), // cantidadEjercicios
                null                  // urlImagen
        );
        // Por defecto para usuarios normales, no es global
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        java.util.Set<String> roles = prefs.getStringSet("roles", new java.util.HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        entrenamientoReq.setEsGlobal(isAdmin);

        // 7. Llamada al API para crear el Entrenamiento
        RetrofitClient.getApiService().crearEntrenamiento(entrenamientoReq).enqueue(new Callback<ApiResponseDto<EntrenamientoResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Response<ApiResponseDto<EntrenamientoResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long entrenamientoId = response.body().getDatos().getId();

                    // Guardar en el repositorio local para actualización inmediata de la UI
                    RepositorioRutinas.guardarRutinaCompletaConPortada(
                            CrearRutinaActivity.this,
                            entrenamientoId,
                            nombre,
                            ejerciciosParaPersistir,
                            urlPortada,
                            duracionFinal + " min",
                            descripcionFinal
                    );

                    // 7. Asignar la rutina recién creada al usuario actual
                    SharedPreferences prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
                    long userId = prefs.getLong("user_id", -1);

                    if (userId != -1 && cbAnadirAPlan != null && cbAnadirAPlan.isChecked()) {
                        // Si el usuario marcó la casilla, la añadimos al plan seleccionado o a la agenda
                        if (spinnerPlanes != null && spinnerPlanes.getSelectedItemPosition() >= 0 && !listaPlanes.isEmpty()) {
                            PlanResponseDto planSeleccionado = listaPlanes.get(spinnerPlanes.getSelectedItemPosition());
                            anadirAPlanMaestro(planSeleccionado.getId(), entrenamientoId);
                            Toast.makeText(CrearRutinaActivity.this, "Rutina añadida al plan: " + planSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();
                        } else {
                            // Si no hay plan seleccionado, la añadimos a la agenda de hoy
                            asignarRutinaCreadaAUsuario(userId, entrenamientoId);
                            Toast.makeText(CrearRutinaActivity.this, "Rutina añadida a tu agenda de hoy", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CrearRutinaActivity.this, "Rutina guardada en 'Mis Rutinas'", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } else {
                    Toast.makeText(CrearRutinaActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Throwable t) {
                Toast.makeText(CrearRutinaActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void anadirAPlanMaestro(Long planId, Long entrenamientoId) {
        DetallePlanRequestDto req = new DetallePlanRequestDto(entrenamientoId, 1, 10);
        List<DetallePlanRequestDto> listaReq = java.util.Collections.singletonList(req);

        RetrofitClient.getApiService().añadirEjercicioAlPlan(planId, listaReq).enqueue(new Callback<PlanResponseDto>() {
            @Override public void onResponse(Call<PlanResponseDto> call, Response<PlanResponseDto> response) { }
            @Override public void onFailure(Call<PlanResponseDto> call, Throwable t) { }
        });
    }

    private void asignarRutinaCreadaAUsuario(long userId, long entrenamientoId) {
        RutinaRequestDto request = new RutinaRequestDto(userId, entrenamientoId, LocalDate.now().toString(), 1);
        RetrofitClient.getApiService().asignarEntrenamiento(request).enqueue(new Callback<ApiResponseDto<RutinaResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<RutinaResponseDto>> call, Response<ApiResponseDto<RutinaResponseDto>> response) {
                if (response.isSuccessful()) {
                    finish();
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<RutinaResponseDto>> call, Throwable t) { }
        });
    }
}