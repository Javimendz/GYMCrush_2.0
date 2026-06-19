package com.backend.service;

import com.backend.domain.Sala;
import com.backend.dto.SalaRequestDto;
import com.backend.dto.SalaResponseDto;
import com.backend.repository.SalaRepository;
import com.backend.mapper.SalaMapper;
import com.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalaServiceImp implements ISalaService {

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SalaResponseDto> listarTodas() {
        log.info("Recuperando listado completo de salas");
        return salaRepository.findAll().stream()
                .map(salaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaResponseDto> listarActivas() {
        log.info("Filtrando salas operativas");
        return salaRepository.findByActivaTrue().stream()
                .map(salaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SalaResponseDto obtenerPorId(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con ID: " + id));
        return salaMapper.toResponse(sala);
    }

    @Override
    @Transactional
    public SalaResponseDto crear(SalaRequestDto dto) {
        log.info("Creando nueva sala: {}", dto.getNombre());

        // No permitir nombres duplicados
        if (salaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RuntimeException("Ya existe una sala registrada con el nombre: " + dto.getNombre());
        }

        Sala nuevaSala = salaMapper.toEntity(dto);
        // Por defecto al crearla, la marcamos como activa
        nuevaSala.setActiva(true);

        return salaMapper.toResponse(salaRepository.save(nuevaSala));
    }

    @Override
    @Transactional
    public SalaResponseDto actualizar(Long id, SalaRequestDto dto) {
        log.info("Actualizando datos de la sala ID: {}", id);

        Sala existente = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Sala no encontrada"));

        // Validar si el nuevo nombre ya lo tiene OTRA sala
        if (!existente.getNombre().equalsIgnoreCase(dto.getNombre()) &&
                salaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RuntimeException("El nombre '" + dto.getNombre() + "' ya está en uso por otra sala.");
        }

        existente.setNombre(dto.getNombre());
        existente.setCapacidadMax(dto.getCapacidadMax());
        existente.setDescripcion(dto.getDescripcion());
        existente.setUbicacion(dto.getUbicacion());
        existente.setEquipamiento(dto.getEquipamiento());

        return salaMapper.toResponse(salaRepository.save(existente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Sala no encontrada"));

        // No eliminar si tiene clases programadas
        if (!sala.getHorarios().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la sala porque tiene horarios de clases asignados. " +
                    "Pruebe a desactivarla en su lugar.");
        }

        salaRepository.delete(sala);
        log.info("Sala ID: {} eliminada físicamente", id);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, Boolean activa) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada"));

        sala.setActiva(activa);
        salaRepository.save(sala);
        log.info("Sala '{}' marcada como {}", sala.getNombre(), activa ? "ACTIVA" : "INACTIVA (Mantenimiento)");
    }
}