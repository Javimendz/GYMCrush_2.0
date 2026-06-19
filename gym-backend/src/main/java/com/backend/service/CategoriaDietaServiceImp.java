package com.backend.service;

import com.backend.domain.CategoriaDieta;
import com.backend.dto.CategoriaDietaRequestDto;
import com.backend.dto.CategoriaDietaResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.CategoriaDietaMapper;
import com.backend.repository.CategoriaDietaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaDietaServiceImp implements ICategoriaDietaService {

    private final CategoriaDietaRepository categoriaRepository;
    private final CategoriaDietaMapper categoriaMapper;

    @Override
    @Transactional
    public CategoriaDietaResponseDto crear(CategoriaDietaRequestDto dto) {
        log.info("Solicitud para crear nueva categoría de dieta: {}", dto.getNombre());

        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            log.warn("Intento de creación duplicada: {}", dto.getNombre());
            throw new IllegalArgumentException("La categoría '" + dto.getNombre() + "' ya existe en el sistema.");
        }

        CategoriaDieta entidad = categoriaMapper.toEntity(dto);
        CategoriaDieta guardada = categoriaRepository.save(entidad);

        log.info("Categoría creada exitosamente con ID: {}", guardada.getId());
        return categoriaMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDietaResponseDto> listarTodas() {
        log.info("Obteniendo listado de todas las categorías de dieta");
        return categoriaMapper.toResponseDtoList(categoriaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDietaResponseDto obtenerPorId(Long id) {
        log.info("Buscando categoría de dieta por ID: {}", id);
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    log.debug("Categoría encontrada: {}", categoria.getNombre());
                    return categoriaMapper.toResponseDto(categoria);
                })
                .orElseThrow(() -> {
                    log.error("No se encontró la categoría con ID: {}", id);
                    return new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
                });
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Solicitud para eliminar categoría ID: {}", id);

        if (!categoriaRepository.existsById(id)) {
            log.error("Error al eliminar: ID {} no existe", id);
            throw new ResourceNotFoundException("No se puede eliminar: Categoría no encontrada");
        }

        categoriaRepository.deleteById(id);
        log.info("Categoría ID {} eliminada correctamente", id);
    }
}