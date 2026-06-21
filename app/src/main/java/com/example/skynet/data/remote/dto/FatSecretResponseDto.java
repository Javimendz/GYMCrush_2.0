package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class FatSecretResponseDto implements Serializable {
    @SerializedName("recipes")
    private RecipesContainer recipes;

    public RecipesContainer getRecipes() { return recipes; }

    public static class RecipesContainer implements Serializable {
        @SerializedName("recipe")
        private List<FatSecretRecipeDto> recipe;

        @SerializedName("total_results")
        private String totalResults;

        public List<FatSecretRecipeDto> getRecipe() { return recipe; }
        public String getTotalResults() { return totalResults; }
    }
}
