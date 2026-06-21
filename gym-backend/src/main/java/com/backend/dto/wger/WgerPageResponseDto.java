package com.backend.dto.wger;


import lombok.*;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WgerPageResponseDto {

    private List<WgerExerciseInfoObjectDto> ejercicios;
    private int totalElementos;
    private int totalPaginas;
    private int paginaActual;
    private int tamañoPagina;
    private boolean hayAnterior;
    private boolean haySiguiente;

    public static WgerPageResponseDto empty() {
        return WgerPageResponseDto.builder()
            .ejercicios(Collections.emptyList())
            .totalElementos(0)
            .totalPaginas(0)
            .paginaActual(1)
            .tamañoPagina(20)
            .hayAnterior(false)
            .haySiguiente(false)
            .build();
    }
}