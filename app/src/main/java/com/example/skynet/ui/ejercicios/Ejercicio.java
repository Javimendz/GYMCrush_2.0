package com.example.skynet.ui.ejercicios;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Ejercicio implements Parcelable {
    private Long id;
    private String nombre;
    private String categoria;
    private String descripcion;
    private String duracion;
    private String urlVideo;
    private String urlImagen;
    private int imagenResId;
    private String dificultad;
    private boolean seleccionado;
    private boolean esGlobal;
    
    // Nuevos campos para rutinas personalizadas
    private String series = "0";
    private String repeticiones = "0";

    // Manejo de series para ejecución
    private List<Serie> seriesList = new ArrayList<>();

    // CONSTRUCTOR 1
    public Ejercicio(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.seleccionado = false;
    }

    public Ejercicio(Long id, String nombre, String categoria, String descripcion, String duracion, String urlVideo, int imagenResId, String dificultad) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.urlVideo = urlVideo;
        this.imagenResId = imagenResId;
        this.dificultad = dificultad;
        this.seleccionado = false;
    }

    public Ejercicio(String nombre, String categoria, String descripcion, String duracion, String urlVideo, int imagenResId, String dificultad) {
        this(null, nombre, categoria, descripcion, duracion, urlVideo, imagenResId, dificultad);
    }

    protected Ejercicio(Parcel in) {
        if (in.readByte() == 0) { id = null; } else { id = in.readLong(); }
        nombre = in.readString();
        categoria = in.readString();
        descripcion = in.readString();
        duracion = in.readString();
        urlVideo = in.readString();
        urlImagen = in.readString();
        imagenResId = in.readInt();
        dificultad = in.readString();
        seleccionado = in.readByte() != 0;
        esGlobal = in.readByte() != 0;
        series = in.readString();
        repeticiones = in.readString();
        seriesList = in.createTypedArrayList(Serie.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) { dest.writeByte((byte) 0); } else { dest.writeByte((byte) 1); dest.writeLong(id); }
        dest.writeString(nombre);
        dest.writeString(categoria);
        dest.writeString(descripcion);
        dest.writeString(duracion);
        dest.writeString(urlVideo);
        dest.writeString(urlImagen);
        dest.writeInt(imagenResId);
        dest.writeString(dificultad);
        dest.writeByte((byte) (seleccionado ? 1 : 0));
        dest.writeByte((byte) (esGlobal ? 1 : 0));
        dest.writeString(series);
        dest.writeString(repeticiones);
        dest.writeTypedList(seriesList);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Ejercicio> CREATOR = new Creator<Ejercicio>() {
        @Override
        public Ejercicio createFromParcel(Parcel in) { return new Ejercicio(in); }
        @Override
        public Ejercicio[] newArray(int size) { return new Ejercicio[size]; }
    };

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public String getDuracion() { return duracion; }
    public String getUrlVideo() { return urlVideo; }
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
    public int getImagenResId() { return imagenResId; }
    public String getDificultad() { return dificultad; }
    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }
    public boolean isEsGlobal() { return esGlobal; }
    public void setEsGlobal(boolean esGlobal) { this.esGlobal = esGlobal; }
    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }
    public String getRepeticiones() { return repeticiones; }
    public void setRepeticiones(String repeticiones) { this.repeticiones = repeticiones; }
    public List<Serie> getSeriesList() { return seriesList; }
    public void setSeriesList(List<Serie> seriesList) { this.seriesList = seriesList; }

    // Clase anidada para las series
    public static class Serie implements Parcelable {
        private int numero;
        private double kg;
        private int reps;

        public Serie(int numero, double kg, int reps) {
            this.numero = numero;
            this.kg = kg;
            this.reps = reps;
        }

        protected Serie(Parcel in) {
            numero = in.readInt();
            kg = in.readDouble();
            reps = in.readInt();
        }

        public static final Creator<Serie> CREATOR = new Creator<Serie>() {
            @Override
            public Serie createFromParcel(Parcel in) { return new Serie(in); }
            @Override
            public Serie[] newArray(int size) { return new Serie[size]; }
        };

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(numero);
            dest.writeDouble(kg);
            dest.writeInt(reps);
        }

        public int getNumero() { return numero; }
        public double getKg() { return kg; }
        public void setKg(double kg) { this.kg = kg; }
        public int getReps() { return reps; }
        public void setReps(int reps) { this.reps = reps; }
    }
}