package com.backend.dto;

import com.backend.domain.enums.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.backend.repository.NotificacionRepository;
import com.backend.service.NotificacionService;
import com.backend.dto.NotificacionRequestDto; 

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequestDto {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @NotBlank(message = "El mensaje no puede estar vacío")
    private String mensaje;

    @NotNull(message = "El tipo de notificación es obligatorio")
    private TipoNotificacion tipo;
}