package com.backend.service;

import java.util.List;
import com.backend.domain.Salud;
import com.backend.domain.Usuario;
import com.backend.dto.SaludRequestDto;
import com.backend.dto.SaludResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.SaludMapper;
import com.backend.repository.SaludRepository;
import com.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio para la gestión de datos de salud y progreso
 * físico.
 * <p>
 * Esta clase maneja el registro y seguimiento de métricas de salud
 * de los usuarios, incluyendo peso, estatura, IMC y objetivos fitness.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Data
@Transactional
public class SaludServiceImp implements ISaludService {

    /** Repositorio para el acceso a datos de salud */
    private final SaludRepository saludRepository;

    /** Repositorio para el acceso a datos de usuarios */
    private final UsuarioRepository usuarioRepository;

    /** Mapper para convertir entre entidades y DTOs de salud */
    private final SaludMapper saludMapper;

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
    @Override
    public SaludResponseDto registrarProgreso(SaludRequestDto dto, Long usuarioId) {
        log.info("Registrando progreso de salud para usuario ID: {}", usuarioId);

        // Buscar al usuario o lanzar excepción si no existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        log.debug("Usuario encontrado: {}", usuario.getUsername());

        // Convertir DTO a entidad y asignar usuario
        Salud salud = saludMapper.toEntity(dto);
        salud.setUsuario(usuario);

        // Guardar la medición en la base de datos
        Salud guardada = saludRepository.save(salud);
        log.info("Medición de salud registrada con ID: {}", guardada.getId());
        return saludMapper.toDto(guardada);
    }

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
    @Override
    public List<SaludResponseDto> obtenerHistorialPorUsuario(Long usuarioId) {
        log.info("Obteniendo historial de salud para usuario ID: {}", usuarioId);

        // Buscar al usuario o lanzar excepción si no existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        // Obtener historial ordenado por fecha descendente
        List<Salud> historial = saludRepository.findByUsuarioOrderByFechaMedicionDesc(usuario);
        return saludMapper.toDtoList(historial);
    }

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
    @Override
    public SaludResponseDto obtenerProgresoActual(Long usuarioId) {
        log.info("Obteniendo progreso actual de salud para usuario ID: {}", usuarioId);

        // Buscar al usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        // Buscar la última medición o lanzar excepción si no tiene registros
        Salud actual = saludRepository.findFirstByUsuarioOrderByFechaMedicionDesc(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario aún no tiene registros de salud"));

        // Retornar la medición más reciente
        return saludMapper.toDto(actual);
    }

    /**
     * Obtiene todos los registros de salud de todos los usuarios.
     * <p>
     * Método administrativo para consultar todas las mediciones
     * registradas en el sistema.
     * </p>
     *
     * @return Lista de DTOs con todos los registros de salud
     */
    @Override
    public List<SaludResponseDto> obtenerTodosLosRegistros() {
        log.info("Obteniendo todos los registros de salud");
        return saludMapper.toDtoList(saludRepository.findAll());
    }

    /**
     * Elimina un registro de salud por su identificador.
     * <p>
     * Verifica que el registro exista antes de proceder a su eliminación.
     * </p>
     *
     * @param id Identificador único del registro a eliminar
     * @throws ResourceNotFoundException si el registro no existe
     */
    @Override
    public void eliminarRegistro(Long id) {
        log.info("Eliminando registro de salud con ID: {}", id);

        // Verificar que el registro exista antes de eliminar
        if (!saludRepository.existsById(id)) {
            throw new ResourceNotFoundException("Registro no encontrado con id: " + id);
        }

        saludRepository.deleteById(id);
        log.info("Registro de salud eliminado exitosamente", id);
    }

}
