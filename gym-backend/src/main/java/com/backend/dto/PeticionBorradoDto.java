package com.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
/**
 * DTO para representar una petición de borrado de mensaje en el chat.
 * <p>
 * Este objeto se utiliza para enviar la información necesaria desde el cliente
 * (Android) al servidor cuando se solicita eliminar un mensaje específico.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeticionBorradoDto {
    private Long id;         // ID del mensaje a borrar
    private String usuario;  // Quién pide borrarlo (para validación)
}