package com.backend.dto.wger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WgerExerciseInfoObjectDto {

    private Long id; // Importante: Wger devuelve el ID del ejercicio
    private List<TranslationDto> translations;
    private List<WgerImageDto> images;

    public String getName() {
        if (translations == null || translations.isEmpty()) return "Sin nombre";
        return translations.stream()
            .filter(t -> t.getLanguage() == 4) // Asegúrate que el lenguaje coincida con el enviado
            .map(TranslationDto::getName)
            .filter(n -> n != null && !n.isBlank())
            .findFirst()
            .orElse(translations.get(0).getName());
    }

    public String getDescription() {
        if (translations == null || translations.isEmpty()) return "";
        return translations.stream()
            .filter(t -> t.getLanguage() == 4)
            .map(TranslationDto::getTexto)
            .filter(d -> d != null && !d.isBlank())
            .findFirst()
            .orElse("");
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TranslationDto {
        private String name;
        @JsonProperty("description")
        private String texto;
        private int language;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WgerImageDto {
        @JsonProperty("image")
        private String imageUrl;
    }
}