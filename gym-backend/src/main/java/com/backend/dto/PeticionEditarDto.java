package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeticionEditarDto {
    private Long id;           // ID del mensaje a editar
    private String contenido;  // Nuevo texto del mensaje
}