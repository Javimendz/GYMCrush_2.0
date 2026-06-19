package com.backend.mapper;

import com.backend.domain.Tutorial;
import com.backend.dto.TutorialRequestDto;
import com.backend.dto.TutorialResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper de MapStruct para la entidad {@link Tutorial}.
 * <p>
 * Convierte entre la entidad Tutorial y sus DTOs de request/response.
 * Incluye mapeos personalizados para extraer información de la categoría
 * asociada.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring")
public interface TutorialMapper {

    /**
     * Convierte una entidad Tutorial a DTO de respuesta.
     * Extrae el ID y nombre de la categoría asociada.
     *
     * @param entity la entidad a convertir
     * @return el DTO de respuesta
     */
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "nombreCategoria", source = "categoria.nombre")
    TutorialResponseDto toDto(Tutorial entity);

    /**
     * Convierte un DTO de request a entidad Tutorial.
     * Ignora el ID y la categoría (se busca el objeto Categoria en el Service).
     *
     * @param dto el DTO con los datos del tutorial
     * @return la nueva entidad Tutorial
     */
    @Mapping(target = "categoria", ignore = true) // Se busca el objeto Categoria en el Service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visualizaciones", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Tutorial toEntity(TutorialRequestDto dto);
}