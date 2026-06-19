package com.backend.mapper;

import com.backend.domain.Dieta;
import com.backend.dto.DietaRequestDto;
import com.backend.dto.DietaResponseDto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DietaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planes", ignore = true)
    @Mapping(target = "categoriaDieta", ignore = true)
    Dieta toEntity(DietaRequestDto requestDto);

    List<DietaResponseDto> toResponseDtoList(List<Dieta> dietas);

    @Mapping(target = "categoriaDietaId", source = "categoriaDieta.id")
    @Mapping(target = "nombreCategoria", source = "categoriaDieta.nombre")
    DietaResponseDto toResponseDto(Dieta dieta);


}
