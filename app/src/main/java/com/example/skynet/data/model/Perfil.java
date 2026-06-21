package com.example.skynet.data.model;

import com.google.gson.annotations.SerializedName;

public class Perfil {

    @SerializedName("id")
    private Long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("dni")
    private String dni;

    @SerializedName("fecha_nacimiento")
    private String fechaNacimiento; // Recibido como String "yyyy-MM-dd"

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("ciudad")
    private String ciudad;

    @SerializedName("pais")
    private String pais;

    @SerializedName("codigo_postal")
    private String codigoPostal;

    @SerializedName("bio")
    private String bio;

    @SerializedName("genero")
    private String genero; // Recibe el valor del Enum como String

    @SerializedName("foto")
    private String foto; // URL de la imagen

    // Constructor vacío
    public Perfil() {}

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    // Método extra para mostrar nombre y apellidos juntos
    public String getNombreCompleto() {
        return nombre + " " + (apellidos != null ? apellidos : "");
    }
}