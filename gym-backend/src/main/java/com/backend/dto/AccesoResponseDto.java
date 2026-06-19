package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para respuestas de acceso al chat mediante token
 * QR.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente después
 * de procesar una solicitud de acceso al chat. Contiene información sobre
 * el resultado del intento de acceso, incluyendo datos del usuario y
 * timestamps.
 * </p>
 * 
 * <p>
 * <b>Información incluida:</b>
 * <ul>
 * <li>Identificador único del registro de acceso</li>
 * <li>Información del usuario que accedió</li>
 * <li>Timestamps de entrada y salida del chat</li>
 * <li>Tipo de acceso (ENTRADA o SALIDA)</li>
 * <li>Mensaje descriptivo del resultado</li>
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
@Schema(description = "DTO para la respuesta del acceso al chat mediante un token QR")
public class AccesoResponseDto {

    /**
     * Identificador único del registro de acceso.
     * <p>
     * Es generado automáticamente por la base de datos y sirve como
     * referencia única para este evento de acceso en particular.
     * </p>
     */
    @Schema(description = "ID único del acceso", example = "1001")
    private Long id;

    /**
     * Nombre de usuario que realizó el acceso al chat.
     * <p>
     * Corresponde al identificador único del usuario en el sistema.
     * Se utiliza para identificar quién realizó el intento de acceso.
     * </p>
     */
    @Schema(description = "Nombre de usuario que accedió al chat", example = "usuario123")
    private String username;

    /**
     * Fecha y hora en que el usuario ingresó al chat.
     * <p>
     * Se establece automáticamente cuando el usuario se conecta exitosamente.
     * Formato ISO 8601: yyyy-MM-ddTHH:mm:ss
     * </p>
     */
    @Schema(description = "Fecha y hora de entrada al chat", example = "2023-12-01T10:00:00")
    private LocalDateTime fechaHoraEntrada;

    /**
     * Fecha y hora en que el usuario salió del chat.
     * <p>
     * Se registra cuando el usuario se desconecta o cierra la sesión.
     * Puede ser null si el usuario todavía está conectado.
     * Formato ISO 8601: yyyy-MM-ddTHH:mm:ss
     * </p>
     */
    @Schema(description = "Fecha y hora de salida del chat", example = "2023-12-01T12:00:00")
    private LocalDateTime fechaHoraSalida;

    /**
     * Tipo de acceso registrado.
     * <p>
     * Puede ser:
     * <ul>
     * <li><b>ENTRADA</b>: Cuando el usuario se conecta al chat</li>
     * <li><b>SALIDA</b>: Cuando el usuario se desconecta del chat</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Tipo de acceso (ENTRADA o SALIDA)", example = "ENTRADA")
    private String tipo; // ENTRADA o SALIDA

    /**
     * Mensaje descriptivo sobre el resultado del acceso.
     * <p>
     * Proporciona información adicional sobre el estado del acceso,
     * como "Acceso concedido", "Token inválido", "Usuario no encontrado", etc.
     * </p>
     */
    @Schema(description = "Mensaje relacionado con el acceso", example = "Acceso concedido")
    private String mensaje; // Corregido el typo en el nombre del campo
}