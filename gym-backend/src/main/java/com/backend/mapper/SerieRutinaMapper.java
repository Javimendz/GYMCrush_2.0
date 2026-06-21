package com.backend.mapper;


import com.backend.domain.SerieRutina;
import com.backend.dto.SerieRutinaResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SerieRutinaMapper {

    SerieRutinaResponseDto toResponseDto(SerieRutina serieRutina);
}