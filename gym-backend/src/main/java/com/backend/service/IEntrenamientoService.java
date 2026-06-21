package com.backend.service;

import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import java.util.List;

public interface IEntrenamientoService {
   EntrenamientoResponseDto crear(EntrenamientoRequestDto dto, String username); 
    
    List<EntrenamientoResponseDto> listarTodos();
    List<EntrenamientoResponseDto> listarPorUsuario(Long usuarioId);
    EntrenamientoResponseDto obtenerPorId(Long id);
    EntrenamientoResponseDto actualizar(Long id, EntrenamientoRequestDto dto);
    void eliminar(Long id);
    List<EntrenamientoResponseDto> obtenerGlobales(String intensidad, String objetivo);
}