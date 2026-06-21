package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class DietaResponseDto implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("objetivoCalorico")
    private Double objetivoCalorico;

    @SerializedName("cantidadProteinas")
    private Double cantidadProteinas;

    @SerializedName("cantidadCarbohidratos")
    private Double cantidadCarbohidratos;

    @SerializedName("cantidadGrasas")
    private Integer cantidadGrasas;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("imagenUrl")
    private String imagenUrl;

    // Getters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public Double getObjetivoCalorico() { return objetivoCalorico; }
    public Double getCantidadProteinas() { return cantidadProteinas; }
    public Double getCantidadCarbohidratos() { return cantidadCarbohidratos; }
    public Integer getCantidadGrasas() { return cantidadGrasas; }
    public String getDescripcion() { return descripcion; }
    public String getImagenUrl() { return imagenUrl; }
}
