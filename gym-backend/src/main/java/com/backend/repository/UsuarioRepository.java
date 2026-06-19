//Paquete de repositorio
package com.backend.repository;

import java.util.Optional;

//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Usuario;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 * Interfaz de repositorio para la entidad Usuario
 * <p>
 * Proporciona métodos para acceder a la base de datos de usuarios.
 * </p>
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /**
     * Busca un usuario por su nombre de usuario.
     * <p>
     * Retorna el usuario con el nombre de usuario especificado o null si no existe.
     * </p>
     *
     * @param username Nombre de usuario a buscar
     * @return Usuario encontrado o null
     */
    Optional<Usuario> findByUsername(String username);


    @Modifying
    @Query("UPDATE Usuario u SET u.planActivo = null WHERE u.planActivo.id = :planId")
    void desvincularPlanDeUsuarios(@Param("planId") Long planId);
    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     * <p>
     * Retorna true si existe un usuario con el nombre de usuario especificado,
     * false en caso contrario.
     * </p>
     *
     * @param username Nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    Boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el correo especificado.
     * <p>
     * Retorna true si existe un usuario con el correo especificado, false en caso
     * contrario.
     * </p>
     *
     * @param correo Correo a verificar
     * @return true si existe, false en caso contrario
     */
    Boolean existsByEmail(String email);
Optional<Usuario> findByEmail(String email);
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r.name = 'ENTRENADOR'")
    List<Usuario> findEntrenadores();

}