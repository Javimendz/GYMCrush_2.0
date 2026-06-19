package com.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO con el detalle del progreso y datos del tutorial")
public class VisualizacionResponseDto {

    @Schema(description = "ID del registro de visualización")
    private Long id;

    @Schema(description = "Fecha de la última vez que se vio")
    private LocalDateTime fechaVisualizacion;

    @Schema(description = "Segundos de progreso guardados")
    private Integer progresoSegundos;

    @Schema(description = "Número total de veces que se ha reproducido este tutorial")
    private Integer contadorReproducciones;

    @Schema(description = "Estado de finalización")
    private Boolean completado;

    // Datos del Usuario
    private Long usuarioId;

    @Schema(description = "ID del video")
    private Long tutorialId;

    @Schema(description = "Título para mostrar en la lista")
    private String tituloTutorial;

    @Schema(description = "Enlace al recurso de video")
    private String urlVideo;

    @Schema(description = "Duración total para calcular barra de progreso (%)")
    private Integer duracionTotalVideo;

    @Schema(description = "Categoría (ej: Yoga, Pecho, HIIT)")
    private String nombreCategoria;
}