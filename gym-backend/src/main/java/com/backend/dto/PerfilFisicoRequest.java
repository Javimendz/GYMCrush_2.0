package com.backend.dto;

import com.backend.domain.enums.EnumNivelActividad;
import com.backend.domain.enums.EnumObjetivo;

import lombok.Data;

@Data
public class PerfilFisicoRequest {
    private Long usuarioId;

    private Double peso;

    private Double altura;

    private EnumObjetivo objetivo;
    private EnumNivelActividad nivelActividad;

}
