package com.backend.mapper;

import com.backend.domain.DetallePlan;
import com.backend.domain.PlanEntrenamiento;
import com.backend.dto.DetallePlanResponseDto;
import com.backend.dto.PlanRequestDto;
import com.backend.dto.PlanResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlanMapper {

  // Extraemos el ID del objeto Usuario asociado a la entidad PlanEntrenamiento
    @Mapping(source = "usuario.id", target = "usuarioId")
    PlanResponseDto toResponseDto(PlanEntrenamiento entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ejercicios", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "esGlobal", ignore = true) // Se determina en el servicio según el rol del usuario
    PlanEntrenamiento toEntity(PlanRequestDto dto);

    @Mapping(target = "entrenamientoId", source = "entrenamiento.id")
    @Mapping(target = "nombreEntrenamiento", source = "entrenamiento.nombre")
    @Mapping(target = "descripcionEntrenamiento", source = "entrenamiento.descripcion")
    DetallePlanResponseDto toDetalleResponseDto(DetallePlan entity);
}