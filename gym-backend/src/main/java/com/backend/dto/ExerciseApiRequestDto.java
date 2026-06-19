package com.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor // Obligatorio para Jackson
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExerciseApiRequestDto {

    private String id;
    private String name;
    private String bodyPart;
    private String equipment;
    private String target;

    @JsonProperty("gifUrl")
    private String gifUrl;

    private List<String> secondaryMuscles;
    private List<String> instructions;
    private String description;
    private String difficulty;
    private String category;
}