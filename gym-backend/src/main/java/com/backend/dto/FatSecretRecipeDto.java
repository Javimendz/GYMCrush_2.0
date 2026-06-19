package com.backend.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class FatSecretRecipeDto {
    @JsonProperty("recipe_description")
    private String recipeDescription;
    @JsonProperty("recipe_id")
    private String recipeId;
    @JsonProperty("recipe_image")
    private String recipeImage;
    @JsonProperty("recipe_name")
    private String recipeName;
    @JsonProperty("recipe_url")
    private String recipeUrl;
    @JsonProperty("recipe_nutrition")
    private FatSecretNutritionDto nutrition;
}