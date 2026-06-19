package com.backend.mapper;

import com.backend.domain.DetallePlan;
import com.backend.dto.DetallePlanRequestDto;
import com.backend.dto.DetallePlanResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DetallePlanMapper {

    /**
     * De Entidad a Response DTO
     * Extraemos el ID y Nombre del objeto Entrenamiento anidado
     */
    @Mapping(target = "entrenamientoId", source = "entrenamiento.id")
    @Mapping(target = "nombreEntrenamiento", source = "entrenamiento.nombre")
    @Mapping(target = "descripcionEntrenamiento", source = "entrenamiento.descripcion")
    DetallePlanResponseDto toResponseDto(DetallePlan entity);

    /**
     * De Request DTO a Entidad
     * El entrenamiento y el plan se asignan manualmente en el Service
     * mediante sus IDs, por eso los ignoramos aquí.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan", ignore = true)
    @Mapping(target = "entrenamiento", ignore = true)
    DetallePlan toEntity(DetallePlanRequestDto dto);

    List<DetallePlanResponseDto> toResponseDtoList(List<DetallePlan> entities);
}