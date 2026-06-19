package com.backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.Salud;
import com.backend.dto.SaludRequestDto;
import com.backend.dto.SaludResponseDto;

/**
 * Mapper de MapStruct para la entidad {@link Salud}.
 * <p>
 * Convierte entre la entidad Salud y sus DTOs de request/response.
 * Incluye mapeo personalizado para extraer el username del usuario asociado.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring")
public interface SaludMapper {

    /**
     * Convierte una entidad Salud a DTO de respuesta.
     * Extrae el username del usuario asociado.
     *
     * @param salud la entidad a convertir
     * @return el DTO de respuesta
     */
    @Mapping(target = "username", source = "usuario.username")
    SaludResponseDto toDto(Salud salud);

    /**
     * Convierte un DTO de request a entidad Salud.
     * Ignora el usuario, ID, fecha de medición e IMC (se calculan/calculan en el
     * servicio).
     *
     * @param dto el DTO con los datos de salud
     * @return la nueva entidad Salud
     */
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaMedicion", ignore = true)
    @Mapping(target = "imc", ignore = true)
    Salud toEntity(SaludRequestDto dto);

    /**
     * Convierte una lista de entidades Salud a lista de DTOs de respuesta.
     *
     * @param saludList la lista de entidades
     * @return la lista de DTOs
     */
    List<SaludResponseDto> toDtoList(List<Salud> saludList);
}
