package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para solicitudes de acceso al gimnasio mediante
 * token QR.
 * <p>
 * Esta clase representa la estructura de datos necesaria para que un cliente
 * solicite acceso al gimnasio utilizando un token QR generado previamente.
 * Es utilizada en el endpoint de autenticación para validar y procesar las
 * solicitudes de entrada al gimnasio.
 * </p>
 * 
 * <p>
 * <b>Flujo de uso:</b>
 * <ol>
 * <li>El cliente escanea un código QR y obtiene un token</li>
 * <li>Envía este DTO al endpoint de acceso</li>
 * <li>El servidor valida el token y concede acceso</li>
 * </ol>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@AllArgsConstructor // Genera constructor con todos los parámetros
@NoArgsConstructor // Genera constructor sin parámetros
@Schema(description = "DTO para el acceso al chat mediante un token QR")
public class AccesoRequestDto {

    /**
     * Token QR obtenido al escanear el código QR.
     * <p>
     * Este token es generado por el sistema y tiene una validez temporal.
     * Es obligatorio y no puede estar vacío para procesar la solicitud de acceso.
     * </p>
     * 
     * <p>
     * <b>Formato esperado:</b> Cadena alfanumérica de 6 caracteres
     * <b>Ejemplo:</b> "123456"
     * </p>
     */
    @NotBlank(message = "El token del QR es obligatorio")
    @Schema(description = "Token QR para acceder al chat", example = "123456")
    private String token;
}