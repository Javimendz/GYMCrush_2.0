package com.backend.service;

import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;

import java.util.List;

public interface IComidaDiariaService {
    // Guarda una lista de comidas
    void guardarComidas(List<ComidaDiariaRequestDto> comidasRequest, Long planId);

    // Añadir una comida suelta
    ComidaDiariaResponseDto añadirComida(ComidaDiariaRequestDto request);

    // Borrar una comida por su ID
    void eliminarComida(Long id);
}