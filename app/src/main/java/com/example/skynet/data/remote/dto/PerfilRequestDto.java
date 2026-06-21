package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para enviar actualizaciones de perfil al backend.
 */
public class PerfilRequestDto {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("dni")
    private String dni;

    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("ciudad")
    private String ciudad;

    @SerializedName("pais")
    private String pais;

    @SerializedName("codigoPostal")
    private String codigoPostal;

    @SerializedName("genero")
    private String genero;

    @SerializedName("correo")
    private String correo;

    @SerializedName("bio")
    private String bio;

    @SerializedName("foto")
    private String foto;

    public PerfilRequestDto() {
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getTelefono() { return telefono; }
    public String getDni() { return dni; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getDireccion() { return direccion; }
    public String getCiudad() { return ciudad; }
    public String getPais() { return pais; }
    public String getCodigoPostal() { return codigoPostal; }
    public String getGenero() { return genero; }
    public String getCorreo() { return correo; }
    public String getBio() { return bio; }
    public String getFoto() { return foto; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDni(String dni) { this.dni = dni; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setPais(String pais) { this.pais = pais; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setBio(String bio) { this.bio = bio; }
    public void setFoto(String foto) { this.foto = foto; }
}
