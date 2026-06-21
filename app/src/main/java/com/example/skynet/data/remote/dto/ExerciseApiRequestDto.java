package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExerciseApiRequestDto {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("bodyPart")
    private String bodyPart;
    @SerializedName("equipment")
    private String equipment;
    @SerializedName("target")
    private String target;
    @SerializedName("gifUrl")
    private String gifUrl;
    @SerializedName("secondaryMuscles")
    private List<String> secondaryMuscles;
    @SerializedName("instructions")
    private List<String> instructions;
    @SerializedName("description")
    private String description;
    @SerializedName("difficulty")
    private String difficulty;
    @SerializedName("category")
    private String category;

    public ExerciseApiRequestDto() {}

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBodyPart() { return bodyPart; }
    public String getEquipment() { return equipment; }
    public String getTarget() { return target; }
    public String getGifUrl() { return gifUrl; }
    public List<String> getSecondaryMuscles() { return secondaryMuscles; }
    public List<String> getInstructions() { return instructions; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public String getCategory() { return category; }
}
