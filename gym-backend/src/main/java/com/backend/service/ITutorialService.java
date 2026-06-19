package com.backend.service;

import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.dto.TutorialRequestDto;
import com.backend.dto.TutorialResponseDto;
import java.util.List;

/**
 * Interfaz del servicio para la gestión de tutoriales educativos.
 * <p>
 * Define los contratos para las operaciones CRUD de tutoriales,
 * incluyendo consulta por categoría y gestión de contenido.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public interface ITutorialService {
    /**
     * Obtiene todos los tutoriales disponibles en el sistema.
     * <p>
     * Retorna una lista completa de todos los tutoriales educativos
     * disponibles en la plataforma del gimnasio.
     * </p>
     *
     * @return Lista de DTOs con todos los tutoriales
     */
    List<TutorialResponseDto> obtenerTodos();

    /**
     * Obtiene los tutoriales filtrados por categoría específica.
     * <p>
     * Retorna todos los tutoriales que pertenecen a una categoría
     * determinada, útil para navegación y organización.
     * </p>
     *
     * @param categoriaId ID de la categoría a filtrar
     * @return Lista de DTOs con los tutoriales de la categoría
     */
    List<TutorialResponseDto> obtenerPorCategoria(Long categoriaId);

    /**
     * Crea un nuevo tutorial en el sistema.
     * <p>
     * Guarda un nuevo tutorial educativo con los datos
     * proporcionados en el DTO de solicitud.
     * </p>
     *
     * @param dto DTO con los datos del tutorial a crear
     * @return DTO con los datos del tutorial creado incluyendo su ID
     */
    TutorialResponseDto crear(TutorialRequestDto dto, String username);
    /**
     * Elimina un tutorial del sistema por su identificador.
     * <p>
     * Busca el tutorial por ID y lo elimina permanentemente
     * de la base de datos.
     * </p>
     *
     * @param id Identificador único del tutorial a eliminar
     */
    void eliminar(Long id);

    TutorialResponseDto actualizar(Long id, TutorialRequestDto dto);

    /**
     * Obtiene todas las categorías de tutoriales disponibles.
     * <p>
     * Retorna una lista completa de todas las categorías
     * que organizan los tutoriales educativos.
     * </p>
     *
     * @return Lista de DTOs con todas las categorías
     */
    List<CategoriaTutorialResponseDto> listarCategorias();

    List<TutorialResponseDto> buscarPorTitulo(String titulo);
    List<TutorialResponseDto> listarGlobales();
    List<TutorialResponseDto> listarParaUsuario(Long usuarioId);
    List<TutorialResponseDto> listarParaUsuarioYGlobales(String username);

}