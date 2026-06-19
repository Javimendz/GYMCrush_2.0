package com.backend.service;

import java.util.List;
import com.backend.domain.Perfil;
import com.backend.dto.PerfilRequestDto;
import com.backend.dto.PerfilResponseDto;
import com.backend.exceptions.ResourceNotFoundException;

/**
 * Interfaz del servicio para la gestión de perfiles de usuario.
 * <p>
 * Define los contratos para las operaciones CRUD de perfiles,
 * incluyendo consulta completa con datos de salud más recientes.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public interface IPerfilService {

    /**
     * Obtiene todos los perfiles de usuarios del sistema.
     * <p>
     * Retorna una lista completa de todos los perfiles incluyendo
     * los datos de salud más recientes de cada usuario.
     * </p>
     *
     * @return Lista de DTOs con todos los perfiles enriquecidos
     */
    List<PerfilResponseDto> findAll();
    
List<PerfilResponseDto> listarEntrenadores(); //
    /**
     * Guarda un nuevo perfil de usuario en la base de datos.
     * <p>
     * Crea un nuevo perfil con los datos proporcionados
     * en la entidad Perfil.
     * </p>
     *
     * @param perfil Entidad Perfil con los datos a guardar
     * @return Entidad Perfil guardada con ID asignado
     */
    Perfil save(Perfil perfil);

    /**
     * Busca un perfil específico por su identificador único.
     * <p>
     * Retorna el perfil con el ID especificado incluyendo
     * los datos de salud más recientes del usuario asociado.
     * </p>
     *
     * @param id Identificador único del perfil a buscar
     * @return DTO con los datos del perfil encontrado
     * @throws ResourceNotFoundException si el perfil no existe
     */
    PerfilResponseDto findById(Long id);

    /**
     * Elimina un perfil del sistema por su identificador.
     * <p>
     * Busca el perfil por ID y lo elimina permanentemente
     * de la base de datos.
     * </p>
     *
     * @param id Identificador único del perfil a eliminar
     * @throws ResourceNotFoundException si el perfil no existe
     */
    void deleteById(Long id);

    /**
     * Actualiza un perfil existente en el sistema.
     * <p>
     * Busca el perfil por ID y actualiza sus datos
     * con los proporcionados en el DTO.
     * </p>
     *
     * @param id  Identificador único del perfil a actualizar
     * @param dto DTO con los datos actualizados del perfil
     * @return DTO con los datos del perfil actualizado
     * @throws ResourceNotFoundException si el perfil no existe
     */
    PerfilResponseDto update(Long id, PerfilRequestDto dto);

    /**
     * Elimina el rol de entrenador del usuario asociado al perfil.
     *
     * @param id Identificador único del perfil
     */
    void quitarRolEntrenador(Long id);
}
