package com.example.skynet.ui.dieta;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.ui.main.DesarrolloActivity;
import com.example.skynet.ui.custom.CustomDonutChart;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.example.skynet.data.remote.dto.ComidaDiariaResponseDto;
import com.example.skynet.data.remote.dto.NutricionResponseDto;
import com.example.skynet.data.remote.dto.PerfilFisicoRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class DietaFragment extends Fragment {

    private DietaViewModel viewModel;
    private TextInputEditText etPeso, etAltura, etEdad;
    private MaterialAutoCompleteTextView spinnerActividad, spinnerObjetivo;
    private MaterialButton btnActualizar, btnAddComida, btnBuscarComida;

    private MaterialCardView cardEmptySuggestion, cardSuggestion;
    private TextView tvNombreSugerida, tvCaloriasSugeridas, tvCaloriasRestantes, tvProt, tvCarbs, tvGrasas, tvTituloRecetas;
    private CustomDonutChart donutChart;
    private LinearProgressIndicator progressCalorias;

    private RecyclerView rvCatalogo, rvHistorial, rvComidas, rvRecetas;
    private DietaAdapter adapter;
    private HistorialNutricionAdapter historialAdapter;
    private ComidaAdapter comidaAdapter;
    private RecetaAdapter recetaAdapter;
    private Long usuarioId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dieta, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(DietaViewModel.class);

        // Unificamos la obtención del ID de usuario
        SharedPreferences prefs = requireActivity().getSharedPreferences("GymCrushPrefs", Context.MODE_PRIVATE);
        usuarioId = prefs.getLong("usuarioId", -1L);
        if (usuarioId == -1L) {
            // Reintento con el otro nombre por si acaso
            prefs = requireActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
            usuarioId = prefs.getLong("user_id", -1L);
        }

        initViews(view);
        setupRecyclerViews();
        setupObservers();
        configurarDropdowns();

        cardSuggestion.setOnClickListener(v -> {
            NutricionResponseDto plan = viewModel.getPlanActual().getValue();
            if (plan != null) {
                ResultadoDietaFragment fragment = ResultadoDietaFragment.newInstance(plan);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (usuarioId != -1L) {
            android.util.Log.d("DietaFragment", "Cargando datos para usuario: " + usuarioId);
            viewModel.cargarDatosIniciales(usuarioId);
        } else {
            android.util.Log.e("DietaFragment", "No se encontró ID de usuario en SharedPreferences");
        }

        btnActualizar.setOnClickListener(v -> {
            generarSugerencia();
        });
        btnAddComida.setOnClickListener(v -> mostrarDialogoAñadirComida());
        btnBuscarComida.setOnClickListener(v -> mostrarDialogoBusqueda());

        view.findViewById(R.id.btnBackDieta).setOnClickListener(v -> {
            if (getActivity() instanceof DesarrolloActivity) {
                ((DesarrolloActivity) getActivity()).mostrarHome();
            } else if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String section = getArguments().getString("scrollToSection");
            boolean mostrarHistorial = getArguments().getBoolean("mostrarHistorial", false);

            if (mostrarHistorial || "historial".equals(section)) {
                rvHistorial.post(() -> {
                    View header = view.findViewById(R.id.rvHistorialNutricion);
                    if (header != null) header.getParent().requestChildFocus(header, header);
                });
            }
        }
    }

    public com.example.skynet.data.remote.dto.NutricionResponseDto getPlanActual() {
        return viewModel.getPlanActual().getValue();
    }

    private void initViews(View v) {
        etPeso = v.findViewById(R.id.etPeso);
        etAltura = v.findViewById(R.id.etAltura);
        etEdad = v.findViewById(R.id.etEdad);
        spinnerActividad = v.findViewById(R.id.spinnerActividad);
        spinnerObjetivo = v.findViewById(R.id.spinnerObjetivo);
        btnActualizar = v.findViewById(R.id.btnActualizarDatos);
        btnAddComida = v.findViewById(R.id.btnAddComidaManual);
        btnBuscarComida = v.findViewById(R.id.btnBuscarComida);

        cardEmptySuggestion = v.findViewById(R.id.cardEmptySuggestion);
        cardSuggestion = v.findViewById(R.id.cardSuggestion);
        tvNombreSugerida = v.findViewById(R.id.tvNombreDietaSugerida);
        tvCaloriasSugeridas = v.findViewById(R.id.tvCaloriasSugeridas);
        tvCaloriasRestantes = v.findViewById(R.id.tvCaloriasRestantes);
        tvProt = v.findViewById(R.id.tvProt);
        tvCarbs = v.findViewById(R.id.tvCarbs);
        tvGrasas = v.findViewById(R.id.tvGrasas);
        donutChart = v.findViewById(R.id.donutChartMacros);
        progressCalorias = v.findViewById(R.id.progressCalorias);

        rvCatalogo = v.findViewById(R.id.rvCatalogoDietas);
        rvHistorial = v.findViewById(R.id.rvHistorialNutricion);
        rvComidas = v.findViewById(R.id.rvComidasSugeridas);
        rvRecetas = v.findViewById(R.id.rvRecetasSugeridas);
        tvTituloRecetas = v.findViewById(R.id.tvTituloRecetas);
    }

    private void setupRecyclerViews() {
        adapter = new DietaAdapter(dieta -> {
            // Convertimos la dieta del catálogo a un formato compatible con la vista de resultados
            NutricionResponseDto planSimulado = new NutricionResponseDto();
            planSimulado.setNombreDieta(dieta.getNombre());
            planSimulado.setTipoDieta(dieta.getTipo());
            
            double totalCal = (dieta.getObjetivoCalorico() != null) ? dieta.getObjetivoCalorico() : 2000.0;
            planSimulado.setCaloriasObjetivo((int) totalCal);
            
            // Inicializar listas para evitar NullPointerException en el fragmento de destino
            planSimulado.setComidas(new java.util.ArrayList<>());

            // Simulamos las comidas diarias recomendadas basándonos en los macros de la dieta
            java.util.List<ComidaDiariaResponseDto> dietasSimuladas = new java.util.ArrayList<>();

            String[] momentos = {"DESAYUNO", "ALMUERZO", "COMIDA", "MERIENDA", "CENA"};
            
            String[] platosEjemplo;
            String tipoNorm = dieta.getTipo() != null ? dieta.getTipo().toUpperCase() : "";
            
            if (tipoNorm.contains("HIPERCAL") || tipoNorm.contains("VOLUMEN") || tipoNorm.contains("GANAR")) {
                platosEjemplo = new String[]{
                    "Gachas de Avena con Plátano y Crema de Cacahuete",
                    "Sándwich de Pollo, Aguacate y Queso",
                    "Pasta Integral con Ternera y Salsa de Tomate",
                    "Batido de Proteínas con Leche Entera y Frutos Secos",
                    "Salmón al Horno con Patata Cocida y Verduras"
                };
            } else if (tipoNorm.contains("HIPOCAL") || tipoNorm.contains("DEFICIT") || tipoNorm.contains("PERDER")) {
                platosEjemplo = new String[]{
                    "Tortilla de Claras con Espinacas",
                    "Yogur Griego 0% con Arándanos",
                    "Pechuga de Pollo a la Plancha con Brócoli",
                    "Manzana con un puñado de Almendras",
                    "Pescado Blanco al Papillote con Calabacín"
                };
            } else {
                platosEjemplo = new String[]{
                    "Bol de Avena y Frutas", 
                    "Sándwich Integral de Pavo", 
                    "Pollo con Arroz y Verduras", 
                    "Batido de Proteínas y Nueces", 
                    "Salmón a la Plancha con Ensalada"
                };
            }

            String[] imagenesEjemplo = {
                    "https://images.unsplash.com/photo-1517673400267-0251440c45dc?q=80&w=500&auto=format&fit=crop", // Avena
                    "https://images.unsplash.com/photo-1528733918455-5a59687cedf0?q=80&w=500&auto=format&fit=crop", // Sandwich
                    "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?q=80&w=500&auto=format&fit=crop", // Pollo/Pasta
                    "https://images.unsplash.com/photo-1574783756247-29f6c244a51d?q=80&w=500&auto=format&fit=crop", // Batido
                    "https://images.unsplash.com/photo-1467003909585-2f8a72700288?q=80&w=500&auto=format&fit=crop"  // Salmon/Pescado
            };

            double protTotal = (dieta.getCantidadProteinas() != null) ? dieta.getCantidadProteinas() : 0.0;
            double carbTotal = (dieta.getCantidadCarbohidratos() != null) ? dieta.getCantidadCarbohidratos() : 0.0;
            double grasTotal = (dieta.getCantidadGrasas() != null) ? dieta.getCantidadGrasas().doubleValue() : 0.0;

            for (int i = 0; i < momentos.length; i++) {
                String momento = momentos[i];
                ComidaDiariaResponseDto item = new ComidaDiariaResponseDto();
                item.setMomento(momento);
                item.setNombreAlimento(platosEjemplo[i]);
                item.setImagenUrl(imagenesEjemplo[i]);
                item.setDescripcion("Sugerencia de dieta " + (dieta.getTipo() != null ? dieta.getTipo().toLowerCase() : "") + " para alcanzar tus " + (int)totalCal + " kcal diarias.");

                // Repartimos macros exactos del catálogo entre las 5 comidas
                item.setProteina(protTotal / 5.0);
                item.setCarbohidratos(carbTotal / 5.0);
                item.setGrasas(grasTotal / 5.0);
                item.setCalorias(totalCal / 5.0);
                dietasSimuladas.add(item);
            }
            planSimulado.setDietas(dietasSimuladas);

            // Pasamos a la pantalla de detalles para que se pueda editar y guardar
            ResultadoDietaFragment fragment = ResultadoDietaFragment.newInstance(planSimulado);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvCatalogo.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCatalogo.setAdapter(adapter);

        historialAdapter = new HistorialNutricionAdapter(plan -> {
            ResultadoDietaFragment fragment = ResultadoDietaFragment.newInstance(plan);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvHistorial.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvHistorial.setAdapter(historialAdapter);
        rvHistorial.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == historialAdapter.getItemCount() - 1) {
                    viewModel.cargarHistorial(usuarioId);
                }
            }
        });

        comidaAdapter = new ComidaAdapter();
        comidaAdapter.setOnMenuClickListener((v, comida) -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(getContext(), v);
            if (comida.getId() != null) {
                popup.getMenu().add("Eliminar");
            } else {
                popup.getMenu().add("Añadir al registro");
            }

            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Eliminar")) {
                    NutricionResponseDto currentPlan = viewModel.getPlanActual().getValue();
                    Long planId = (currentPlan != null) ? currentPlan.getId() : null;
                    viewModel.eliminarComida(comida.getId(), planId, usuarioId);
                } else if (item.getTitle().equals("Añadir al registro")) {
                    NutricionResponseDto plan = viewModel.getPlanActual().getValue();
                    if (plan != null) {
                        com.example.skynet.data.remote.dto.ComidaDiariaRequestDto request = new com.example.skynet.data.remote.dto.ComidaDiariaRequestDto();
                        request.setNombreAlimento(comida.getNombreAlimento());
                        request.setMomento(comida.getMomento());
                        request.setCalorias(comida.getCalorias());
                        request.setProteina(comida.getProteina());
                        request.setCarbohidratos(comida.getCarbohidratos());
                        request.setGrasas(comida.getGrasas());
                        request.setPlanNutricionalId(plan.getId());
                        request.setImagenUrl(comida.getImagenUrl());
                        request.setDescripcion(comida.getDescripcion());
                        request.setRecipeUrl(comida.getRecipeUrl());
                        viewModel.añadirComida(request, usuarioId);
                    }
                }
                return true;
            });
            popup.show();
        });
        comidaAdapter.setOnComidaClickListener(comida -> {
            // Intentar enriquecer con descripción e imagen del plan actual
            NutricionResponseDto plan = viewModel.getPlanActual().getValue();
            if (plan != null && plan.getMacronutrientes() != null &&
                    plan.getMacronutrientes().getRecipes() != null) {
                for (com.example.skynet.data.remote.dto.FatSecretRecipeDto recipe :
                        plan.getMacronutrientes().getRecipes().getRecipe()) {
                    if (recipe.getRecipeName().trim().equalsIgnoreCase(comida.getNombreAlimento().trim())) {
                        comida.setDescripcion(recipe.getRecipeDescription());
                        comida.setRecipeUrl(recipe.getRecipeUrl());
                        if (recipe.getRecipeImage() != null) {
                            comida.setImagenUrl(recipe.getRecipeImage());
                        }
                        break;
                    }
                }
            }
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, DetalleComidaFragment.newInstance(comida))
                    .addToBackStack(null)
                    .commit();
        });
        rvComidas.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvComidas.setAdapter(comidaAdapter);

        recetaAdapter = new RecetaAdapter(receta -> {
            com.example.skynet.data.remote.dto.ComidaDiariaResponseDto tempComida = new com.example.skynet.data.remote.dto.ComidaDiariaResponseDto();
            tempComida.setNombreAlimento(receta.getRecipeName());
            tempComida.setDescripcion(receta.getRecipeDescription());
            tempComida.setRecipeUrl(receta.getRecipeUrl());
            tempComida.setImagenUrl(receta.getRecipeImage());
            tempComida.setMomento("RECETA RECOMENDADA");

            if (receta.getRecipeNutrition() != null) {
                try {
                    tempComida.setCalorias(Double.parseDouble(receta.getRecipeNutrition().getCalories()));
                    tempComida.setProteina(Double.parseDouble(receta.getRecipeNutrition().getProtein()));
                    tempComida.setCarbohidratos(Double.parseDouble(receta.getRecipeNutrition().getCarbohydrate()));
                    tempComida.setGrasas(Double.parseDouble(receta.getRecipeNutrition().getFat()));
                } catch (Exception ignored) {}
            }

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, DetalleComidaFragment.newInstance(tempComida))
                    .addToBackStack(null)
                    .commit();
        });
        rvRecetas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvRecetas.setAdapter(recetaAdapter);
    }

    private void setupObservers() {
        viewModel.getPlanActual().observe(getViewLifecycleOwner(), plan -> {
            if (plan != null) {
                cardEmptySuggestion.setVisibility(View.GONE);
                cardSuggestion.setVisibility(View.VISIBLE);

                // IMPORTANTE: Esta función es la que pinta la gráfica y los gramos
                mostrarPlan(plan);
                actualizarRecetas();
            } else {
                cardEmptySuggestion.setVisibility(View.VISIBLE);
                cardSuggestion.setVisibility(View.GONE);
            }
        });

        viewModel.getSaludActual().observe(getViewLifecycleOwner(), salud -> {
            if (salud != null) {
                if (salud.getPeso() != null) etPeso.setText(String.valueOf(salud.getPeso()));
                if (salud.getEstatura() != null) etAltura.setText(String.valueOf(salud.getEstatura()));
                if (salud.getNivelActividad() != null) {
                    spinnerActividad.setText(salud.getNivelActividad(), false);
                }
            }
        });

        viewModel.getDietasCatalogo().observe(getViewLifecycleOwner(), dietas -> {
            if (dietas != null) {
                adapter.setDietas(dietas);
            }
        });

        viewModel.getHistorial().observe(getViewLifecycleOwner(), historial -> {
            if (historial != null) {
                historialAdapter.setHistorial(historial);
                // Si el plan actual no tenía recetas, al cargar el historial puede que ahora sí encontremos
                actualizarRecetas();
            }
        });

        viewModel.getPlanGeneradoEvent().observe(getViewLifecycleOwner(), plan -> {
            if (plan != null) {
                viewModel.consumirEventoPlan();
                ResultadoDietaFragment fragment = ResultadoDietaFragment.newInstance(plan);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                if (!error.contains("404") && !error.contains("no tiene registros") && !error.contains("No se encontraron datos de salud")) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarRecetas() {
        NutricionResponseDto plan = viewModel.getPlanActual().getValue();
        if (plan == null) {
            rvRecetas.setVisibility(View.GONE);
            tvTituloRecetas.setVisibility(View.GONE);
            return;
        }

        // 1. Intentar con recetas del plan actual
        if (plan.getMacronutrientes() != null &&
                plan.getMacronutrientes().getRecipes() != null &&
                plan.getMacronutrientes().getRecipes().getRecipe() != null &&
                !plan.getMacronutrientes().getRecipes().getRecipe().isEmpty()) {

            recetaAdapter.setRecetas(plan.getMacronutrientes().getRecipes().getRecipe());
            rvRecetas.setVisibility(View.VISIBLE);
            tvTituloRecetas.setVisibility(View.VISIBLE);
            return;
        }

        // 2. Si no hay, buscar en el historial (fallback)
        java.util.List<NutricionResponseDto> hist = viewModel.getHistorial().getValue();
        if (hist != null) {
            for (NutricionResponseDto p : hist) {
                if (p.getMacronutrientes() != null &&
                        p.getMacronutrientes().getRecipes() != null &&
                        p.getMacronutrientes().getRecipes().getRecipe() != null &&
                        !p.getMacronutrientes().getRecipes().getRecipe().isEmpty()) {
                    recetaAdapter.setRecetas(p.getMacronutrientes().getRecipes().getRecipe());
                    rvRecetas.setVisibility(View.VISIBLE);
                    tvTituloRecetas.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }

        // 3. Si nada funciona, ocultar sección
        recetaAdapter.setRecetas(new java.util.ArrayList<>());
        rvRecetas.setVisibility(View.GONE);
        tvTituloRecetas.setVisibility(View.GONE);
    }

    private void mostrarPlan(NutricionResponseDto plan) {


        String titulo = plan.getNombreDieta() != null && !plan.getNombreDieta().isEmpty()
                ? plan.getNombreDieta()
                : (plan.getTipoDieta() != null ? plan.getTipoDieta() : "PLAN PERSONALIZADO");
        tvNombreSugerida.setText(titulo);

        // Lista combinada para mostrar tanto lo sugerido como lo consumido
        java.util.List<com.example.skynet.data.remote.dto.ComidaDiariaResponseDto> todasLasComidas = new java.util.ArrayList<>();

        // 1. Añadimos las sugerencias del plan (si ya están consumidas, usamos el objeto real para tener el ID)
        if (plan.getDietas() != null) {
            for (com.example.skynet.data.remote.dto.ComidaDiariaResponseDto sugerencia : plan.getDietas()) {
                com.example.skynet.data.remote.dto.ComidaDiariaResponseDto itemMostrar = sugerencia;
                if (plan.getComidas() != null) {
                    for (com.example.skynet.data.remote.dto.ComidaDiariaResponseDto real : plan.getComidas()) {
                        if (real.getNombreAlimento() != null && sugerencia.getNombreAlimento() != null &&
                                real.getNombreAlimento().equalsIgnoreCase(sugerencia.getNombreAlimento()) &&
                                real.getMomento() != null && sugerencia.getMomento() != null &&
                                real.getMomento().equalsIgnoreCase(sugerencia.getMomento()) &&
                                (real.getPlanNutricionalId() == null || real.getPlanNutricionalId().equals(plan.getId()))) {
                            itemMostrar = real;
                            itemMostrar.setSelected(true); // Marcar como consumida
                            break;
                        }
                    }
                }
                todasLasComidas.add(itemMostrar);
            }
        }

        // 2. Añadimos comidas extras que no estaban en las sugerencias
        if (plan.getComidas() != null) {
            for (com.example.skynet.data.remote.dto.ComidaDiariaResponseDto real : plan.getComidas()) {
                if (real.getPlanNutricionalId() != null && !real.getPlanNutricionalId().equals(plan.getId())) {
                    continue;
                }
                boolean yaEsta = false;
                for (com.example.skynet.data.remote.dto.ComidaDiariaResponseDto m : todasLasComidas) {
                    if (real.getId() != null && real.getId().equals(m.getId())) {
                        yaEsta = true;
                        break;
                    }
                }
                if (!yaEsta) {
                    real.setSelected(true);
                    todasLasComidas.add(real);
                }
            }
        }

        comidaAdapter.setComidas(todasLasComidas);
        actualizarResumenMacros(plan);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Cada vez que el usuario vuelve a esta pantalla (por ejemplo, después de guardar)
        // cargamos el último plan oficial de la base de datos.
        if (usuarioId != -1L) {
            viewModel.cargarUltimoPlan(usuarioId);
            // También refrescamos el historial por si el nombre cambió
            viewModel.reiniciarPaginacionHistorial(usuarioId);
        }
    }
    private void actualizarResumenMacros(NutricionResponseDto plan) {
        if (plan == null) return;

        double p = 0, c = 0, g = 0, calConsumidas = 0;
        int calObjetivo = plan.getCaloriasObjetivo() != null ? plan.getCaloriasObjetivo() : 2000;

        // Sumar todos los macros de las comidas mostradas, ya que no hay selección de "completado"
        if (comidaAdapter != null && comidaAdapter.getComidas() != null) {
            for (com.example.skynet.data.remote.dto.ComidaDiariaResponseDto item : comidaAdapter.getComidas()) {
                if (item.getProteina() != null) p += item.getProteina();
                if (item.getCarbohidratos() != null) c += item.getCarbohidratos();
                if (item.getGrasas() != null) g += item.getGrasas();
                if (item.getCalorias() != null) calConsumidas += item.getCalorias();
            }
        }

        if (tvProt != null) tvProt.setText(String.format(java.util.Locale.getDefault(), "%.0fg Prot", p));
        if (tvCarbs != null) tvCarbs.setText(String.format(java.util.Locale.getDefault(), "%.0fg Carbs", c));
        if (tvGrasas != null) tvGrasas.setText(String.format(java.util.Locale.getDefault(), "%.0fg Grasas", g));
        if (donutChart != null) donutChart.setData((float) p, (float) c, (float) g);

        // Reactividad: Restar del objetivo para mostrar lo que falta o el total consumido
        double calRestantes = calObjetivo - calConsumidas;
        if (calRestantes < 0) calRestantes = 0;

        if (tvCaloriasSugeridas != null) tvCaloriasSugeridas.setText(String.format(java.util.Locale.getDefault(), "%.0f kcal consumidas", calConsumidas));
        // El texto grande central o secundario puede mostrar lo que falta
        if (tvCaloriasRestantes != null) tvCaloriasRestantes.setText(String.format(java.util.Locale.getDefault(), "%.0f", calRestantes));

        int progress = calObjetivo > 0 ? (int) ((calConsumidas / calObjetivo) * 100) : 0;
        if (progressCalorias != null) progressCalorias.setProgress(Math.min(progress, 100));
    }

    private void generarSugerencia() {
        if (usuarioId == -1L) {
            Toast.makeText(getContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        String pesoStr = etPeso.getText().toString().trim();
        String alturaStr = etAltura.getText().toString().trim();

        if (pesoStr.isEmpty() || alturaStr.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa peso y altura", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            PerfilFisicoRequest request = new PerfilFisicoRequest();
            request.setUsuarioId(usuarioId);
            request.setPeso(Double.parseDouble(pesoStr));
            request.setAltura(Double.parseDouble(alturaStr));
            // etEdad puede estar vacío si no se cargó del perfil
            String edadStr = etEdad.getText().toString().trim();
            request.setEdad(edadStr.isEmpty() ? 25 : Integer.parseInt(edadStr));

            // Mapeo de Nivel de Actividad al formato que espera el Backend (Enum)
            String actividadSeleccionada = spinnerActividad.getText().toString();
            String actividadBackend;
            switch (actividadSeleccionada) {
                case "SEDENTARIO":
                    actividadBackend = "SEDENTARIO";
                    break;
                case "LIGERO":
                    actividadBackend = "LIGERO";
                    break;
                case "MODERADO":
                    actividadBackend = "MODERADO";
                    break;
                case "INTENSO":
                    actividadBackend = "INTENSO";
                    break;
                case "ATLETA":
                    actividadBackend = "ATLETA";
                    break;
                default:
                    actividadBackend = "MODERADO";
                    break;
            }
            request.setNivelActividad(actividadBackend);

            // Mapeo de objetivo al formato que espera el Backend (EnumObjetivo)
            String objetivoSeleccionado = spinnerObjetivo.getText().toString();
            String objetivoBackend;
            switch (objetivoSeleccionado) {
                case "PERDER_GRASA":
                    objetivoBackend = "PERDER";
                    break;
                case "GANAR_MUSCULO":
                    objetivoBackend = "GANAR";
                    break;
                default:
                    objetivoBackend = "MANTENER";
                    break;
            }
            request.setObjetivo(objetivoBackend);

            viewModel.generarPlan(request);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Por favor, introduce peso y altura válidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoAñadirComida() {
        NutricionResponseDto plan = viewModel.getPlanActual().getValue();
        if (plan == null) {
            Toast.makeText(getContext(), "Primero debes generar o tener un plan activo", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_comida, null);
        AutoCompleteTextView spinnerMomento = dialogView.findViewById(R.id.dialog_spinner_momento);
        TextInputEditText etNombre = dialogView.findViewById(R.id.dialog_et_nombre);
        TextInputEditText etCalorias = dialogView.findViewById(R.id.dialog_et_calorias);
        TextInputEditText etProt = dialogView.findViewById(R.id.dialog_et_prot);
        TextInputEditText etCarbs = dialogView.findViewById(R.id.dialog_et_carbs);
        TextInputEditText etGrasas = dialogView.findViewById(R.id.dialog_et_grasas);

        String[] momentos = {"DESAYUNO", "ALMUERZO", "COMIDA", "MERIENDA", "CENA"};
        spinnerMomento.setAdapter(new NoFilterAdapter(requireContext(), momentos));
        spinnerMomento.setText(momentos[0], false);
        spinnerMomento.setOnClickListener(v -> {
            spinnerMomento.showDropDown();
        });

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialogView);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialogView.findViewById(R.id.btn_cancelar_comida).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_guardar_comida).setOnClickListener(v -> {
            try {
                com.example.skynet.data.remote.dto.ComidaDiariaRequestDto request = new com.example.skynet.data.remote.dto.ComidaDiariaRequestDto();
                request.setMomento(spinnerMomento.getText().toString());
                request.setNombreAlimento(etNombre.getText().toString());
                request.setCalorias(Double.parseDouble(etCalorias.getText().toString()));
                request.setProteina(Double.parseDouble(etProt.getText().toString()));
                request.setCarbohidratos(Double.parseDouble(etCarbs.getText().toString()));
                request.setGrasas(Double.parseDouble(etGrasas.getText().toString()));
                request.setPlanNutricionalId(plan.getId());

                viewModel.añadirComida(request, usuarioId);
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error en los datos introducidos", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void mostrarDialogoBusqueda() {
        NutricionResponseDto plan = viewModel.getPlanActual().getValue();
        if (plan == null) {
            Toast.makeText(getContext(), "Primero debes generar o tener un plan activo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Limpiar resultados anteriores al abrir el diálogo
        viewModel.limpiarBusqueda();

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_food, null);
        androidx.appcompat.widget.SearchView searchView = dialogView.findViewById(R.id.searchViewFood);
        // Prevenir crash de OneSearchSuggestProvider en algunos dispositivos
        searchView.setSuggestionsAdapter(null);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        AutoCompleteTextView spinnerMomento = dialogView.findViewById(R.id.spinnerMomentoBusqueda);
        android.widget.ProgressBar progressBar = dialogView.findViewById(R.id.pbSearchLoading);
        RecyclerView rvResults = dialogView.findViewById(R.id.rvFoodSearchResults);
        TextView tvNoResults = dialogView.findViewById(R.id.tvNoSearchResults);

        String[] momentos = {"DESAYUNO", "ALMUERZO", "COMIDA", "MERIENDA", "CENA"};
        spinnerMomento.setAdapter(new NoFilterAdapter(requireContext(), momentos));
        spinnerMomento.setText(momentos[0], false);
        spinnerMomento.setOnClickListener(v -> {
            spinnerMomento.showDropDown();
        });

        MaterialButton btnCerrar = dialogView.findViewById(R.id.btnCerrarBusqueda);

        FoodSearchAdapter searchAdapter = new FoodSearchAdapter();
        rvResults.setAdapter(searchAdapter);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- SOLUCIÓN: Conectar SearchView con ViewModel y observar resultados ---
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.onSearchQueryChanged(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.onSearchQueryChanged(newText);
                return true;
            }
        });

        viewModel.getResultadosBusqueda().observe(getViewLifecycleOwner(), items -> {
            searchAdapter.setItems(items);
            if (items == null || items.isEmpty()) {
                tvNoResults.setVisibility(View.VISIBLE);
                rvResults.setVisibility(View.GONE);
            } else {
                tvNoResults.setVisibility(View.GONE);
                rvResults.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        // -------------------------------------------------------------------------
        
        androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setView(dialogView)
                .setPositiveButton("Añadir", (d, which) -> {
                    java.util.List<com.example.skynet.data.remote.dto.ComidaDiariaRequestDto> seleccionados = searchAdapter.getSelectedItems();
                    if (!seleccionados.isEmpty()) {
                        String momento = spinnerMomento.getText().toString();
                        for (com.example.skynet.data.remote.dto.ComidaDiariaRequestDto c : seleccionados) {
                            c.setPlanNutricionalId(plan.getId());
                            c.setMomento(momento);
                        }
                        viewModel.guardarMuchasComidas(plan.getId(), seleccionados, usuarioId);
                    } else {
                        Toast.makeText(getContext(), "No has seleccionado ningún alimento", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cerrar", null)
                .setOnDismissListener(d -> {
                    viewModel.getResultadosBusqueda().removeObservers(getViewLifecycleOwner());
                    viewModel.getLoading().removeObservers(getViewLifecycleOwner());
                    // No removemos el de error global para no romper otros flujos, pero podríamos
                })
                .create();

        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void configurarDropdowns() {
        String[] niveles = {"SEDENTARIO", "LIGERO", "MODERADO", "INTENSO", "ATLETA"};
        String[] objetivos = {"PERDER_GRASA", "MANTENER", "GANAR_MUSCULO"};

        // Usamos un adaptador personalizado que desactiva el filtrado
        // Esto soluciona el problema de que solo se vea un elemento o ninguno
        NoFilterAdapter adapterActividad = new NoFilterAdapter(requireContext(), niveles);
        NoFilterAdapter adapterObjetivo = new NoFilterAdapter(requireContext(), objetivos);

        spinnerActividad.setAdapter(adapterActividad);
        spinnerObjetivo.setAdapter(adapterObjetivo);

        // Forzar comportamiento de Spinner (no teclado)
        spinnerActividad.setInputType(android.text.InputType.TYPE_NULL);
        spinnerObjetivo.setInputType(android.text.InputType.TYPE_NULL);

        // Aseguramos apertura inmediata al tocar
        spinnerActividad.setOnClickListener(v -> {
            spinnerActividad.showDropDown();
        });

        spinnerObjetivo.setOnClickListener(v -> {
            spinnerObjetivo.showDropDown();
        });
    }

    /**
     * Adaptador que ignora el filtrado de AutoCompleteTextView.
     * Esto asegura que siempre se muestren todas las opciones independientemente del texto actual.
     */
    private static class NoFilterAdapter extends ArrayAdapter<String> implements Filterable {
        private final String[] items;

        public NoFilterAdapter(Context context, String[] items) {
            super(context, android.R.layout.simple_dropdown_item_1line, items);
            this.items = items;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    results.values = items;
                    results.count = items.length;
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    notifyDataSetChanged();
                }
            };
        }
    }
}