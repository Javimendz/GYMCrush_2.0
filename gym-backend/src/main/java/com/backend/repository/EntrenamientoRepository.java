
//Paquete
package com.backend.repository;

//Imports
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.Entrenamiento;

import org.springframework.stereotype.Repository;

@Repository
public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Long> {


    
   @Query("SELECT e FROM Entrenamiento e LEFT JOIN FETCH e.tutoriales WHERE e.esGlobal = true OR e.usuario.id = :usuarioId")
List<Entrenamiento> findGlobalesYDelUsuario(@Param("usuarioId") Long usuarioId);

// Y para listarTodos (vista admin):
@Query("SELECT e FROM Entrenamiento e LEFT JOIN FETCH e.tutoriales")
List<Entrenamiento> findAllWithTutoriales();
Optional<Entrenamiento> findByNombre(String nombre);
    List<Entrenamiento> findByEsGlobalTrue();
    
}
