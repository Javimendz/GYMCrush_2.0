package com.backend.service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.backend.domain.Actividad;
import com.backend.domain.Horario;
import com.backend.domain.Reserva;
import com.backend.domain.Sala;
import com.backend.domain.Usuario;
import com.backend.domain.enums.EnumEstado;
import com.backend.domain.enums.TipoNotificacion;
import com.backend.dto.HorarioRequestDto;
import com.backend.dto.HorarioResponseDto;
import com.backend.mapper.HorarioMapper;
import com.backend.repository.ActividadRepository;
import com.backend.repository.HorarioRepository;
import com.backend.repository.ReservaRepository;
import com.backend.repository.SalaRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.exceptions.ResourceNotFoundException;

/**
 * Implementación de la lógica de negocio para la gestión de horarios del
 * gimnasio.
 * Esta clase coordina la asignación de actividades a franjas horarias
 * específicas,
 * asegurando la integridad referencial y el control de aforo.
 * * @author JavierMendez/TFG
 * 
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HorarioServiceImp implements IHorarioService {

    private final HorarioRepository horarioRepository;
    private final ActividadRepository actividadRepository;
    private final HorarioMapper horarioMapper;
    private final SalaRepository salaRepository;
    private final NotificacionService notificacionService;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Recupera el listado completo de horarios configurados en el sistema.
     * 
     * @return Lista de {@link HorarioResponseDto} con la información técnica y
     *         comercial.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> listarTodos() {
        log.info("Listando todos los horarios con cálculo de plazas");
        List<Horario> horarios = horarioRepository.findAll();

        return horarios.stream()
                .map(this::enriquecerHorarioConPlazas) // Método de apoyo que crearemos abajo
                .toList();
    }

    /**
     * Busca un horario específico por su identificador único.
     * 
     * @param id Identificador del horario.
     * @return DTO con la información detallada del horario.
     * @throws ResourceNotFoundException si el ID no existe en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public HorarioResponseDto obtenerPorId(Long id) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con ID: " + id));
        return enriquecerHorarioConPlazas(horario);
    }

    /**
     * Crea un nuevo registro de horario vinculándolo a una actividad existente.
     * 
     * @param dto Datos de la petición (día, horas, aforo e ID de actividad).
     * @return {@link HorarioResponseDto} con los datos persistidos.
     * @throws ResourceNotFoundException si la actividad o sala referenciada no
     *                                   existe.
     */
    @Override
    @Transactional
    public HorarioResponseDto crear(HorarioRequestDto dto) {
        log.info("Iniciando creación de horario. Validando disponibilidad de sala {} el {}", dto.getSalaId(),
                dto.getDiaSemana());

        // Validar solapamiento de horario en la sala
        boolean ocupado = horarioRepository.existeSolapamiento(
                dto.getSalaId(),
                dto.getDiaSemana(),
                dto.getHoraInicio(),
                dto.getHoraFin());

        if (ocupado) {
            log.warn("Conflicto de horario detectado en sala {} para el día {}", dto.getSalaId(), dto.getDiaSemana());
            throw new IllegalArgumentException("La sala ya tiene una actividad programada en ese rango horario.");
        }

        // Buscar dependencias (Actividad, Sala y Entrenador)
        Actividad actividad = actividadRepository.findById(dto.getActividadId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear: Actividad no encontrada"));

        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear: Sala no encontrada"));

        Usuario entrenador = usuarioRepository.findById(dto.getEntrenadorId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear: Entrenador no encontrado"));

        Horario nuevoHorario = horarioMapper.toEntity(dto);
        nuevoHorario.setActividad(actividad);
        nuevoHorario.setSala(sala);
        nuevoHorario.setEntrenador(entrenador);

        Horario guardado = horarioRepository.save(nuevoHorario);
        log.info("Horario creado con éxito. ID: {}", guardado.getId());

        return enriquecerHorarioConPlazas(guardado); // <-- Devuelve el DTO con 15/15 plazas
    }

    /**
     * Actualiza la información de un horario existente y notifica a los usuarios
     * en caso de cambios críticos (hora o sala).
     * * @param id ID del horario a modificar.
     * 
     * @param dto Nuevos datos de configuración.
     * @return DTO actualizado.
     */
    @Override
    @Transactional
    public HorarioResponseDto actualizar(Long id, HorarioRequestDto dto) {
        log.info("Actualizando horario con ID: {}", id);

        // Verificar que el horario exista
        Horario existente = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Horario no encontrado"));

        // Evitar que la hora fin sea antes que la de inicio
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de finalización debe ser posterior a la hora de inicio.");
        }

        // Está la sala ocupada por OTRO horario?
        boolean ocupado = horarioRepository.existeSolapamientoActualizar(
                id,
                dto.getSalaId(),
                dto.getDiaSemana(),
                dto.getHoraInicio(),
                dto.getHoraFin());

        if (ocupado) {
            log.warn("Conflicto de disponibilidad: Sala {} ocupada el {} en ese rango.", dto.getSalaId(),
                    dto.getDiaSemana());
            throw new IllegalArgumentException(
                    "No se puede actualizar: La sala ya tiene otra actividad programada en ese horario.");
        }

        // Guardar estado antiguo para el sistema de notificaciones
        LocalTime horaAntigua = existente.getHoraInicio();
        Long salaAntiguaId = (existente.getSala() != null) ? existente.getSala().getId() : null;

        // Gestión de cambio de Actividad
        if (existente.getActividad() == null || !existente.getActividad().getId().equals(dto.getActividadId())) {
            Actividad nuevaActividad = actividadRepository.findById(dto.getActividadId())
                    .orElseThrow(() -> new ResourceNotFoundException("La nueva actividad asignada no existe"));
            existente.setActividad(nuevaActividad);
        }

        // Gestión de cambio de Sala
        if (existente.getSala() == null || !existente.getSala().getId().equals(dto.getSalaId())) {
            Sala nuevaSala = salaRepository.findById(dto.getSalaId())
                    .orElseThrow(() -> new ResourceNotFoundException("La nueva sala asignada no existe"));
            existente.setSala(nuevaSala);
        }

        // Gestión de cambio de Entrenador
        if (existente.getEntrenador() == null || !existente.getEntrenador().getId().equals(dto.getEntrenadorId())) {
            Usuario nuevoEntrenador = usuarioRepository.findById(dto.getEntrenadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("El nuevo entrenador asignado no existe"));
            existente.setEntrenador(nuevoEntrenador);
        }

        // Actualización de atributos básicos
        existente.setDiaSemana(dto.getDiaSemana());
        existente.setHoraInicio(dto.getHoraInicio());
        existente.setHoraFin(dto.getHoraFin());
        existente.setAforoMax(dto.getAforoMax());

        // Persistencia
        Horario guardado = horarioRepository.save(existente);
        HorarioResponseDto resultado = horarioMapper.toResponseDto(guardado);

        // Lógica de notificación
        boolean cambioHora = horaAntigua != null && !horaAntigua.equals(dto.getHoraInicio());
        boolean cambioSala = salaAntiguaId != null && !salaAntiguaId.equals(dto.getSalaId());

        if (cambioHora || cambioSala) {
            log.info("Cambio crítico detectado. Notificando a los socios inscritos...");
            List<Reserva> reservasAfectadas = reservaRepository.findByHorarioIdAndEstado(id, EnumEstado.CONFIRMADA);

            for (Reserva reserva : reservasAfectadas) {
                notificacionService.enviarNotificacionRapida(
                        reserva.getUsuario().getId(),
                        "Modificación de tu clase",
                        "La clase de " + guardado.getActividad().getNombre()
                                + " ha cambiado su horario o sala. Por favor, revísalo.",
                        TipoNotificacion.ALERTA);
            }
        }

        log.info("Horario {} actualizado exitosamente", id);
        return resultado;
    }

    /**
     * Elimina físicamente un horario del sistema.
     * 
     * @param id ID del registro a eliminar.
     * @throws ResourceNotFoundException si el registro no existe.
     */
    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Intentando eliminar horario con ID: {}", id);

        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: ID " + id + " no existe"));

        // 1. EL TRUCO PARA EL BORRADO SILENCIOSO:
        // Romper el enlace bidireccional desde los padres (Sala, Actividad, Entrenador)
        if (horario.getSala() != null && horario.getSala().getHorarios() != null) {
            horario.getSala().getHorarios().remove(horario);
        }

        if (horario.getActividad() != null && horario.getActividad().getHorarios() != null) {
            horario.getActividad().getHorarios().remove(horario);
        }

        if (horario.getEntrenador() != null && horario.getEntrenador().getHorarios() != null) {
            horario.getEntrenador().getHorarios().remove(horario);
        }

        // 2. Limpiar las relaciones hijas
        if (horario.getReservas() != null) {
            horario.getReservas().clear();
        }

        // 3. Ahora sí, borrar (Hibernate ya no lo verá como una entidad "viva" en otra
        // lista)
        horarioRepository.delete(horario);
        horarioRepository.flush();

        log.info("Horario {} eliminado físicamente de la base de datos", id);
    }

    /**
     * Filtra los horarios disponibles según el día de la semana.
     * 
     * @param dia Nombre del día (LUNES, MARTES, etc.).
     * @return Lista de horarios programados para dicho día.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> listarPorDia(String dia) {
        return horarioRepository.findByDiaSemanaIgnoreCase(dia).stream()
                .map(this::enriquecerHorarioConPlazas) // <-- Unificamos la lógica
                .toList();
    }

    // MÉTODO DE APOYO PARA CALCULAR LAS PLAZAS

    private HorarioResponseDto enriquecerHorarioConPlazas(Horario horario) {
        HorarioResponseDto dto = horarioMapper.toResponseDto(horario);

        // countBy siempre devuelve Long
        Long ocupadas = reservaRepository.countByHorarioIdAndEstadoNot(
                horario.getId(),
                EnumEstado.CANCELADA);

        // Convertimos ocupadas a intValue para operar con el Integer de aforoMax
        int libres = horario.getAforoMax() - ocupadas.intValue();

        dto.setPlazasLibres(libres);

        return dto;
    }

    /**
     * Obtiene el cronograma semanal de una actividad específica.
     * 
     * @param actividadId ID de la actividad (ej: Crossfit).
     * @return Lista de horarios donde se imparte la actividad.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> buscarPorActividad(Long actividadId) {
        log.info("Buscando horarios por actividad ID: {}", actividadId);
        List<HorarioResponseDto> horarios = horarioMapper.toResponseDtoList(
                horarioRepository.findByActividadId(actividadId));
        log.debug("Horarios encontrados para actividad {}: {}", actividadId, horarios.size());
        return horarios;
    }

}