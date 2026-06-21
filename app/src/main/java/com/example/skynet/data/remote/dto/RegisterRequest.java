package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Set;

public class RegisterRequest {

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("email")
    private String email;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;

    @SerializedName("genero")
    private String genero;

    @SerializedName("roles")
    private Set<String> roles;

    @SerializedName("ciudad")
    private String ciudad;

    @SerializedName("codigoPostal")
    private String codigoPostal;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("pais")
    private String pais;

    @SerializedName("dni")
    private String dni;

    @SerializedName("faceVector")
    private List<Float> faceVector;

    // Getters and Setters
    public List<Float> getFaceVector() { return faceVector; }
    public void setFaceVector(List<Float> faceVector) { this.faceVector = faceVector; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
}
