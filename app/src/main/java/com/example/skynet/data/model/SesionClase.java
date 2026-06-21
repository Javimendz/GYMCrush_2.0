package com.example.skynet.data.model;

public class SesionClase {
    public String name;       // Ej: "Crossfit", "Yoga"
    public long startTime;    // Hora de inicio en milisegundos (timestamp UTC)
    public int durationMinutes; // Duración en minutos
    public String imageUrl;   // URL opcional para la imagen de la clase

    public SesionClase(String name, long startTime, int durationMinutes, String imageUrl) {
        this.name = name;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.imageUrl = imageUrl;
    }
}


