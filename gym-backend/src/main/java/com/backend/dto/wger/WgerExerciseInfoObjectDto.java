package com.backend.dto.wger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class WgerExerciseInfoObjectDto {

    private List<TranslationDto> translations;
    private List<WgerImageDto> images;

    public String getName() {
        if (translations == null || translations.isEmpty()) return null;
        return translations.stream()
            .filter(t -> t.getLanguage() == 2)
            .map(TranslationDto::getName)
            .filter(n -> n != null && !n.isBlank())
            .findFirst()
            .orElseGet(() -> translations.get(0).getName());
    }

    public String getDescription() {
        if (translations == null || translations.isEmpty()) return null;
        return translations.stream()
            .filter(t -> t.getLanguage() == 2)
            .map(TranslationDto::getTexto)
            .filter(d -> d != null && !d.isBlank())
            .findFirst()
            .orElse(null);
    }

    @Data
    public static class TranslationDto {
        private String name;
        @JsonProperty("description")
        private String texto;
        private int language;
    }

    @Data
    public static class WgerImageDto {
        @JsonProperty("image")
        private String imageUrl;
    }
}
