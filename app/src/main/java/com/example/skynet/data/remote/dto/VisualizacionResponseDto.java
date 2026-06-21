package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class VisualizacionResponseDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("tutorialId")
    private Long tutorialId;
    @SerializedName("progresoSegundos")
    private Integer progresoSegundos;
    @SerializedName("completado")
    private Boolean completado;
    @SerializedName("contadorReproducciones")
    private Integer contadorReproducciones;
    @SerializedName("tituloTutorial")
    private String tituloTutorial;
    @SerializedName("descripcionTutorial")
    private String descripcionTutorial;
    @SerializedName("urlVideo")
    private String urlVideo;

    public VisualizacionResponseDto() {}

    public Long getId() { return id; }
    public Long getTutorialId() { return tutorialId; }
    public Integer getProgresoSegundos() { return progresoSegundos; }
    public Boolean getCompletado() { return completado; }
    public Integer getContadorReproducciones() { return contadorReproducciones; }
    public String getTituloTutorial() { return tituloTutorial; }
    public String getDescripcionTutorial() { return descripcionTutorial; }
    public String getUrlVideo() { return urlVideo; }
}
