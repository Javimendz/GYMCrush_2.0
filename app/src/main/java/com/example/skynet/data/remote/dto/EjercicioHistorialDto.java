package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EjercicioHistorialDto {
    @SerializedName("fecha")
    private String fecha;
    
    @SerializedName("mejorPeso")
    private double mejorPeso;
    
    @SerializedName("mejor1RM")
    private double mejor1RM;
    
    @SerializedName("volumenTotal")
    private double volumenTotal;

    @SerializedName("duracion")
    private String duracion;

    @SerializedName("series")
    private List<SerieDto> series;

    public static class SerieDto {
        private double kg;
        private int reps;
        private String fecha;

        public SerieDto(double kg, int reps, String fecha) {
            this.kg = kg;
            this.reps = reps;
            this.fecha = fecha;
        }

        public double getKg() { return kg; }
        public int getReps() { return reps; }
        public String getFecha() { return fecha; }
    }

    public EjercicioHistorialDto(String fecha, double mejorPeso, double mejor1RM, double volumenTotal, String duracion) {
        this.fecha = fecha;
        this.mejorPeso = mejorPeso;
        this.mejor1RM = mejor1RM;
        this.volumenTotal = volumenTotal;
        this.duracion = duracion;
    }

    public String getFecha() { return fecha; }
    public double getMejorPeso() { return mejorPeso; }
    public double getMejor1RM() { return mejor1RM; }
    public double getVolumenTotal() { return volumenTotal; }
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public List<SerieDto> getSeries() { return series; }
    public void setSeries(List<SerieDto> series) { this.series = series; }
}