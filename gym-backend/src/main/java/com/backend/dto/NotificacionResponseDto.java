package com.backend.dto;

import com.backend.domain.enums.TipoNotificacion;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificacionResponseDto {
    private Long id;
    private String titulo;
    private String mensaje;
    private String fecha; // Enviamos la fecha como String formateado para Android
    private boolean leido;
    private TipoNotificacion tipo;
}
