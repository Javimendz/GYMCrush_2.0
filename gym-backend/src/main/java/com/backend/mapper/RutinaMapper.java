package com.backend.mapper;

import com.backend.domain.Rutina;
import com.backend.dto.RutinaRequestDto;
import com.backend.dto.RutinaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SerieRutinaMapper.class})
public interface RutinaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "entrenamiento", ignore = true)
    @Mapping(target = "detalleSeries", ignore = true) // Ignoramos porque las mapeamos a mano en el ServiceImp
    Rutina toEntity(RutinaRequestDto dto);

    @Mapping(target = "entrenamientoId", source = "entrenamiento.id")
    @Mapping(target = "nombreEntrenamiento", source = "entrenamiento.nombre")
    @Mapping(target = "intensidad", source = "entrenamiento.intensidad")
    @Mapping(target = "duracion", source = "entrenamiento.duracion")
    @Mapping(target = "urlImagen", source = "entrenamiento.urlImagen")
    @Mapping(target = "nombrePlan", ignore = true)
    @Mapping(target = "series", source = "detalleSeries") // Mapea la lista de la entidad a la lista de tu RutinaResponseDto
    RutinaResponseDto toResponseDto(Rutina rutina);
}