package com.backend.service;

import java.util.List;
import com.backend.dto.SaludRequestDto;
import com.backend.dto.SaludResponseDto;

/**
 * Interfaz del servicio para la gestión de datos de salud y progreso físico.
 * <p>
 * Define los contratos para el registro y seguimiento de métricas
 * de salud de los usuarios, incluyendo peso, estatura, IMC y objetivos.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public interface ISaludService {

    /**
     * Registra una nueva medición de salud para un usuario.
     * <p>
     * Crea un registro con las métricas físicas actuales del usuario,
     * calcula automáticamente el IMC y lo asocia al usuario.
     * </p>
     *
     * @param dto       DTO con los datos de la medición (peso, estatura, etc.)
     * @param usuarioId ID del usuario al que pertenece la medición
     * @return DTO con los datos de la medición guardada
     * @throws ResourceNotFoundException si el usuario no existe
     */
    SaludResponseDto registrarProgreso(SaludRequestDto dto, Long usuarioId);

    /**
     * Obtiene el historial completo de mediciones de salud de un usuario.
     * <p>
     * Retorna todas las mediciones registradas ordenadas por fecha
     * de más reciente a más antigua.
     * </p>
     *
     * @param usuarioId ID del usuario del cual se desea el historial
     * @return Lista de DTOs con todas las mediciones del usuario
     * @throws ResourceNotFoundException si el usuario no existe
     */
    List<SaludResponseDto> obtenerHistorialPorUsuario(Long usuarioId);

    /**
     * Obtiene la medición de salud más reciente de un usuario.
     * <p>
     * Retorna el último registro de métricas físicas del usuario,
     * útil para mostrar el progreso actual en el dashboard.
     * </p>
     *
     * @param usuarioId ID del usuario del cual se desea el progreso actual
     * @return DTO con la medición más reciente
     * @throws ResourceNotFoundException si el usuario no tiene mediciones
     */
    SaludResponseDto obtenerProgresoActual(Long usuarioId);

    /**
     * Obtiene todos los registros de salud de todos los usuarios.
     * <p>
     * Método administrativo para consultar todas las mediciones
     * registradas en el sistema.
     * </p>
     *
     * @return Lista de DTOs con todos los registros de salud
     */
    List<SaludResponseDto> obtenerTodosLosRegistros();

    /**
     * Elimina un registro de salud por su identificador.
     * <p>
     * Verifica que el registro exista antes de proceder a su eliminación.
     * </p>
     *
     * @param id Identificador único del registro a eliminar
     * @throws ResourceNotFoundException si el registro no existe
     */
    void eliminarRegistro(Long id);
}
