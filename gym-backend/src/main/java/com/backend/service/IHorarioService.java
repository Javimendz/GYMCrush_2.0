package com.backend.service;

import java.util.List;

import com.backend.dto.HorarioRequestDto;
import com.backend.dto.HorarioResponseDto;

/**
 * Interfaz del servicio para la gestión de horarios del gimnasio.
 * <p>
 * Define los contratos para las operaciones CRUD de horarios,
 * incluyendo asignación de actividades y control de aforo.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public interface IHorarioService {
    /**
     * Obtiene todos los horarios configurados en el sistema.
     * <p>
     * Retorna una lista completa de todos los horarios con sus
     * actividades asociadas, ordenados por defecto.
     * </p>
     *
     * @return Lista de DTOs con todos los horarios configurados
     */
    List<HorarioResponseDto> listarTodos();

    /**
     * Busca un horario específico por su identificador único.
     * <p>
     * Incluye la información de la actividad asociada
     * para facilitar la visualización en la interfaz de usuario.
     * </p>
     *
     * @param id Identificador único del horario a buscar
     * @return DTO con la información detallada del horario
     * @throws ResourceNotFoundException si el horario no existe
     */
    HorarioResponseDto obtenerPorId(Long id);

    /**
     * Crea un nuevo horario en el sistema.
     * <p>
     * Asigna el horario a una actividad existente y establece
     * el aforo máximo permitido.
     * </p>
     *
     * @param dto DTO con los datos del horario (día, horas, aforo, actividad)
     * @return DTO con los datos del horario creado incluyendo su ID
     * @throws ResourceNotFoundException si la actividad referenciada no existe
     */
    HorarioResponseDto crear(HorarioRequestDto dto);

    /**
     * Actualiza un horario existente en el sistema.
     * <p>
     * Permite modificar día, horas, aforo o actividad asociada
     * al horario especificado.
     * </p>
     *
     * @param id Identificador único del horario a actualizar
     * @param dto DTO con los datos actualizados del horario
     * @return DTO con los datos del horario actualizado
     * @throws ResourceNotFoundException si el horario no existe
     */
    HorarioResponseDto actualizar(Long id, HorarioRequestDto dto);

    /**
     * Elimina un horario del sistema por su identificador.
     * <p>
     * Verifica que el horario exista antes de proceder a su eliminación.
     * </p>
     *
     * @param id Identificador único del horario a eliminar
     * @throws ResourceNotFoundException si el horario no existe
     */
    void eliminar(Long id);

    /**
     * Filtra los horarios por un día específico de la semana.
     * <p>
     * Retorna todos los horarios programados para un día
     * determinado (LUNES, MARTES, etc.).
     * </p>
     *
     * @param dia Nombre del día de la semana a filtrar
     * @return Lista de DTOs con los horarios de ese día
     */
    List<HorarioResponseDto> listarPorDia(String dia);

    /**
     * Obtiene el cronograma semanal de una actividad específica.
     * <p>
     * Retorna todos los horarios donde se imparte una actividad
     * determinada, útil para mostrar el calendario de clases.
     * </p>
     *
     * @param actividadId ID de la actividad a consultar
     * @return Lista de DTOs con los horarios de la actividad
     */
    List<HorarioResponseDto> buscarPorActividad(Long actividadId);
}
