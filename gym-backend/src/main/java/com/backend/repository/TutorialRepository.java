package com.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.domain.Tutorial;
/**
 * Interfaz de repositorio para la entidad Tutorial
 * <p>
 * Proporciona métodos para acceder a la base de datos de tutoriales.
 * </p>
 */
@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Long> {    
/**
 * Busca todos los tutoriales de una categoría específica.
 * <p>
 * Retorna todos los tutoriales con la categoría especificada.
 * </p>
 *
 * @param categoriaId ID de la categoría a buscar
 * @return Lista de tutoriales encontrados
 */
    List<Tutorial> findByCategoriaId(Long categoriaId);
    
/**
 * Busca todos los tutoriales que contienen el título especificado.
 * <p>
 * Retorna todos los tutoriales que contienen el título especificado.
 * </p>
 *
 * @param titulo Título a buscar
 * @return Lista de tutoriales encontrados
 */
    List<Tutorial> findByTituloContainingIgnoreCase(String titulo);

    boolean existsByTituloIgnoreCase(String titulo);

    // Verifica si la URL del GIF ya está en uso
    boolean existsByUrlVideo(String urlVideo);
    List<Tutorial> findByEsGlobalTrue();
    @Query("SELECT t FROM Tutorial t WHERE t.usuario.id = :usuarioId OR t.usuario IS NULL")
    List<Tutorial> findGlobalesYDelUsuario(@Param("usuarioId") Long usuarioId);


}
