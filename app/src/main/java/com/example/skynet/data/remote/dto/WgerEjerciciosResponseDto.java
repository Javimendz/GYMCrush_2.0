package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WgerEjerciciosResponseDto {
    @SerializedName("ejercicios")
    private List<WgerEjercicioItemDto> ejercicios;

    @SerializedName("totalElementos")
    private int totalElementos;

    @SerializedName("totalPaginas")
    private int totalPaginas;

    @SerializedName("paginaActual")
    private int paginaActual;

    public List<WgerEjercicioItemDto> getEjercicios() { return ejercicios; }
    public int getTotalElementos() { return totalElementos; }
    public int getTotalPaginas() { return totalPaginas; }
    public int getPaginaActual() { return paginaActual; }

    public static class WgerEjercicioItemDto {

        @SerializedName("id")
        private Long id;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("images")
        private List<WgerImageDto> images;

        @SerializedName("translations")
        private List<WgerTranslationDto> translations;

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public List<WgerImageDto> getImages() { return images; }
        public List<WgerTranslationDto> getTranslations() { return translations; }

        public String getImageUrl() {
            return (images != null && !images.isEmpty())
                    ? images.get(0).getImage()
                    : null;
        }
    }

    public static class WgerImageDto {
        @SerializedName("image")
        private String image;
        public String getImage() { return image; }
    }

    public static class WgerTranslationDto {
        @SerializedName("language")
        private int language;
        @SerializedName("name")
        private String name;
        public int getLanguage() { return language; }
        public String getName() { return name; }
    }
}
