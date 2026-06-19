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
    private final PlanEntrenamientoRepository planRepository; // Añadido
    private final DetallePlanRepository detallePlanRepository; // Añadido
    private final RutinaMapper rutinaMapper;

    /* 
    @Override
    @Transactional
    public void activarPlanMaestro(Long usuarioId, Long planId) {
        log.info("Activando Plan Maestro ID {} para el usuario ID {}", planId, usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        PlanEntrenamiento plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        // 1. Opcional: Limpiar rutinas pendientes futuras para evitar duplicados al
        // reactivar
        rutinaRepository.deleteByUsuarioAndFechaAsignacionGreaterThanEqualAndCompletado(usuario, LocalDate.now(),
                false);

        // 2. Obtener el lunes de la semana actual como punto de referencia
        LocalDate lunesDeEstaSemana = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 3. Obtener los ejercicios configurados en el plan
        List<DetallePlan> detalles = detallePlanRepository.findByPlanIdOrderByDiaSemanaAscOrdenAsc(planId);

        for (DetallePlan detalle : detalles) {
            Rutina nuevaRutina = new Rutina();
            nuevaRutina.setUsuario(usuario);
            nuevaRutina.setEntrenamiento(detalle.getEntrenamiento());
            nuevaRutina.setOrden(detalle.getOrden());
            nuevaRutina.setCompletado(false);
            nuevaRutina.setSeries(detalle.getSeries());
            nuevaRutina.setRepeticiones(detalle.getRepeticiones());

            // CÁLCULO DE FECHA: Si diaSemana es 1 (Lunes), plusDays(0). Si es 3
            // (Miércoles), plusDays(2).
            LocalDate fechaReal = lunesDeEstaSemana.plusDays(detalle.getDiaSemana() - 1);
            nuevaRutina.setFechaAsignacion(fechaReal);

            rutinaRepository.save(nuevaRutina);
        }

        // 4. Actualizar el plan activo en el perfil del usuario
        usuario.setPlanActivo(plan);
        usuarioRepository.save(usuario);
    }
    */

    @Override
    @Transactional(readOnly = true)
    public List<RutinaResponseDto> obtenerRutinaDiaria(Long usuarioId, LocalDate fecha) {
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
        log.info("Asignando entrenamiento ID {} al usuario ID {}", dto.getEntrenamientoId(), dto.getUsuarioId());

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Entrenamiento entrenamiento = entrenamientoRepository.findById(dto.getEntrenamientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado"));

        Rutina rutina = rutinaMapper.toEntity(dto);
        rutina.setUsuario(usuario);
        rutina.setEntrenamiento(entrenamiento);
        rutina.setCompletado(false); // Por defecto no completada

        Rutina guardada = rutinaRepository.save(rutina);
        return rutinaMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional
    public RutinaResponseDto marcarComoCompletada(Long id) {
        log.info("Marcando rutina ID {} como completada/pendiente", id);

        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de rutina no encontrada"));

        // si está completada la pone pendiente y viceversa
        rutina.setCompletado(!rutina.getCompletado());

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