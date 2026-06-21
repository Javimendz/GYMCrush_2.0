package com.example.skynet.ui.ejercicios;

import com.example.skynet.data.remote.dto.WgerCategoriaDto;
import com.example.skynet.data.remote.dto.WgerMusculoDto;
import java.util.ArrayList;
import java.util.List;

public abstract class AgregarEjercicioUiState {
    public static class Loading extends AgregarEjercicioUiState {}

    public static class Success extends AgregarEjercicioUiState {
        private final List<EntrenamientoDto> ejercicios;
        private final List<WgerMusculoDto> musculos;
        private final List<WgerCategoriaDto> categorias;
        private final List<Long> selectedIds;
        private final int totalPaginas;
        private final int paginaActual;

        public Success(List<EntrenamientoDto> ejercicios) {
            this(ejercicios, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 1, 1);
        }

        public Success(List<EntrenamientoDto> ejercicios, List<WgerMusculoDto> musculos, List<WgerCategoriaDto> categorias, List<Long> selectedIds, int totalPaginas, int paginaActual) {
            this.ejercicios = ejercicios;
            this.musculos = musculos;
            this.categorias = categorias;
            this.selectedIds = selectedIds;
            this.totalPaginas = totalPaginas;
            this.paginaActual = paginaActual;
        }

        public List<EntrenamientoDto> getEjercicios() { return ejercicios; }
        public List<WgerMusculoDto> getMusculos() { return musculos; }
        public List<WgerCategoriaDto> getCategorias() { return categorias; }
        public List<Long> getSelectedIds() { return selectedIds; }
        public int getTotalPaginas() { return totalPaginas; }
        public int getPaginaActual() { return paginaActual; }
    }

    public static class Error extends AgregarEjercicioUiState {
        private final String message;
        public Error(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}
