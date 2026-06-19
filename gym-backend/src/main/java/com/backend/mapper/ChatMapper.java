package com.backend.mapper;

import org.springframework.stereotype.Component;
import com.backend.domain.MensajeChat;
import com.backend.dto.ChatResponseDto;

/**
 * Mapper manual para convertir entidades {@link MensajeChat} a DTOs de
 * respuesta.
 * <p>
 * Este mapper se utiliza en el sistema de chat WebSocket para transformar
 * los mensajes guardados en la base de datos antes de enviarlos a los clientes.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Component
public class ChatMapper {

    /**
     * Convierte una entidad MensajeChat a su DTO de respuesta.
     *
     * @param entidad la entidad MensajeChat a convertir
     * @return el DTO de respuesta, o null si la entidad es null
     */
    public ChatResponseDto toDto(MensajeChat entidad) {
        if (entidad == null)
            return null;

        return ChatResponseDto.builder()
                .id(entidad.getId())
                .contenido(entidad.getContenido())
                .emisor(entidad.getEmisor())
                .fechaEnvio(entidad.getFechaEnvio())
                .tipo(entidad.getTipo())
                .build();
    }
}