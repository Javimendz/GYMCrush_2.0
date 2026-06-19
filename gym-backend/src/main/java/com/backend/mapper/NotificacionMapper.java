package com.backend.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.backend.domain.Notificacion;
import com.backend.dto.NotificacionRequestDto;
import com.backend.dto.NotificacionResponseDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificacionMapper {

    // MAPEO DE REQUEST A ENTIDAD
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true) // El usuario se setea en el Service
    @Mapping(target = "leido", constant = "false")
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    Notificacion toEntity(NotificacionRequestDto dto);

    // MAPEO DE ENTIDAD A RESPONSE
    @Mapping(target = "fecha", source = "fechaCreacion", qualifiedByName = "mapFecha")
    NotificacionResponseDto toResponseDto(Notificacion entity);

    // Lógica personalizada para formatear la fecha de LocalDateTime a String
    @Named("mapFecha")
    default String mapFecha(LocalDateTime fecha) {
        if (fecha == null)
            return null;
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}