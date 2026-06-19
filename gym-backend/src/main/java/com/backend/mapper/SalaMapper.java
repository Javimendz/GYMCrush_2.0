package com.backend.mapper;

import com.backend.domain.Sala;
import com.backend.dto.SalaRequestDto;
import com.backend.dto.SalaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalaMapper {

    /**
     * Convierte un DTO de request a entidad Sala.
     * 
     * @param requestDTO el DTO con los datos de la sala
     * @return la nueva entidad Sala
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "horarios", ignore = true)
    @Mapping(target = "nombre", source = "nombre")
    Sala toEntity(SalaRequestDto requestDTO);

    /**
     * Convierte una entidad Sala a DTO de respuesta.
     * * @param sala la entidad con los datos
     * 
     * @return el DTO para el frontend
     */
    SalaResponseDto toResponse(Sala sala);
}