package com.backend.dto.wger;



import lombok.Data;
import java.util.List;

@Data
public class WgerInfoResponseDto {
    private Integer count;    // Total de ejercicios en Wger
    private String next;      // URL de la siguiente página (null si es la última)
    private String previous;  // URL de la página anterior
    private List<WgerExerciseInfoObjectDto> results;
}