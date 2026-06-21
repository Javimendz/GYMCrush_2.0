package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class WgerMusculoDto {

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;        // nombre científico: "Biceps brachii"

    @SerializedName("name_en")
    private String nameEn;      // nombre común: "Biceps"

    @SerializedName("is_front")
    private boolean isFront;

    @SerializedName("image_url_main")
    private String imageUrlMain;

    @SerializedName("image_url_secondary")
    private String imageUrlSecondary;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getNameEn() { return nameEn; }
    public boolean isFront() { return isFront; }
    public String getImageUrlMain() { return imageUrlMain; }
    public String getImageUrlSecondary() { return imageUrlSecondary; }

    // Para mostrar en el dropdown — usa nameEn si existe, si no el nombre científico
    public String getDisplayName() {
        return (nameEn != null && !nameEn.isEmpty()) ? nameEn : name;
    }
}
