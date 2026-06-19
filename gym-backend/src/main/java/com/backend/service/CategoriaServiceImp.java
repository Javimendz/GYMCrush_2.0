package com.backend.service;

import com.backend.domain.Categoria;
import com.backend.dto.CategoriaRequestDto;
import com.backend.dto.CategoriaResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.CategoriaMapper;
import com.backend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para gestionar las categorías de actividades del
 * gimnasio.
 * (Cardio, Fuerza, Yoga, etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaServiceImp implements ICategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional
    public CategoriaResponseDto crear(CategoriaRequestDto dto) {
        log.info("Iniciando creación de categoría: {}", dto.getNombre());

        // Evitar nombres duplicados (ignorando mayúsculas)
        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            log.warn("La categoría '{}' ya existe", dto.getNombre());
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + dto.getNombre());
        }

        // Convertir DTO a Entidad
        Categoria entidad = categoriaMapper.toEntity(dto);

        // Guardar en la Base de Datos
        Categoria guardada = categoriaRepository.save(entidad);

        log.info("Categoría creada con éxito. ID asignado: {}", guardada.getId());
        return categoriaMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDto> listarTodas() {
        log.info("Obteniendo listado completo de categorías");
        List<Categoria> entidades = categoriaRepository.findAll();
        return categoriaMapper.toResponseDtoList(entidades);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDto obtenerPorId(Long id) {
        log.info("Buscando categoría por ID: {}", id);
        return categoriaRepository.findById(id)
                .map(categoriaMapper::toResponseDto)
                .orElseThrow(() -> {
                    log.error("ID {} no encontrado en categorías de actividad", id);
                    return new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
                });
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Solicitud para eliminar categoría ID: {}", id);

        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Categoría no encontrada");
        }

        categoriaRepository.deleteById(id);
        log.info("Categoría ID {} eliminada correctamente", id);
    }
}