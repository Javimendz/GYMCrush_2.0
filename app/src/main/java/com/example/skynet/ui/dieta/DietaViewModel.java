package com.example.skynet.ui.dieta;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.ComidaDiariaRequestDto;
import com.example.skynet.data.remote.dto.ComidaDiariaResponseDto;
import com.example.skynet.data.remote.dto.DietaResponseDto;
import com.example.skynet.data.remote.dto.FatSecretRecipeDto;
import com.example.skynet.data.remote.dto.FatSecretResponseDto;
import com.example.skynet.data.remote.dto.NutricionResponseDto;
import com.example.skynet.data.remote.dto.PageResponseDto;
import com.example.skynet.data.remote.dto.PerfilFisicoRequest;
import com.example.skynet.data.remote.dto.SaludResponseDto;
import com.example.skynet.data.repository.DietaRepository;
import com.example.skynet.data.repository.NutricionRepository;
import com.example.skynet.data.repository.PerfilRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DietaViewModel extends ViewModel {
    private final DietaRepository dietaRepository;
    private final NutricionRepository nutricionRepository;
    private final PerfilRepository perfilRepository;

    private final MutableLiveData<List<DietaResponseDto>> dietasCatalogo = new MutableLiveData<>();
    private final MutableLiveData<NutricionResponseDto> planActual = new MutableLiveData<>();
    private final MutableLiveData<List<NutricionResponseDto>> historial = new MutableLiveData<>();
    private final MutableLiveData<SaludResponseDto> saludActual = new MutableLiveData<>();
    private final MutableLiveData<List<ComidaDiariaRequestDto>> resultadosBusqueda = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<NutricionResponseDto> planGeneradoEvent = new MutableLiveData<>();

    private final PublishSubject<String> searchSubject = PublishSubject.create();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public DietaViewModel() {
        this.dietaRepository = new DietaRepository();
        this.nutricionRepository = new NutricionRepository();
        this.perfilRepository = new PerfilRepository();
        setupSearchDebounce();
    }

    private void setupSearchDebounce() {
        disposables.add(
                searchSubject
                        .debounce(400, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::buscarAlimentos)
        );
    }

    public void onSearchQueryChanged(String query) {
        if (query == null || query.length() <= 2) {
            limpiarBusqueda();
            return;
        }
        searchSubject.onNext(query);
    }

    public LiveData<List<DietaResponseDto>> getDietasCatalogo() { return dietasCatalogo; }
    public LiveData<NutricionResponseDto> getPlanActual() { return planActual; }
    public LiveData<List<NutricionResponseDto>> getHistorial() { return historial; }
    public LiveData<SaludResponseDto> getSaludActual() { return saludActual; }
    public LiveData<List<ComidaDiariaRequestDto>> getResultadosBusqueda() { return resultadosBusqueda; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<NutricionResponseDto> getPlanGeneradoEvent() { return planGeneradoEvent; }

    public void consumirEventoPlan() {
        planGeneradoEvent.setValue(null);
    }

    private int currentPage = 0;
    private final int PAGE_SIZE = 5;
    private boolean isLastPage = false;
    private boolean isHistoryLoading = false;
    private List<NutricionResponseDto> currentHistorialList = new ArrayList<>();

    public void cargarDatosIniciales(Long usuarioId) {
        loading.setValue(true);
        cargarSalud(usuarioId);
        cargarUltimoPlan(usuarioId);
        reiniciarPaginacionHistorial(usuarioId);
        cargarCatalogoDietas();
    }

    private void cargarCatalogoDietas() {
        dietaRepository.listarDietas(new Callback<ApiResponseDto<List<DietaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<DietaResponseDto>>> call, Response<ApiResponseDto<List<DietaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dietasCatalogo.setValue(response.body().getDatos());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<DietaResponseDto>>> call, Throwable t) {
                // Silencioso o log
            }
        });
    }

    private void cargarSalud(Long usuarioId) {
        perfilRepository.getSaludActual(usuarioId, new PerfilRepository.PerfilCallback<SaludResponseDto>() {
            @Override
            public void onSuccess(SaludResponseDto result) {
                saludActual.setValue(result);
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
            }
        });
    }

    public void reiniciarPaginacionHistorial(Long usuarioId) {
        if (isHistoryLoading) return;
        currentPage = 0;
        isLastPage = false;
        currentHistorialList.clear();
        cargarHistorial(usuarioId);
    }

    public void cargarHistorial(Long usuarioId) {
        if (isLastPage || isHistoryLoading) return;

        isHistoryLoading = true;
        nutricionRepository.obtenerHistorial(usuarioId, currentPage, PAGE_SIZE, new Callback<ApiResponseDto<PageResponseDto<NutricionResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponseDto<NutricionResponseDto>>> call, Response<ApiResponseDto<PageResponseDto<NutricionResponseDto>>> response) {
                isHistoryLoading = false;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PageResponseDto<NutricionResponseDto> pageData = response.body().getDatos();
                    if (pageData != null) {
                        List<NutricionResponseDto> nuevosPlanes = pageData.getContent();
                        if (nuevosPlanes == null || nuevosPlanes.isEmpty()) {
                            isLastPage = true;
                        } else {
                            currentHistorialList.addAll(nuevosPlanes);
                            historial.setValue(new ArrayList<>(currentHistorialList));
                            currentPage++;
                            isLastPage = pageData.isLast();
                        }
                    }
                }
                checkLoadingComplete();
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PageResponseDto<NutricionResponseDto>>> call, Throwable t) {
                isHistoryLoading = false;
                checkLoadingComplete();
            }
        });
    }

    private void checkLoadingComplete() {
        loading.setValue(false);
    }

    public void generarPlan(PerfilFisicoRequest request) {
        loading.setValue(true);
        nutricionRepository.generarPlan(request, new Callback<ApiResponseDto<NutricionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<NutricionResponseDto>> call, Response<ApiResponseDto<NutricionResponseDto>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        NutricionResponseDto nuevoPlan = response.body().getDatos();
                        planActual.setValue(nuevoPlan);
                        planGeneradoEvent.setValue(nuevoPlan);
                        // Actualizar historial tras generar un nuevo plan
                        reiniciarPaginacionHistorial(request.getUsuarioId());
                    } else {
                        error.setValue(response.body().getMensaje());
                    }
                } else {
                    error.setValue("No se pudo generar el plan nutricional: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<NutricionResponseDto>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Error de red: " + t.getMessage());
            }
        });
    }

    public void cargarUltimoPlan(Long usuarioId) {
        // Opcional: planActual.setValue(null); // Esto pondría un skeleton o loading
        nutricionRepository.obtenerUltimoPlan(usuarioId, new Callback<ApiResponseDto<NutricionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<NutricionResponseDto>> call, Response<ApiResponseDto<NutricionResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Seteamos el plan que viene de la DB (con sus comidas y macros)
                    planActual.setValue(response.body().getDatos());
                }
                loading.setValue(false);
            }

            @Override
            public void onFailure(Call<ApiResponseDto<NutricionResponseDto>> call, Throwable t) {
                loading.setValue(false);
            }
        });
    }

    public void cargarPlan(Long planId) {
        loading.setValue(true);
        nutricionRepository.obtenerPlanPorId(planId, new Callback<ApiResponseDto<NutricionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<NutricionResponseDto>> call, Response<ApiResponseDto<NutricionResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    planActual.setValue(response.body().getDatos());
                }
                checkLoadingComplete();
            }

            @Override
            public void onFailure(Call<ApiResponseDto<NutricionResponseDto>> call, Throwable t) {
                checkLoadingComplete();
            }
        });
    }

    public void añadirComida(ComidaDiariaRequestDto request, Long usuarioId) {
        loading.setValue(true);
        nutricionRepository.añadirComida(request, new Callback<ApiResponseDto<ComidaDiariaResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<ComidaDiariaResponseDto>> call, Response<ApiResponseDto<ComidaDiariaResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Recargar el plan específico al que añadimos la comida
                    if (request.getPlanNutricionalId() != null) {
                        cargarPlan(request.getPlanNutricionalId());
                    } else {
                        cargarUltimoPlan(usuarioId);
                    }
                } else {
                    loading.setValue(false);
                    error.setValue("No se pudo añadir el plato");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<ComidaDiariaResponseDto>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Error de red: " + t.getMessage());
            }
        });
    }

    public void eliminarComida(Long comidaId, Long planId, Long usuarioId) {
        loading.setValue(true);
        nutricionRepository.eliminarComida(comidaId, new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    if (planId != null) {
                        cargarPlan(planId);
                    } else {
                        cargarUltimoPlan(usuarioId);
                    }
                } else {
                    loading.setValue(false);
                    error.setValue("No se pudo eliminar el plato");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Error de red: " + t.getMessage());
            }
        });
    }

    public void guardarMuchasComidas(Long planId, List<ComidaDiariaRequestDto> comidas, Long usuarioId) {
        loading.setValue(true);
        nutricionRepository.guardarMuchasComidas(planId, comidas, new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    if (planId != null) {
                        cargarPlan(planId);
                    } else {
                        cargarUltimoPlan(usuarioId);
                    }
                } else {
                    loading.setValue(false);
                    error.setValue("No se pudieron guardar las comidas");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Error de red: " + t.getMessage());
            }
        });
    }

    public void buscarAlimentos(String query) {
        loading.setValue(true);
        nutricionRepository.buscarAlimentos(query, new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        String jsonString = response.body().getDatos();
                        parsearResultadosFatSecret(jsonString);
                    } else {
                        resultadosBusqueda.setValue(new java.util.ArrayList<>());
                        error.setValue(response.body().getMensaje());
                    }
                } else {
                    resultadosBusqueda.setValue(new java.util.ArrayList<>());
                    error.setValue("Error en el servidor (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                loading.setValue(false);
                resultadosBusqueda.setValue(new java.util.ArrayList<>());
                error.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void parsearResultadosFatSecret(String jsonString) {
        try {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            FatSecretResponseDto fsResponse = gson.fromJson(jsonString, FatSecretResponseDto.class);

            List<ComidaDiariaRequestDto> conversion = new java.util.ArrayList<>();
            if (fsResponse != null && fsResponse.getRecipes() != null && fsResponse.getRecipes().getRecipe() != null) {
                for (FatSecretRecipeDto recipe : fsResponse.getRecipes().getRecipe()) {
                    ComidaDiariaRequestDto dto = new ComidaDiariaRequestDto();
                    dto.setNombreAlimento(recipe.getRecipeName());
                    dto.setImagenUrl(recipe.getRecipeImage());
                    dto.setRecipeUrl(recipe.getRecipeUrl());
                    dto.setDescripcion(recipe.getRecipeDescription());

                    if (recipe.getRecipeNutrition() != null) {
                        try {
                            dto.setCalorias(Double.parseDouble(recipe.getRecipeNutrition().getCalories()));
                            dto.setProteina(Double.parseDouble(recipe.getRecipeNutrition().getProtein()));
                            dto.setCarbohidratos(Double.parseDouble(recipe.getRecipeNutrition().getCarbohydrate()));
                            dto.setGrasas(Double.parseDouble(recipe.getRecipeNutrition().getFat()));
                        } catch (Exception ignored) {}
                    }
                    conversion.add(dto);
                }
            }
            resultadosBusqueda.setValue(conversion);
        } catch (Exception e) {
            error.setValue("Error al procesar alimentos");
            resultadosBusqueda.setValue(new java.util.ArrayList<>());
        }
    }

    public void limpiarBusqueda() {
        resultadosBusqueda.setValue(new java.util.ArrayList<>());
    }

    public void actualizarNombrePlan(Long planId, String nombre, Long usuarioId) {
        loading.setValue(true);
        nutricionRepository.actualizarNombrePlan(planId, nombre, new Callback<ApiResponseDto<NutricionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<NutricionResponseDto>> call, Response<ApiResponseDto<NutricionResponseDto>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    planActual.setValue(response.body().getDatos());
                    reiniciarPaginacionHistorial(usuarioId);
                } else {
                    String errorMsg = "No se pudo renombrar el plan";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += ": " + response.errorBody().string();
                        } catch (Exception ignored) {}
                    }
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<NutricionResponseDto>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Error de red: " + t.getMessage());
            }
        });
    }

    public void guardarPlan(Long usuarioId, NutricionResponseDto plan) {
        loading.setValue(true);
        nutricionRepository.guardarPlan(usuarioId, plan, new Callback<ApiResponseDto<NutricionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<NutricionResponseDto>> call, Response<ApiResponseDto<NutricionResponseDto>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // CLAVE: Seteamos el plan guardado (que ya tiene ID de la DB) como el plan actual
                    planActual.setValue(response.body().getDatos());

                    // Forzamos desbloqueo para asegurar que el historial se actualice tras un guardado
                    isHistoryLoading = false;
                    reiniciarPaginacionHistorial(usuarioId);

                    error.setValue("Plan guardado correctamente");
                } else {
                    error.setValue("No se pudo guardar el plan");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<NutricionResponseDto>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Error de red: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}