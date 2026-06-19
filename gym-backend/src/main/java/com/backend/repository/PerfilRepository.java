package com.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.backend.domain.Perfil;
import com.backend.domain.Usuario;
import com.backend.domain.enums.EnumGenero;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    // Buscar por usuario
    Optional<Perfil> findByUsuarioId(Long usuarioId);

    // Buscar por dni
    Optional<Perfil> findByDni(String dni);

    // Buscar por telefono
    Optional<Perfil> findByTelefono(String telefono);

    // Buscar por nombre o apellido
    List<Perfil> findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombre, String apellidos);

    // FIltrar por genero, util para estadisticas
    List<Perfil> findByGenero(EnumGenero genero);

    // Contar perfiles por genero
    long countByGenero(EnumGenero genero);

    Perfil findByUsuario(Usuario usuario);

    @Query("SELECT p FROM Perfil p WHERE MONTH(p.fechaNacimiento) = :mes")
    List<Perfil> findByMesNacimiento(@Param("mes") Integer mes);

    // Verificar si existe un perfil con el dni o telefono
    boolean existsByDni(String dni);

    boolean existsByTelefono(String telefono);

    @Modifying
    @Query("DELETE FROM Perfil p WHERE p.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}
