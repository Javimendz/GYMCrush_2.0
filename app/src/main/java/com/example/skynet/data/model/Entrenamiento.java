package com.example.skynet.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modelo de dominio para un Entrenamiento/Ejercicio.
 */
public class Entrenamiento implements Parcelable {
    private Long id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;

    public Entrenamiento() {}

    public Entrenamiento(Long id, String nombre, String descripcion, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
    }

    protected Entrenamiento(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        nombre = in.readString();
        descripcion = in.readString();
        imagenUrl = in.readString();
    }

    public static final Creator<Entrenamiento> CREATOR = new Creator<Entrenamiento>() {
        @Override
        public Entrenamiento createFromParcel(Parcel in) {
            return new Entrenamiento(in);
        }

        @Override
        public Entrenamiento[] newArray(int size) {
            return new Entrenamiento[size];
        }
    };

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(nombre);
        dest.writeString(descripcion);
        dest.writeString(imagenUrl);
    }
}
