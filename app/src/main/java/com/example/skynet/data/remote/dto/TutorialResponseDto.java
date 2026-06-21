package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class TutorialResponseDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("titulo")
    private String titulo;
    @SerializedName("descripcion")
    private String descripcion;
    @SerializedName("urlVideo")
    private String urlVideo;
    @SerializedName("duracionMin")
    private Integer duracionMin;
    @SerializedName("categoriaId")
    private Long categoriaId;
    @SerializedName("nombreCategoria")
    private String nombreCategoria;
    @SerializedName("musculoObjetivo")
    private String musculoObjetivo;
    @SerializedName("equipamiento")
    private String equipamiento;
    @SerializedName("esGlobal")
    private boolean esGlobal;
    @SerializedName("usuarioId")
    private Long usuarioId;
    @SerializedName("urlImagen")
    private String urlImagen;
    @SerializedName("contadorReproducciones")
    private Integer contadorReproducciones;

    public TutorialResponseDto() {}

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getUrlVideo() { return urlVideo; }
    public Integer getDuracionMin() { return duracionMin; }
    public Long getCategoriaId() { return categoriaId; }
    public String getNombreCategoria() { return nombreCategoria; }
    public String getMusculoObjetivo() { return musculoObjetivo; }
    public String getEquipamiento() { return equipamiento; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public boolean isEsGlobal() { return esGlobal; }
    public void setEsGlobal(boolean esGlobal) { this.esGlobal = esGlobal; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Integer getContadorReproducciones() { return contadorReproducciones; }
    public void setContadorReproducciones(Integer contadorReproducciones) { this.contadorReproducciones = contadorReproducciones; }
}
