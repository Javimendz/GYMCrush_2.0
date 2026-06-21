package com.example.skynet.ui.ejercicios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.WgerCategoriaDto;
import com.example.skynet.data.remote.dto.WgerEjerciciosResponseDto;
import com.example.skynet.data.remote.dto.WgerMusculoDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarEjercicioViewModel extends ViewModel {

    private final MutableLiveData<String> _searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<AgregarEjercicioUiState> _uiState = new MutableLiveData<>(new AgregarEjercicioUiState.Loading());
    private final List<Long> selectedExerciseIds = new ArrayList<>();
    
    private List<EntrenamientoDto> allEjercicios = new ArrayList<>();
    private List<WgerMusculoDto> allMusculos = new ArrayList<>();
    private List<WgerCategoriaDto> allCategorias = new ArrayList<>();

    private Integer selectedMuscleId = null;
    private Integer selectedCategoryId = null;
    private int currentPage = 1;
    private int totalPaginas = 1;
    private boolean isFetching = false;

    public LiveData<String> getSearchQuery() { return _searchQuery; }
    public LiveData<AgregarEjercicioUiState> getUiState() { return _uiState; }

    public AgregarEjercicioViewModel() {
        fetchInitialData();
    }

    private void fetchInitialData() {
        fetchMusculos();
        fetchCategorias();
        fetchEjercicios(1, null, null);
    }

    public void onSearchQueryChange(String query) {
        _searchQuery.setValue(query);
        filterEjercicios(query);
    }

    public void onMuscleFilterChanged(Integer muscleId) {
        if (java.util.Objects.equals(this.selectedMuscleId, muscleId)) return;
        this.selectedMuscleId = muscleId;
        this.currentPage = 1;
        fetchEjercicios(currentPage, selectedMuscleId, selectedCategoryId);
    }

    public void onCategoryFilterChanged(Integer categoryId) {
        if (java.util.Objects.equals(this.selectedCategoryId, categoryId)) return;
        this.selectedCategoryId = categoryId;
        this.currentPage = 1;
        fetchEjercicios(currentPage, selectedMuscleId, selectedCategoryId);
    }

    public void onPageChanged(int page) {
        if (page < 1 || page > totalPaginas || page == currentPage) return;
        this.currentPage = page;
        fetchEjercicios(currentPage, selectedMuscleId, selectedCategoryId);
    }

    private void fetchMusculos() {
        RetrofitClient.getApiService().getWgerMusculos().enqueue(new Callback<ApiResponseDto<List<WgerMusculoDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<WgerMusculoDto>>> call, Response<ApiResponseDto<List<WgerMusculoDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allMusculos = response.body().getDatos();
                    updateUiStateWithCurrentData();
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<WgerMusculoDto>>> call, Throwable t) {}
        });
    }

    private void fetchCategorias() {
        RetrofitClient.getApiService().getWgerCategorias().enqueue(new Callback<ApiResponseDto<List<WgerCategoriaDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<WgerCategoriaDto>>> call, Response<ApiResponseDto<List<WgerCategoriaDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCategorias = response.body().getDatos();
                    updateUiStateWithCurrentData();
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<WgerCategoriaDto>>> call, Throwable t) {}
        });
    }

    private void fetchEjercicios(int page, Integer muscleId, Integer categoryId) {
        if (isFetching) return;
        isFetching = true;

        if (page == 1) {
            _uiState.setValue(new AgregarEjercicioUiState.Loading());
        }
        
        RetrofitClient.getApiService().getWgerEjercicios(page, 20, "4", muscleId, categoryId)
                .enqueue(new Callback<ApiResponseDto<WgerEjerciciosResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<WgerEjerciciosResponseDto>> call, Response<ApiResponseDto<WgerEjerciciosResponseDto>> response) {
                isFetching = false;
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    WgerEjerciciosResponseDto data = response.body().getDatos();
                    totalPaginas = data.getTotalPaginas();
                    currentPage = data.getPaginaActual();

                    List<EntrenamientoDto> nuevosEjercicios = data.getEjercicios().stream()
                            .map(d -> new EntrenamientoDto(
                                    d.getId(),
                                    d.getName() != null ? d.getName() : "Sin nombre",
                                    d.getDescription(),
                                    d.getImageUrl(),
                                    "Wger"
                            ))
                            .collect(Collectors.toList());

                    if (page == 1) {
                        allEjercicios = new ArrayList<>(nuevosEjercicios);
                    } else {
                        // Evitar duplicados si por alguna razón la API repite elementos
                        for (EntrenamientoDto nuevo : nuevosEjercicios) {
                            if (nuevo.getId() != null && allEjercicios.stream().noneMatch(e -> e.getId() != null && e.getId().equals(nuevo.getId()))) {
                                allEjercicios.add(nuevo);
                            }
                        }
                    }

                    filterEjercicios(_searchQuery.getValue());
                } else {
                    _uiState.setValue(new AgregarEjercicioUiState.Error("Error al cargar ejercicios de Wger"));
                }
            }

                    @Override
                    public void onFailure(Call<ApiResponseDto<WgerEjerciciosResponseDto>> call, Throwable t) {
                        isFetching = false; // Reset necesario ante cualquier error
                        _uiState.setValue(new AgregarEjercicioUiState.Error(t.getMessage()));
                    }
        });
    }

    public void loadNextPage() {
        // Verifica si ya estamos cargando o si ya llegamos al final
        if (isFetching || currentPage >= totalPaginas) return;

        // Incrementa la página localmente antes de llamar
        fetchEjercicios(currentPage + 1, selectedMuscleId, selectedCategoryId);
    }
    public void toggleExerciseSelection(Long exerciseId) {
        if (selectedExerciseIds.contains(exerciseId)) {
            selectedExerciseIds.remove(exerciseId);
        } else {
            selectedExerciseIds.add(exerciseId);
        }
        updateUiStateWithCurrentData();
    }

    public List<Long> getSelectedExerciseIds() {
        return new ArrayList<>(selectedExerciseIds);
    }

    private void updateUiStateWithCurrentData() {
        if (_uiState.getValue() instanceof AgregarEjercicioUiState.Success) {
            AgregarEjercicioUiState.Success current = (AgregarEjercicioUiState.Success) _uiState.getValue();
            _uiState.setValue(new AgregarEjercicioUiState.Success(
                    current.getEjercicios(),
                    allMusculos,
                    allCategorias,
                    selectedExerciseIds,
                    totalPaginas,
                    currentPage
            ));
        }
    }

    private void filterEjercicios(String query) {
        List<EntrenamientoDto> filtered;
        if (query == null || query.isEmpty()) {
            filtered = new ArrayList<>(allEjercicios);
        } else {
            String lowerQuery = query.toLowerCase();
            filtered = allEjercicios.stream()
                    .filter(e -> e.getNombre().toLowerCase().contains(lowerQuery))
                    .collect(Collectors.toList());
        }
        
        _uiState.setValue(new AgregarEjercicioUiState.Success(
                filtered,
                allMusculos,
                allCategorias,
                selectedExerciseIds,
                totalPaginas,
                currentPage
        ));
    }

    public Integer getSelectedMuscleId() { return selectedMuscleId; }
    public Integer getSelectedCategoryId() { return selectedCategoryId; }
}
