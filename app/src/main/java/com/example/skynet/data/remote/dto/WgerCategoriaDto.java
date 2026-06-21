package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class WgerCategoriaDto {

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    public Integer getId() { return id; }
    public String getName() { return name; }

    // Para mostrar en el dropdown igual que hicimos con músculos
    public String getDisplayName() {
        return name != null ? name : "Sin nombre";
    }
}
