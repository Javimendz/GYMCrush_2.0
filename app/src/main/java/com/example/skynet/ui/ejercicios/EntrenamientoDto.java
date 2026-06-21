package com.example.skynet.ui.ejercicios;

public class EntrenamientoDto {
    private final Long id;
    private final String nombre;
    private final String descripcion;
    private final String imagenUrl;
    private final String musculo;

    public EntrenamientoDto(Long id, String nombre, String descripcion, String imagenUrl, String musculo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.musculo = musculo != null ? musculo : "General";
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getImagenUrl() { return imagenUrl; }
    public String getMusculo() { return musculo; }
}
