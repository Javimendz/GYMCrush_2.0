package com.backend.service;

import com.backend.dto.TutorialResponseDto;
import com.backend.dto.VisualizacionRequestDto;
import com.backend.dto.VisualizacionResponseDto;
import java.util.List;

public interface IVisualizacionService {
    VisualizacionResponseDto guardarProgreso(VisualizacionRequestDto dto);

    List<VisualizacionResponseDto> obtenerHistorial(Long usuarioId);

    List<VisualizacionResponseDto> obtenerContinuarViendo(Long usuarioId);

    void borrarDeHistorial(Long id);
    VisualizacionResponseDto obtenerProgreso(Long usuarioId, Long tutorialId);
    List<TutorialResponseDto> obtenerPopulares();
}