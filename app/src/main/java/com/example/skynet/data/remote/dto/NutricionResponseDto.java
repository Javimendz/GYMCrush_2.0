package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class NutricionResponseDto implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("tipoDieta")
    private String tipoDieta;

    @SerializedName("fechaGeneracion")
    private String fechaGeneracion;

    @SerializedName("nombreDieta")
    private String nombreDieta;

    @SerializedName("caloriasObjetivo")
    private Integer caloriasObjetivo;

    @SerializedName("macronutrientes")
    private FatSecretResponseDto macronutrientes;

    @SerializedName("dietas")
    private List<ComidaDiariaResponseDto> dietas = new java.util.ArrayList<>();

    @SerializedName("comidas")
    private List<ComidaDiariaResponseDto> comidas = new java.util.ArrayList<>();

    // Getters
    public Long getId() { return id; }
    public String getTipoDieta() { return tipoDieta; }
    public String getFechaGeneracion() { return fechaGeneracion; }
    public String getNombreDieta() { return nombreDieta; }
    public Integer getCaloriasObjetivo() { return caloriasObjetivo; }
    public FatSecretResponseDto getMacronutrientes() { return macronutrientes; }
    public List<ComidaDiariaResponseDto> getDietas() { 
        if (dietas == null) dietas = new java.util.ArrayList<>();
        return dietas; 
    }
    public List<ComidaDiariaResponseDto> getComidas() { 
        if (comidas == null) comidas = new java.util.ArrayList<>();
        return comidas; 
    }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTipoDieta(String tipoDieta) { this.tipoDieta = tipoDieta; }
    public void setFechaGeneracion(String fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public void setNombreDieta(String nombreDieta) { this.nombreDieta = nombreDieta; }
    public void setCaloriasObjetivo(Integer caloriasObjetivo) { this.caloriasObjetivo = caloriasObjetivo; }
    public void setMacronutrientes(FatSecretResponseDto macronutrientes) { this.macronutrientes = macronutrientes; }
    public void setDietas(List<ComidaDiariaResponseDto> dietas) { this.dietas = dietas; }
    public void setComidas(List<ComidaDiariaResponseDto> comidas) { this.comidas = comidas; }
}
