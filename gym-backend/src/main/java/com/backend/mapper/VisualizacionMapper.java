package com.backend.mapper;

import com.backend.domain.Visualizacion;
import com.backend.dto.VisualizacionRequestDto;
import com.backend.dto.VisualizacionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VisualizacionMapper {

    /**
     * De DTO (Android) a Entidad (DB)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "tutorial", ignore = true)
    @Mapping(target = "fechaVisualizacion", ignore = true) 
    @Mapping(target = "ultimaActualizacion", ignore = true)
    Visualizacion toEntity(VisualizacionRequestDto requestDto);

    /**
     * De Entidad (DB) a DTO (Android)
     */
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "tutorialId", source = "tutorial.id")
    @Mapping(target = "tituloTutorial", source = "tutorial.titulo")
    @Mapping(target = "urlVideo", source = "tutorial.urlVideo")
    @Mapping(target = "duracionTotalVideo", source = "tutorial.duracionMin")
    @Mapping(target = "nombreCategoria", source = "tutorial.categoria.nombre")
    @Mapping(source = "fechaVisualizacion", target = "fechaVisualizacion")
    VisualizacionResponseDto toResponseDto(Visualizacion visualizacion);
}