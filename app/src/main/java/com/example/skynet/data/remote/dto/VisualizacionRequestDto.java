package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class VisualizacionRequestDto {
    @SerializedName("usuarioId")
    private Long usuarioId;
    @SerializedName("tutorialId")
    private Long tutorialId;
    @SerializedName("progresoSegundos")
    private Integer progresoSegundos;
    @SerializedName("completado")
    private Boolean completado;

    public VisualizacionRequestDto() {}

    public VisualizacionRequestDto(Long usuarioId, Long tutorialId, Integer progresoSegundos, Boolean completado) {
        this.usuarioId = usuarioId;
        this.tutorialId = tutorialId;
        this.progresoSegundos = progresoSegundos;
        this.completado = completado;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getTutorialId() { return tutorialId; }
    public void setTutorialId(Long tutorialId) { this.tutorialId = tutorialId; }
    public Integer getProgresoSegundos() { return progresoSegundos; }
    public void setProgresoSegundos(Integer progresoSegundos) { this.progresoSegundos = progresoSegundos; }
    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
}
