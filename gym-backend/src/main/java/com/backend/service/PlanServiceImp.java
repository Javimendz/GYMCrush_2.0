package com.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.domain.*;
import com.backend.dto.*;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.PlanMapper;
import com.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanServiceImp implements IPlanService {

    private final PlanEntrenamientoRepository planRepo;
    private final RutinaRepository rutinaRepo;
    private final UsuarioRepository usuarioRepo;
    private final EntrenamientoRepository entrenamientoRepo;
    private final PlanMapper planMapper;

    @Override
    @Transactional
    public PlanResponseDto crearPlan(PlanRequestDto dto, String username) {
        log.info("Creando nuevo plan: {} por el usuario {}", dto.getNombre(), username);
        PlanEntrenamiento plan = planMapper.toEntity(dto);

        Usuario autor = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Determinamos si es Global (ADMIN) o Personal (USUARIO)
        boolean isAdmin = autor.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));

        if (isAdmin) {
            plan.setUsuario(null); // Los planes globales no tienen dueño individual
            plan.setEsGlobal(true);
        } else {
            plan.setUsuario(autor);
            plan.setEsGlobal(false);
        }

        // Persistimos y devolvemos el DTO mapeado (ahora con usuarioId lleno)
        PlanEntrenamiento guardado = planRepo.saveAndFlush(plan);
        return planMapper.toResponseDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponseDto> listarPlanesParaUsuario(Long usuarioId) {
        // Usa la query personalizada del repositorio
        return planRepo.findGlobalesYDelUsuario(usuarioId).stream()
                .map(planMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponseDto> listarTodos() {
        return planRepo.findAll().stream()
                .map(planMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponseDto obtenerPorId(Long id) {
        PlanEntrenamiento plan = planRepo.findWithEjerciciosById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + id));
        return planMapper.toResponseDto(plan);
    }

    @Override
    @Transactional
    public void eliminarPlan(Long id) {
        PlanEntrenamiento plan = planRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        rutinaRepo.deleteByUsuarioPlanActivoIdAndCompletado(id, false);
        usuarioRepo.desvincularPlanDeUsuarios(id);
        planRepo.delete(plan);
    }

    @Override
    @Transactional
    public void suscribirUsuarioAPlan(Long usuarioId, Long planId, String usernameLogueado, boolean esAdmin) {
        Usuario usuarioDestino = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        PlanEntrenamiento plan = planRepo.findWithEjerciciosById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        LocalDate hoy = LocalDate.now();
        rutinaRepo.deleteByUsuarioAndFechaAsignacionGreaterThanEqualAndCompletado(usuarioDestino, hoy, false);

        List<Rutina> rutinasAGuardar = new java.util.ArrayList<>();
        LocalDate lunesDeEstaSemana = hoy.with(java.time.DayOfWeek.MONDAY);

        for (DetallePlan detalle : plan.getEjercicios()) {
            LocalDate fechaAsignacion = lunesDeEstaSemana.plusDays(detalle.getDiaSemana() - 1);
            if (fechaAsignacion.isBefore(hoy)) {
                fechaAsignacion = fechaAsignacion.plusWeeks(1);
            }

            rutinasAGuardar.add(Rutina.builder()
                    .usuario(usuarioDestino)
                    .entrenamiento(detalle.getEntrenamiento())
                    .fechaAsignacion(fechaAsignacion)
                    .orden(detalle.getOrden())
                    .completado(false)
                    .build());
        }

        rutinaRepo.saveAll(rutinasAGuardar);
        usuarioDestino.setPlanActivo(plan);
        usuarioRepo.save(usuarioDestino);
    }

    @Override
    @Transactional
    public PlanResponseDto añadirEjercicioAlPlan(Long planId, List<DetallePlanRequestDto> ejerciciosDto) {
        log.info("Añadiendo {} ejercicios al plan con ID {}", ejerciciosDto.size(), planId);
        
        PlanEntrenamiento plan = planRepo.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + planId));

        for (DetallePlanRequestDto dto : ejerciciosDto) {
            Entrenamiento entrenamiento = entrenamientoRepo.findById(dto.getEntrenamientoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con ID: " + dto.getEntrenamientoId()));

            DetallePlan nuevoDetalle = new DetallePlan();
            nuevoDetalle.setEntrenamiento(entrenamiento);
            nuevoDetalle.setDiaSemana(dto.getDiaSemana());
            nuevoDetalle.setOrden(dto.getOrden());
            nuevoDetalle.setSeries(dto.getSeries());
            nuevoDetalle.setRepeticiones(dto.getRepeticiones());

            // Tu método helper se encarga de setear el plan en el detalle e incluirlo en la lista
            plan.addEjercicio(nuevoDetalle);
        }

        // Al guardar el plan con CascadeType.ALL, se persistirán los nuevos DetallePlan automáticamente
        PlanEntrenamiento planGuardado = planRepo.save(plan);
        return planMapper.toResponseDto(planGuardado);
    }
}