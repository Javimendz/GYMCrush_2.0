package com.backend.repository;

import com.backend.domain.Tutorial;
import com.backend.domain.Visualizacion;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Visualizacion.
 * <p>
 * Gestiona el progreso de los usuarios en los videotutoriales.
 * Permite recuperar el estado de reproducción y filtrar por completados.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 */
@Repository
public interface VisualizacionRepository extends JpaRepository<Visualizacion, Long> {

   
    Optional<Visualizacion> findByUsuarioIdAndTutorialId(Long usuarioId, Long tutorialId);

    List<Visualizacion> findByUsuarioIdAndCompletadoTrue(Long usuarioId);

    List<Visualizacion> findByUsuarioIdAndCompletadoFalse(Long usuarioId);

    // Versiones con ordenamiento 
    List<Visualizacion> findByUsuarioIdAndCompletadoTrueOrderByUltimaActualizacionDesc(Long usuarioId);
    List<Visualizacion> findByUsuarioIdAndCompletadoFalseOrderByUltimaActualizacionDesc(Long usuarioId);

    @Query("SELECT v.tutorial FROM Visualizacion v GROUP BY v.tutorial ORDER BY COUNT(v) DESC")
    List<Tutorial> findPopularTutorials(Pageable pageable);
    long countByTutorialId(Long tutorialId);
}