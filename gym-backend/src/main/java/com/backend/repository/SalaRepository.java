package com.backend.repository;

import com.backend.domain.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad Sala.
 * <p>
 * Gestiona las operaciones de acceso a la base de datos para los espacios
 * físicos del gimnasio. Incluye consultas personalizadas para filtrado
 * por estado y capacidad.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {

    /**
     * Busca una sala por su nombre exacto (ignorando mayúsculas/minúsculas).
     * Útil para validar que no se creen dos salas con el mismo nombre.
     *
     * @param nombre Nombre de la sala (Ej: "Sala Zen")
     * @return Optional con la sala si existe
     */
    Optional<Sala> findByNombreIgnoreCase(String nombre);

    /**
     * Obtiene una lista de todas las salas que están operativas actualmente.
     * Útil para los menús desplegables del frontend al crear un Horario
     * (no queremos mostrar salas que estén en obras o inactivas).
     *
     * @return Lista de salas activas
     */
    List<Sala> findByActivaTrue();

    /**
     * Busca salas que tengan al menos una capacidad específica.
     * Útil si un entrenador necesita buscar "una sala donde quepan 30 personas".
     *
     * @param capacidad Aforo mínimo requerido
     * @return Lista de salas que cumplen el requisito
     */
    List<Sala> findByCapacidadMaxGreaterThanEqual(Integer capacidad);

    boolean existsByNombreIgnoreCase(String nombre);

}