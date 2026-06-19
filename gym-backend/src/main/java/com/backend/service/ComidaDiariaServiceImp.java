package com.backend.service;

import com.backend.domain.ComidaDiaria;
import com.backend.domain.Nutricion;
import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.ComidaDiariaMapper;
import com.backend.repository.ComidaDiariaRepository;
import com.backend.repository.NutricionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComidaDiariaServiceImp implements IComidaDiariaService {

    private final ComidaDiariaRepository comidaRepository;
    private final NutricionRepository nutricionRepository;
    private final ComidaDiariaMapper comidaMapper;

    @Override
    @Transactional
    public void guardarComidas(List<ComidaDiariaRequestDto> comidasRequest, Long planId) {
        log.info("Iniciando guardado masivo para el plan ID: {}", planId);

        Nutricion plan = nutricionRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se pueden guardar las comidas: Plan no encontrado con ID: " + planId));

        List<ComidaDiaria> entidades = comidasRequest.stream()
                .filter(dto -> !comidaRepository.existsByPlanIdAndMomentoIgnoreCaseAndNombreAlimentoIgnoreCase(
                        planId, dto.getMomento(), dto.getNombreAlimento()))
                .map(dto -> comidaMapper.toEntity(dto, plan))
                .toList();

        // GUARDAR
        if (!entidades.isEmpty()) {
            comidaRepository.saveAll(entidades);
            log.info("Se han insertado {} nuevas comidas al plan {}", entidades.size(), planId);
        } else {
            log.info("No hay comidas nuevas para insertar (todas estaban duplicadas)");
        }
    }

    @Override
    @Transactional
    public ComidaDiariaResponseDto añadirComida(ComidaDiariaRequestDto request) {
        log.info("Validando duplicados para '{}' en el momento '{}'", request.getNombreAlimento(),
                request.getMomento());

        // Verificar si ya existe en este plan
        boolean existe = comidaRepository.existsByPlanIdAndMomentoIgnoreCaseAndNombreAlimentoIgnoreCase(
                request.getPlanNutricionalId(),
                request.getMomento(),
                request.getNombreAlimento());

        if (existe) {
            log.warn("Intento de duplicado detectado: {} ya existe en {}", request.getNombreAlimento(),
                    request.getMomento());
            throw new IllegalArgumentException(
                    "Ya tienes registrado '" + request.getNombreAlimento() + "' en el " + request.getMomento());
        }

        // Si no existe, seguimos con el proceso normal
        Nutricion plan = nutricionRepository.findById(request.getPlanNutricionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        ComidaDiaria nuevaComida = comidaMapper.toEntity(request, plan);

        if (nuevaComida.getFecha() == null) {
            nuevaComida.setFecha(LocalDate.now());
        }
        return comidaMapper.toResponseDto(comidaRepository.save(nuevaComida));
    }

    @Override
    @Transactional
    public void eliminarComida(Long id) {
        log.info("Solicitud para eliminar comida con ID: {}", id);
        if (!comidaRepository.existsById(id)) {
            log.warn("Intento de eliminar comida inexistente con ID: {}", id);
            throw new ResourceNotFoundException("Comida no encontrada con ID: " + id);
        }
        comidaRepository.deleteById(id);
        log.info("Comida ID {} eliminada correctamente", id);
    }

}