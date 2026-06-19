package com.backend.service;

import com.backend.dto.CategoriaRequestDto;
import com.backend.dto.CategoriaResponseDto;
import java.util.List;

public interface ICategoriaService {
    CategoriaResponseDto crear(CategoriaRequestDto dto);
    List<CategoriaResponseDto> listarTodas();
    CategoriaResponseDto obtenerPorId(Long id);
    void eliminar(Long id);
}