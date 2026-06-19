package com.backend.service;

import java.util.List;

import com.backend.dto.CategoriaDietaRequestDto;
import com.backend.dto.CategoriaDietaResponseDto;



public interface ICategoriaDietaService {
 
    CategoriaDietaResponseDto crear(CategoriaDietaRequestDto dto);
    List<CategoriaDietaResponseDto> listarTodas();
    CategoriaDietaResponseDto obtenerPorId(Long id);
    void eliminar(Long id);

}
