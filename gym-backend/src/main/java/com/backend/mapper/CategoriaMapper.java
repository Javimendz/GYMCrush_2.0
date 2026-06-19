package com.backend.mapper;

import com.backend.domain.Categoria;
import com.backend.dto.CategoriaRequestDto;
import com.backend.dto.CategoriaResponseDto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaResponseDto toResponseDto(Categoria categoria);

    List<CategoriaResponseDto> toResponseDtoList(List<Categoria> categorias);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actividades", ignore = true) // Ignoramos la lista de actividades al crear
    Categoria toEntity(CategoriaRequestDto dto);
}