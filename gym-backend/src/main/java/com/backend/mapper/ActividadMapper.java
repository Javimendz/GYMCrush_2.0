package com.backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.backend.domain.Actividad;
import com.backend.dto.ActividadRequestDto;
import com.backend.dto.ActividadResponseDto;

/**
 * Mapper de MapStruct para la entidad {@link Actividad}.
 * <p>
 * Convierte entre la entidad Actividad y sus DTOs de request/response.
 * </p>
 * <p>
 * Configuración:
 * <ul>
 * <li>componentModel = "spring": Permite inyección con @Autowired</li>
 * <li>unmappedTargetPolicy = IGNORE: Ignora campos no mapeados (como la lista
 * de horarios)</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ActividadMapper {

    /**
     * Convierte una entidad Actividad a DTO de respuesta.
     *
     * @param actividad la entidad a convertir
     * @return el DTO de respuesta
     */
    @Mapping(source = "categoria", target = "categoria")
    ActividadResponseDto toResponseDto(Actividad actividad);

    /**
     * Convierte una lista de entidades Actividad a lista de DTOs de respuesta.
     *
     * @param actividades la lista de entidades
     * @return la lista de DTOs
     */
    List<ActividadResponseDto> toResponseDtoList(List<Actividad> actividades);

    /**
     * Convierte un DTO de request a nueva entidad Actividad para crear.
     * Ignora el ID y los horarios (se asignan en el servicio).
     *
     * @param dto el DTO con los datos de la nueva actividad
     * @return la nueva entidad Actividad
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "horarios", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    Actividad toEntityCreate(ActividadRequestDto dto);

    /**
     * Actualiza una entidad Actividad existente con datos del DTO.
     * Ignora el ID para preservar el identificador original.
     *
     * @param dto       el DTO con los datos actualizados
     * @param actividad la entidad a actualizar
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    void updateActividadFromDto(ActividadRequestDto dto, @MappingTarget Actividad actividad);
}
