package com.backend.mapper;

import com.backend.domain.CategoriaDieta;
import com.backend.dto.CategoriaDietaRequestDto;
import com.backend.dto.CategoriaDietaResponseDto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoriaDietaMapper {

    // Convierte la Entidad al DTO que verá Android
    CategoriaDietaResponseDto toResponseDto(CategoriaDieta categoria);

    List<CategoriaDietaResponseDto> toResponseDtoList(List<CategoriaDieta> categorias);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dietas", ignore = true)
    CategoriaDieta toEntity(CategoriaDietaRequestDto dto);

}
