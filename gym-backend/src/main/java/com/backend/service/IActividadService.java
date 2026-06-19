package com.backend.service;

import java.util.List;
import com.backend.dto.ActividadRequestDto;
import com.backend.dto.ActividadResponseDto;

/**
 * Interfaz del servicio para la gestión de actividades del gimnasio.
 * <p>
 * Define los contratos para las operaciones CRUD de actividades,
 * incluyendo búsqueda, filtrado y gestión de disciplinas deportivas.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public interface IActividadService {

    // Obtener todas las actividades disponibles
    List<ActividadResponseDto> listarTodos();

    /**
     * Busca una actividad específica por su identificador único.
     * <p>
     * Retorna la actividad con el ID especificado o lanza
     * una excepción si no existe en la base de datos.
     * </p>
     *
     * @param id Identificador único de la actividad a buscar
     * @return DTO con los datos de la actividad encontrada
     * @throws ResourceNotFoundException si la actividad no existe
     */
    ActividadResponseDto buscarPorId(Long id);

    /**
     * Crea una nueva actividad en el sistema.
     * <p>
     * Registra una nueva disciplina deportiva con los datos
     * proporcionados en el DTO de solicitud.
     * </p>
     *
     * @param dto DTO con los datos de la nueva actividad
     * @return DTO con los datos de la actividad creada incluyendo su ID
     */
    ActividadResponseDto crear(ActividadRequestDto dto);

    /**
     * Actualiza una actividad existente en el sistema.
     * <p>
     * Permite modificar los datos de una disciplina deportiva
     * existente con la información proporcionada.
     * </p>
     *
     * @param id  Identificador único de la actividad a actualizar
     * @param dto DTO con los datos actualizados de la actividad
     * @return DTO con los datos de la actividad actualizada
     * @throws ResourceNotFoundException si la actividad no existe
     */
    ActividadResponseDto actualizar(Long id, ActividadRequestDto dto);

    /**
     * Elimina una actividad del sistema por su identificador.
     * <p>
     * Verifica que la actividad exista antes de proceder a su eliminación.
     * </p>
     *
     * @param id Identificador único de la actividad a eliminar
     * @throws ResourceNotFoundException si la actividad no existe
     */
    void eliminar(Long id);

    /**
     * Busca actividades que contienen un nombre específico.
     * <p>
     * Realiza una búsqueda parcial por nombre de actividad,
     * útil para funcionalidad de búsqueda en la interfaz.
     * </p>
     *
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de DTOs con las actividades que coinciden
     */
    List<ActividadResponseDto> buscarPorNombre(String nombre);

    /**
     * Filtra las actividades por un día específico de la semana.
     * <p>
     * Retorna todas las actividades que tienen horarios
     * programados para un día determinado.
     * </p>
     *
     * @param dia Nombre del día de la semana a filtrar
     * @return Lista de DTOs con las actividades de ese día
     */
    List<ActividadResponseDto> listarPorDia(String dia);

    /**
     * Filtra actividades por precio máximo permitido.
     * <p>
     * Retorna todas las actividades cuyo precio no excede
     * el límite especificado, útil para filtros de presupuesto.
     * </p>
     *
     * @param precio Precio máximo a considerar
     * @return Lista de DTOs con las actividades dentro del presupuesto
     */
    List<ActividadResponseDto> filtrarPorPrecioMaximo(Integer precio);
}
