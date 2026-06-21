package com.backend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class WgerResponseDto {
    private List<WgerExerciseDto> results;
}

@Data
 class WgerExerciseDto {
    private Long id;
    private String name;
    private String description;
    
    @JsonProperty("category")
    private Integer categoryId; // Wger mapea los músculos con IDs numéricos (ej: 10 = Pecho)
}