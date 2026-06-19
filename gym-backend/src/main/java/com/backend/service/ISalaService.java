package com.backend.service;

import com.backend.dto.SalaRequestDto;
import com.backend.dto.SalaResponseDto;
import java.util.List;

public interface ISalaService {
    List<SalaResponseDto> listarTodas();

    List<SalaResponseDto> listarActivas();

    SalaResponseDto obtenerPorId(Long id);

    SalaResponseDto crear(SalaRequestDto dto);

    SalaResponseDto actualizar(Long id, SalaRequestDto dto);

    void eliminar(Long id);

    void cambiarEstado(Long id, Boolean activa);
}