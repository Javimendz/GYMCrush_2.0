package com.backend.service;

import com.backend.domain.CategoriaDieta;
import com.backend.domain.Dieta;
import com.backend.dto.DietaRequestDto;
import com.backend.dto.DietaResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.DietaMapper;
import com.backend.repository.CategoriaDietaRepository;
import com.backend.repository.DietaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DietaServiceImp implements IDietaService {

    private final DietaRepository dietaRepository;
    private final DietaMapper dietaMapper;
    private final CategoriaDietaRepository categoriaRepository;

    @Override
    @Transactional
    public DietaResponseDto crearDieta(DietaRequestDto dto) {
        log.info("Iniciando creación de dieta base: {}", dto.getNombre());

        // Evitar dietas con nombre duplicado en el catálogo
        if (dietaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            log.warn("Ya existe una dieta con el nombre: {}", dto.getNombre());
            throw new IllegalArgumentException("La dieta '" + dto.getNombre() + "' ya existe en el catálogo.");
        }

        // Convertimos el DTO a la entidad inicial
        Dieta dieta = dietaMapper.toEntity(dto);

        // Asignación de Categoría
        if (dto.getCategoriaDietaId() != null) {
            log.debug("Asociando categoría ID {} a la nueva dieta", dto.getCategoriaDietaId());
            CategoriaDieta categoria = categoriaRepository.findById(dto.getCategoriaDietaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se puede crear la dieta: Categoría no encontrada con ID: "
                                    + dto.getCategoriaDietaId()));
            dieta.setCategoriaDieta(categoria);
        }

        Dieta guardada = dietaRepository.save(dieta);
        log.info("Dieta base '{}' creada exitosamente con ID: {}", guardada.getNombre(), guardada.getId());

        return dietaMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietaResponseDto> listarTodas() {
        log.info("Recuperando todas las dietas del catálogo maestro");
        List<Dieta> dietas = dietaRepository.findAll();
        log.debug("Se encontraron {} dietas en total", dietas.size());
        return dietaMapper.toResponseDtoList(dietas);
    }

    @Override
    @Transactional(readOnly = true)
    public DietaResponseDto obtenerPorId(Long id) {
        log.info("Buscando detalle de dieta con ID: {}", id);
        return dietaRepository.findById(id)
                .map(dieta -> {
                    log.debug("Dieta localizada: {}", dieta.getNombre());
                    return dietaMapper.toResponseDto(dieta);
                })
                .orElseThrow(() -> {
                    log.error("Dieta con ID {} no existe", id);
                    return new ResourceNotFoundException("Dieta no encontrada con ID: " + id);
                });
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando dieta con ID: {}", id);
        if (!dietaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Dieta no encontrada con ID: " + id);
        }
        dietaRepository.deleteById(id);
        log.info("Dieta {} eliminada correctamente", id);
    }
}