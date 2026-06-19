package com.backend.repository;

import com.backend.domain.CategoriaDieta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad CategoriaDieta.
 * <p>
 * Proporciona el acceso a datos para clasificar los planes nutricionales.
 * Permite realizar búsquedas por nombre para validaciones de integridad.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 */
@Repository
public interface CategoriaDietaRepository extends JpaRepository<CategoriaDieta, Long> {

    /**
     * Busca una categoría de dieta por su nombre exacto (case-insensitive).
     * <p>
     * Útil para asegurar que no se creen categorías con nombres repetidos
     * antes de realizar un 'save'.
     * </p>
     *
     * @param nombre Nombre de la categoría (ej: "Vegana", "Definición").
     * @return Un Optional con la categoría si existe.
     */
    Optional<CategoriaDieta> findByNombreIgnoreCase(String nombre);

    /**
     * Comprueba si ya existe una categoría con ese nombre.
     *
     * @param nombre Nombre a verificar.
     * @return true si el nombre ya está registrado.
     */
    boolean existsByNombreIgnoreCase(String nombre);
}