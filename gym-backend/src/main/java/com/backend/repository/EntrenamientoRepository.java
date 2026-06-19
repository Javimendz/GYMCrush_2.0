
//Paquete
package com.backend.repository;

//Imports
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.Entrenamiento;

import org.springframework.stereotype.Repository;

@Repository
public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Long> {

    @Query("SELECT e FROM Entrenamiento e WHERE e.usuario.id = :usuarioId OR e.usuario IS NULL")
    List<Entrenamiento> findGlobalesYDelUsuario(@Param("usuarioId") Long usuarioId);
}
