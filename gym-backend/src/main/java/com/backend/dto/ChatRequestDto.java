package com.backend.dto;

import com.backend.domain.enums.EnumMessageType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para solicitudes de chat.
 * <p>
 * Esta clase representa la estructura de datos que los clientes envían
 * al servidor cuando desean enviar un mensaje en el chat. Es una implementación
 * de Java Record, lo que la hace inmutable y concisa.
 * </p>
 * 
 * <p>
 * <b>Uso principal:</b>
 * <ul>
 * <li>Es el único DTO que envían los clientes desde Android</li>
 * <li>Se utiliza en endpoints WebSocket para comunicación en tiempo real</li>
 * <li>Soporta diferentes tipos de mensajes (texto, imagen, video, etc.)</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Características del Record:</b>
 * <ul>
 * <li>Inmutable por defecto</li>
 * <li>Constructor generado automáticamente</li>
 * <li>Métodos accessor generados automáticamente</li>
 * <li>equals(), hashCode() y toString() implementados</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Schema(description = "DTO para la solicitud de chat")
public record ChatRequestDto(

        /**
         * Contenido del mensaje que se enviará al chat.
         * <p>
         * Puede ser texto plano, URL de imagen, URL de video, etc.
         * El formato depende del tipo de mensaje especificado.
         * </p>
         * 
         * <p>
         * <b>Ejemplos según tipo:</b>
         * <ul>
         * <li><b>TEXTO</b>: "Hola, ¿cómo estás?"</li>
         * <li><b>IMAGEN</b>: "https://example.com/image.jpg"</li>
         * <li><b>VIDEO</b>: "https://example.com/video.mp4"</li>
         * </ul>
         * </p>
         */
        @Schema(description = "Contenido del mensaje", example = "Hola, ¿cómo estás?") String contenido,

        /**
         * Tipo de mensaje que se está enviando.
         * <p>
         * Define cómo debe interpretarse el contenido del mensaje.
         * Los valores posibles están definidos en el enum {@link EnumMessageType}.
         * </p>
         * 
         * <p>
         * <b>Tipos soportados:</b>
         * <ul>
         * <li><b>TEXTO</b>: Mensajes de texto plano</li>
         * <li><b>IMAGEN</b>: Imágenes (generalmente URLs)</li>
         * <li><b>VIDEO</b>: Videos (generalmente URLs)</li>
         * <li><b>AUDIO</b>: Mensajes de audio</li>
         * <li><b>ARCHIVO</b>: Archivos adjuntos</li>
         * </ul>
         * </p>
         */
        @Schema(description = "Tipo de mensaje (texto, imagen, video, etc.)", example = "TEXTO") EnumMessageType tipo) {
}