package com.backend.domain;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un rol de usuario en el sistema.
 * <p>
 * Define los permisos y niveles de acceso de los usuarios,
 * incluyendo relación muchos-a-muchos con usuarios.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    /**
     * Identificador único del rol.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nombre del rol.
     * <p>
     * Obligatorio, identifica el rol (ej: ROLE_ADMIN, ROLE_USUARIO).
     * </p>
     */
    private String name;

    /**
     * Conjunto de usuarios asignados a este rol.
     * <p>
     * Relación muchos-a-muchos, excluido de JSON para evitar
     * ciclos infinitos durante la serialización.
     * </p>
     */
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<Usuario> usuarios;

    public Set<Usuario> getUsuarios() {
    return usuarios;
}

}
