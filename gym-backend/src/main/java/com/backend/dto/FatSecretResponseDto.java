package com.backend.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
public class FatSecretResponseDto {
    private RecipeWrapper recipes;

    @Data
    public static class RecipeWrapper {
        @JsonProperty("max_results")
        private String maxResults;
        @JsonProperty("page_number")
        private String pageNumber;
        @JsonProperty("total_results")
        private String totalResults;
        private List<FatSecretRecipeDto> recipe;
    }
}