package com.backend.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import com.backend.domain.enums.EnumMessageType;

/**
 * Entidad que representa un mensaje individual de chat.
 * <p>
 * Almacena mensajes con tipo específico, emisor y timestamp
 * para facilitar la clasificación y ordenamiento de comunicaciones.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "mensajes_chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeChat {

    /**
     * Identificador único del mensaje de chat.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id")
    private String ticketId;
    /**
     * Contenido del mensaje enviado.
     * <p>
     * Obligatorio, almacena el texto de la comunicación.
     * </p>
     */
    private String contenido;

    /**
     * Identificador del emisor del mensaje.
     * <p>
     * Obligatorio, indica quién envió el mensaje.
     * </p>
     */
    private String emisor;
    
    /**
     * Fecha y hora de envío del mensaje.
     * <p>
     * Obligatorio, registra cuándo se envió el mensaje.
     * </p>
     */
    private LocalDateTime fechaEnvio;

    /**
     * Tipo de mensaje para clasificación.
     * <p>
     * Obligatorio, permite categorizar mensajes
     * (ej: TEXTO, IMAGEN, SISTEMA).
     * </p>
     */
    @Enumerated(EnumType.STRING)
    private EnumMessageType tipo;

    private Long usuarioId;
}