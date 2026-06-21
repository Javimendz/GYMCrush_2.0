package com.backend.dto.wger;



import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WgerInfoResponseDto {
    private Integer count;
    private String next;
    private String previous;
    private List<WgerExerciseInfoObjectDto> results;
}