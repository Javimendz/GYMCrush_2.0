package com.backend.service;

import com.backend.domain.*;
import com.backend.dto.RutinaRequestDto;
import com.backend.dto.RutinaResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.RutinaMapper;
import com.backend.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RutinaServiceImp implements IRutinaService {

    private final RutinaRepository rutinaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final PlanEntrenamientoRepository planRepository; 
    private final DetallePlanRepository detallePlanRepository; 
    private final RutinaMapper rutinaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RutinaResponseDto> obtenerRutinaDiaria(Long usuarioId, LocalDate fecha) {
        log.info("Obteniendo rutina diaria para usuario ID {} en fecha {}", usuarioId, fecha);
        return rutinaRepository.findByUsuarioIdAndFechaAsignacionOrderByOrdenAsc(usuarioId, fecha)
                .stream()
                .map(rutina -> {
                    RutinaResponseDto dto = rutinaMapper.toResponseDto(rutina);
                    if (rutina.getUsuario().getPlanActivo() != null) {
                        dto.setNombrePlan(rutina.getUsuario().getPlanActivo().getNombre());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RutinaResponseDto asignarEntrenamiento(RutinaRequestDto dto) {
        log.info("Asignando entrenamiento ID {} al usuario ID {} con {} series", 
                dto.getEntrenamientoId(), dto.getUsuarioId(), dto.getSeries() != null ? dto.getSeries().size() : 0);

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Entrenamiento entrenamiento = entrenamientoRepository.findById(dto.getEntrenamientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado"));

        // 1. Convertir base del DTO a Entidad mediante tu Mapper
        Rutina rutina = rutinaMapper.toEntity(dto);
        rutina.setUsuario(usuario);
        rutina.setEntrenamiento(entrenamiento);
        rutina.setCompletado(false); 

        // 2. Mapear y enlazar las series dinámicas si vienen en la petición (Botón + Agregar Serie)
        if (dto.getSeries() != null) {
            List<SerieRutina> detalleSeries = dto.getSeries().stream().map(serieDto -> 
                SerieRutina.builder()
                        .numeroSerie(serieDto.getNumeroSerie())
                        .peso(serieDto.getPeso())
                        .repeticiones(serieDto.getRepeticiones())
                        .completada(false)
                        .rutina(rutina) // Clave para mantener la consistencia de la FK
                        .build()
            ).collect(Collectors.toList());
            
            rutina.setDetalleSeries(detalleSeries);
        }

        // 3. Al guardar la rutina, CascadeType.ALL insertará automáticamente los registros en 'serie_rutinas'
        Rutina guardada = rutinaRepository.save(rutina);
        return rutinaMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional
    public RutinaResponseDto marcarComoCompletada(Long id) {
        log.info("Marcando rutina ID {} como completada/pendiente", id);

        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de rutina no encontrada"));

        rutina.setCompletado(!rutina.getCompletado());

        // Opcional: Si se marca la cabecera, podemos marcar todas sus series individuales como completadas
        if (rutina.getDetalleSeries() != null) {
            rutina.getDetalleSeries().forEach(serie -> serie.setCompletada(rutina.getCompletado()));
        }

        return rutinaMapper.toResponseDto(rutinaRepository.save(rutina));
    }

    @Override
    @Transactional
    public void eliminarAsignacion(Long id) {
        log.info("Eliminando asignación de rutina ID {}", id);
        if (!rutinaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe la rutina con ID: " + id);
        }
        rutinaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutinaResponseDto> obtenerPorRango(Long usuarioId, LocalDate inicio, LocalDate fin) {
        log.info("Obteniendo rutinas para usuario {} entre {} y {}", usuarioId, inicio, fin);

        return rutinaRepository.findByUsuarioIdAndFechaAsignacionBetweenOrderByOrden(usuarioId, inicio, fin)
                .stream()
                .map(rutinaMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}