package com.backend.repository;


import com.backend.domain.PlanEntrenamiento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanEntrenamientoRepository extends JpaRepository<PlanEntrenamiento, Long> {

    // Evita duplicados de nombres de planes (ej: no tener dos "Full Body")
    boolean existsByNombreIgnoreCase(String nombre);
@Query("SELECT p FROM PlanEntrenamiento p WHERE p.usuario.id = :usuarioId OR p.usuario IS NULL")
    List<PlanEntrenamiento> findGlobalesYDelUsuario(@Param("usuarioId") Long usuarioId);
    // Filtra el catálogo por objetivo (útil para el buscador de la App)
    List<PlanEntrenamiento> findByObjetivo(String objetivo);

    // Filtra por nivel (Principiante, etc.)
    List<PlanEntrenamiento> findByNivel(String nivel);
Optional<PlanEntrenamiento> findByNombre(String nombre);
    // Trae el plan con sus ejercicios cargados para evitar el error 'LazyInitializationException'
    @EntityGraph(attributePaths = {"ejercicios", "ejercicios.entrenamiento"})
    Optional<PlanEntrenamiento> findWithEjerciciosById(Long id);
}