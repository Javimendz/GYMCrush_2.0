package com.backend.dto;

import lombok.Data;

@Data
public class NutricionRequestDto {
    private Long usuarioId;
    private Double peso; // Cambiado a 'peso' para coincidir con tu Service
    private Double altura; // Recuerda: Enviar en CM (176) no en Metros (1.76)
    private Integer edad;
    private String genero; // "MASCULINO" o "FEMENINO"
    private String objetivo; // "VOLUMEN", "DEFINICION", "MANTENIMIENTO"
    private String nivelActividad;// "SEDENTARIO", "LIGERO", "MODERADO", "INTENSO", "ATLETA"
}