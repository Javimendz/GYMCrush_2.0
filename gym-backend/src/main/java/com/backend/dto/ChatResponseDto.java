package com.backend.dto;

import java.time.LocalDateTime;
import com.backend.domain.enums.EnumMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para respuestas de chat.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se envía o recibe un mensaje del chat. Incluye información
 * completa sobre el mensaje como identificador, contenido, emisor,
 * timestamp y tipo de mensaje.
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Respuesta a envíos de mensajes en el chat</li>
 * <li>Distribución de mensajes a través de WebSocket</li>
 * <li>Historial de conversaciones</li>
 * <li>Notificaciones en tiempo real</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Campos mejorados:</b>
 * <ul>
 * <li>{@code contenido}: Renombrado de 'mensaje' para mayor claridad</li>
 * <li>{@code emisor}: Campo añadido para identificar al remitente</li>
 * <li>{@code fechaEnvio}: Mejorado con LocalDateTime para mayor precisión</li>
 * <li>{@code tipo}: Campo añadido para soportar múltiples tipos de mensaje</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Builder // Permite la construcción fluida de objetos
@AllArgsConstructor // Genera constructor con todos los parámetros
@NoArgsConstructor // Genera constructor sin parámetros
@Schema(description = "DTO para la respuesta de chat")
public class ChatResponseDto {

    /**
     * Identificador único del mensaje en la base de datos.
     * <p>
     * Es generado automáticamente cuando se guarda el mensaje
     * y sirve como referencia única para el mensaje específico.
     * </p>
     */
    @Schema(description = "ID único del mensaje de chat", example = "1")
    private Long id;

    /**
     * Contenido del mensaje enviado.
     * <p>
     * Contiene el texto, URL de imagen, video o cualquier otro
     * contenido del mensaje. El formato depende del tipo de mensaje.
     * </p>
     * 
     * <p>
     * <b>Nota:</b> Anteriormente se llamaba 'mensaje', se renombró
     * a 'contenido' para mayor claridad y consistencia.
     * </p>
     */
    @Schema(description = "Contenido del mensaje", example = "Hola, ¿cómo estás?")
    private String contenido;

    /**
     * Identificador del usuario que envió el mensaje.
     * <p>
     * Contiene el nombre de usuario o identificador único del remitente.
     * Es fundamental para identificar quién envió cada mensaje
     * en conversaciones con múltiples participantes.
     * </p>
     * 
     * <p>
     * <b>Nota:</b> Campo añadido en esta versión para mejorar
     * el seguimiento de mensajes en el chat.
     * </p>
     */
    @Schema(description = "Emisor del mensaje", example = "usuario123")
    private String emisor;

    /**
     * Fecha y hora exacta en que se envió el mensaje.
     * <p>
     * Se establece automáticamente cuando se crea el mensaje
     * y se utiliza para ordenar la conversación cronológicamente.
     * Formato ISO 8601: yyyy-MM-ddTHH:mm:ss
     * </p>
     * 
     * <p>
     * <b>Nota:</b> Anteriormente se llamaba 'fechaHora' y usaba LocalDate.
     * Se mejoró a LocalDateTime con {@code fechaEnvio} para mayor precisión.
     * </p>
     */
    @Schema(description = "Fecha y hora de envío del mensaje", example = "2023-10-15T12:30:00")
    private LocalDateTime fechaEnvio;

    /**
     * Tipo de mensaje que se está enviando.
     * <p>
     * Define cómo debe interpretarse y mostrarse el contenido del mensaje.
     * Los valores posibles están definidos en el enum {@link EnumMessageType}.
     * </p>
     * 
     * <p>
     * <b>Nota:</b> Campo añadido en esta versión para soportar
     * diferentes tipos de contenido en el chat.
     * </p>
     */
    @Schema(description = "Tipo de mensaje (texto, imagen, video, etc.)", example = "TEXTO")
    private EnumMessageType tipo;
}