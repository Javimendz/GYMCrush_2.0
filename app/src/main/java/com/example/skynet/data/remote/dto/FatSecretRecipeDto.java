package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class FatSecretRecipeDto implements Serializable {
    @SerializedName("recipe_id")
    private String recipeId;

    @SerializedName("recipe_name")
    private String recipeName;

    @SerializedName(value = "recipe_description", alternate = {"recipeDescription", "description", "descripcion"})
    private String recipeDescription;

    @SerializedName(value = "recipe_image", alternate = {"recipeImage", "image", "imagenUrl"})
    private String recipeImage;

    @SerializedName(value = "recipe_url", alternate = {"recipeUrl", "url"})
    private String recipeUrl;

    @SerializedName("recipe_nutrition")
    private RecipeNutritionDto recipeNutrition;

    public String getRecipeId() { return recipeId; }
    public String getRecipeName() { return recipeName; }
    public String getRecipeDescription() { return recipeDescription; }
    public String getRecipeImage() { return recipeImage; }
    public String getRecipeUrl() { return recipeUrl; }
    public RecipeNutritionDto getRecipeNutrition() { return recipeNutrition; }

    public static class RecipeNutritionDto implements Serializable {
        @SerializedName("calories")
        private String calories;
        @SerializedName("carbohydrate")
        private String carbohydrate;
        @SerializedName("fat")
        private String fat;
        @SerializedName("protein")
        private String protein;

        public String getCalories() { return calories; }
        public String getCarbohydrate() { return carbohydrate; }
        public String getFat() { return fat; }
        public String getProtein() { return protein; }
    }
}
