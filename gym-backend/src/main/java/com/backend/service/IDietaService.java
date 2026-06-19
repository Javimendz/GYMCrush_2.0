package com.backend.service;

import java.util.List;

import com.backend.dto.DietaRequestDto;
import com.backend.dto.DietaResponseDto;

public interface IDietaService {
    

    DietaResponseDto crearDieta(DietaRequestDto dto);
    List<DietaResponseDto> listarTodas();
    DietaResponseDto obtenerPorId(Long id);
    void eliminar(Long id);
}
