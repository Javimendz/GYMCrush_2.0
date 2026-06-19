package com.backend.mapper;

import com.backend.domain.Rutina;
import com.backend.dto.RutinaRequestDto;
import com.backend.dto.RutinaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RutinaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "entrenamiento", ignore = true)
    @Mapping(target = "series", ignore = true)
    @Mapping(target = "peso", ignore = true)
    @Mapping(target = "repeticiones", ignore = true)
    Rutina toEntity(RutinaRequestDto dto);

    // Entidad Rutina -> RutinaResponseDto
    @Mapping(target = "entrenamientoId", source = "entrenamiento.id")
    @Mapping(target = "nombreEntrenamiento", source = "entrenamiento.nombre")
    @Mapping(target = "intensidad", source = "entrenamiento.intensidad")
    @Mapping(target = "duracion", source = "entrenamiento.duracion")
    @Mapping(target = "urlImagen", source = "entrenamiento.urlImagen")
    RutinaResponseDto toResponseDto(Rutina rutina);
}