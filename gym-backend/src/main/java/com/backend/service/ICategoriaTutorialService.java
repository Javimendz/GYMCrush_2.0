package com.backend.service;

import com.backend.dto.CategoriaTutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.exceptions.ResourceNotFoundException;

import java.util.List;

/**
 * Interfaz del servicio para la gestión de categorías de tutoriales.
 * <p>
 * Define los contratos para las operaciones CRUD de categorías
 * que organizan los tutoriales educativos del gimnasio.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public interface ICategoriaTutorialService {
    /**
     * Obtiene todas las categorías de tutoriales disponibles.
     * <p>
     * Retorna una lista completa de todas las categorías que organizan
     * los tutoriales educativos del sistema.
     * </p>
     *
     * @return Lista de DTOs con todas las categorías
     */
    List<CategoriaTutorialResponseDto> listarTodas();
    /**
     * Crea una nueva categoría de tutoriales en el sistema.
     * <p>
     * Guarda una nueva categoría con los datos proporcionados
     * en el DTO de solicitud.
     * </p>
     *
     * @param dto DTO con los datos de la nueva categoría
     * @return DTO con los datos de la categoría creada incluyendo su ID
     */
    CategoriaTutorialResponseDto crear(CategoriaTutorialRequestDto dto);
    /**
     * Busca una categoría específica por su identificador único.
     * <p>
     * Retorna la categoría con el ID especificado o lanza
     * una excepción si no existe en la base de datos.
     * </p>
     *
     * @param id Identificador único de la categoría a buscar
     * @return DTO con los datos de la categoría encontrada
     * @throws ResourceNotFoundException si la categoría no existe
     */
    CategoriaTutorialResponseDto buscarPorId(Long id);
    /**
     * Elimina una categoría de tutoriales del sistema.
     * <p>
     * Verifica que la categoría exista antes de proceder a su eliminación.
     * </p>
     *
     * @param id Identificador único de la categoría a eliminar
     * @throws ResourceNotFoundException si la categoría no existe
     */
    void eliminar(Long id);

CategoriaTutorialResponseDto actualizar(Long id, CategoriaTutorialRequestDto dto);

}