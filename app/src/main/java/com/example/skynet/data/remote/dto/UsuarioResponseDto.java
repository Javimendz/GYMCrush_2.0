package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para representar la información completa de un usuario.
 * Se utiliza tanto en el registro como en la consulta de perfil.
 */
public class UsuarioResponseDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("dni")
    private String dni;

    @SerializedName("correo")
    private String correo;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("ciudad")
    private String ciudad;

    @SerializedName("codigoPostal")
    private String codigoPostal;

    @SerializedName("pais")
    private String pais;

    @SerializedName("foto")
    private String foto;

    @SerializedName("bio")
    private String bio;

    // Aquí usamos una lista de strings o un RoleDto simple si solo necesitas el nombre del rol
    @SerializedName("roles")
    private List<String> roles;

    public UsuarioResponseDto() {
    }

    // --- GETTERS ---
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getNombre() { return nombre != null ? nombre : ""; }
    public String getApellidos() { return apellidos != null ? apellidos : ""; }
    public String getDni() { return dni; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getCiudad() { return ciudad; }
    public String getCodigoPostal() { return codigoPostal; }
    public String getPais() { return pais; }
    public String getFoto() { return foto; }
    public String getBio() { return bio; }
    public List<String> getRoles() {
        return roles != null ? roles : new ArrayList<>();
    }

    // Método de utilidad para la UI
    public String getNombreCompleto() {
        return getNombre() + " " + getApellidos();
    }
}