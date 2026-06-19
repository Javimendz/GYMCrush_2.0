package com.backend.mapper;

import com.backend.domain.Horario;
import com.backend.dto.HorarioRequestDto;
import com.backend.dto.HorarioResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

/**
 * Mapper de MapStruct para la entidad {@link Horario}.
 * <p>
 * Convierte entre la entidad Horario y sus DTOs de request/response.
 * Incluye mapeos personalizados para extraer información de la actividad
 * asociada.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HorarioMapper {

    /**
     * Convierte una entidad Horario a DTO de respuesta.
     * Extrae el ID y nombre de la actividad asociada.
     *
     * @param horario la entidad a convertir
     * @return el DTO de respuesta con datos de la actividad
     */
// EN EL BACKEND (HorarioMapper.java)
@Mapping(source = "entrenador.perfil.nombre", target = "nombreEntrenador")
    @Mapping(source = "entrenador.id", target = "entrenadorId")
    @Mapping(target = "actividadId", source = "actividad.id")
    @Mapping(target = "nombreActividad", source = "actividad.nombre")
    @Mapping(target = "salaId", source = "sala.id")
    @Mapping(target = "nombreSala", source = "sala.nombre")
    HorarioResponseDto toResponseDto(Horario horario);
    /**
     * Convierte un DTO de request a entidad Horario.
     * Ignora el ID y la actividad (se asignan en el servicio).
     *
     * @param dto el DTO con los datos del horario
     * @return la nueva entidad Horario
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actividad", ignore = true)
    @Mapping(target = "sala", ignore = true)
    @Mapping(target = "entrenador", ignore = true)
    Horario toEntity(HorarioRequestDto dto);

    /**
     * Convierte una lista de entidades Horario a lista de DTOs de respuesta.
     *
     * @param horarios la lista de entidades
     * @return la lista de DTOs
     */
    List<HorarioResponseDto> toResponseDtoList(List<Horario> horarios);
}