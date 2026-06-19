package com.backend.mapper;

import com.backend.domain.Entrenamiento;
import com.backend.domain.Tutorial;
import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy; 


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntrenamientoMapper {

//  Entity -> DTO (Para listas simples)
    @Mapping(target = "categoria", source = "categoria.nombre")
    EntrenamientoResponseDto toDto(Entrenamiento entrenamiento);

    //  DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tutoriales", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    Entrenamiento toEntity(EntrenamientoRequestDto dto);

    @Mapping(target = "id", source = "entrenamiento.id")
    @Mapping(target = "nombre", source = "entrenamiento.nombre")
    @Mapping(target = "descripcion", source = "entrenamiento.descripcion")
    @Mapping(target = "duracion", source = "entrenamiento.duracion") // 🔥 FALTABA
    @Mapping(target = "intensidad", source = "entrenamiento.intensidad") // 🔥 FALTABA
    @Mapping(target = "urlImagen", source = "entrenamiento.urlImagen") // 🔥 FALTABA
    @Mapping(target = "urlVideo", source = "tutorial.urlVideo")
    @Mapping(target = "categoria", source = "entrenamiento.categoria.nombre")
    EntrenamientoResponseDto toResponseDto(Entrenamiento entrenamiento, Tutorial tutorial);
}