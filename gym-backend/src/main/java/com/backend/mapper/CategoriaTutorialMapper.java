package com.backend.mapper;

import com.backend.domain.CategoriaTutorial;
import com.backend.dto.CategoriaTutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper de MapStruct para la entidad {@link CategoriaTutorial}.
 * <p>
 * Convierte entre la entidad CategoriaTutorial y sus DTOs.
 * Incluye lógica personalizada para calcular la cantidad de videos
 * de forma lazy (sin cargar la lista completa de tutoriales).
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring")
public interface CategoriaTutorialMapper {

    /**
     * Convierte una entidad CategoriaTutorial a DTO de respuesta.
     * Calcula la cantidad de videos de la categoría sin cargar la lista completa.
     *
     * @param entity la entidad a convertir
     * @return el DTO de respuesta con cantidad de videos
     */
    @Mapping(target = "cantidadVideos", expression = "java(entity.getTutoriales() != null ? entity.getTutoriales().size() : 0)")
    CategoriaTutorialResponseDto toDto(CategoriaTutorial entity);

    /**
     * Convierte un DTO de request a entidad CategoriaTutorial.
     * Ignora la lista de tutoriales (se maneja por separado) y el ID.
     *
     * @param dto el DTO con los datos de la categoría
     * @return la nueva entidad CategoriaTutorial
     */
    @Mapping(target = "tutoriales", ignore = true)
    @Mapping(target = "id", ignore = true)
    CategoriaTutorial toEntity(CategoriaTutorialRequestDto dto);
}