package com.backend.repository;

import com.backend.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad Categoria.
 * <p>
 * Proporciona el acceso a datos para las categorías de actividades.
 * Incluye métodos para validación de nombres únicos.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Busca una categoría por su nombre exacto, ignorando mayúsculas y minúsculas.
     * <p>
     * Muy útil en el Service para evitar que se creen categorías duplicadas
     * (ej: evitar crear "CARDIO" si ya existe "Cardio").
     * </p>
     *
     * @param nombre Nombre de la categoría a buscar.
     * @return Un Optional que contiene la categoría si se encuentra.
     */
    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    /**
     * Verifica si existe una categoría con un nombre específico.
     *
     * @param nombre Nombre de la categoría.
     * @return true si ya existe, false en caso contrario.
     */
    boolean existsByNombreIgnoreCase(String nombre);
}