package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para recibir la información del perfil desde el backend,
 * incluyendo los datos de salud enriquecidos.
 */
public class PerfilResponseDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("usuarioId")
    private Long usuarioId;

    @SerializedName("username")
    private String username;

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

    @SerializedName("bio")
    private String bio;

    @SerializedName("foto")
    private String foto;

    @SerializedName("correo")
    private String correo;

    @SerializedName("rol")
    private java.util.List<String> roles;

    // Campos enriquecidos de Salud
    @SerializedName("peso")
    private Double peso;

    @SerializedName("estatura")
    private Double estatura;

    @SerializedName("imc")
    private Double imc;

    @SerializedName("nivelActividad")
    private String nivelActividad;

    public PerfilResponseDto() {
    }

    // Getters
    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getUsername() { return username; }
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
    public String getBio() { return bio; }
    public String getFoto() { return foto; }
    public String getCorreo() { return correo; }

    public java.util.List<String> getRoles() {
        return roles != null ? roles : new java.util.ArrayList<>();
    }
    public Double getPeso() { return peso; }
    public Double getEstatura() { return estatura; }
    public Double getImc() { return imc; }
    public String getNivelActividad() { return nivelActividad; }

    // Setters (opcionales para Retrofit, pero útiles para pruebas)
    public void setId(Long id) { this.id = id; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public void setUsername(String username) { this.username = username; }
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
    public void setBio(String bio) { this.bio = bio; }
    public void setFoto(String foto) { this.foto = foto; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setPeso(Double peso) { this.peso = peso; }
    public void setEstatura(Double estatura) { this.estatura = estatura; }
    public void setImc(Double imc) { this.imc = imc; }
    public void setNivelActividad(String nivelActividad) { this.nivelActividad = nivelActividad; }

    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "");
    }
}
