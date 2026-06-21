package com.example.skynet.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para representar los roles del usuario.
 * Ayuda a la App a decidir qué funcionalidades mostrar según el permiso.
 */
public class RoleDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name; // Ejemplo: "ROLE_USER", "ROLE_ADMIN"

    public RoleDto() {
    }

    // --- GETTERS ---
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Método de conveniencia para comprobar roles sin lidiar con el prefijo "ROLE_"
     */
    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(name) || "ROLE_SUPER_ADMIN".equals(name);
    }

    public boolean isTrainer() {
        return "ROLE_TRAINER".equals(name) || "ROLE_ENTRENADOR".equals(name);
    }
}