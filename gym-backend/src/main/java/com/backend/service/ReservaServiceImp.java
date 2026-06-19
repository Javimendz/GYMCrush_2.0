package com.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.Horario;
import com.backend.domain.Reserva;
import com.backend.domain.Usuario;
import com.backend.domain.enums.EnumEstado;
import com.backend.domain.enums.TipoNotificacion;
import com.backend.dto.HorarioResponseDto;
import com.backend.dto.ReservaResponseDto;
import com.backend.event.ReservaEvent;
import com.backend.exceptions.BusinessException;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.repository.ReservaRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.repository.HorarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaServiceImp implements IReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioRepository horarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Lista de estados que restan cupo (Constante para reutilizar)
    private final List<EnumEstado> ESTADOS_OCUPADOS = List.of(EnumEstado.CONFIRMADA, EnumEstado.ASISTIDA);

    @Override
    @Transactional
    public ReservaResponseDto crearReserva(Long usuarioId, Long horarioId) {
        log.info("Iniciando proceso de reserva: Usuario {}, Horario {}", usuarioId, horarioId);

        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        LocalDate fechaClase = calcularProximaFecha(horario.getDiaSemana(), horario.getHoraInicio());
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaHoraInicioClase = LocalDateTime.of(fechaClase, horario.getHoraInicio());

        if (ahora.isAfter(fechaHoraInicioClase.minusMinutes(5))) {
            throw new BusinessException("No puedes reservar: La clase está a punto de empezar o ya pasó.");
        }

        validarDuplicadoYOcupado(usuarioId, horarioId, fechaClase, horario.getHoraInicio());

        // VALIDAR CUPO
        Long inscritos = reservaRepository.countByHorarioIdAndFechaAndEstadoIn(horarioId, fechaClase, ESTADOS_OCUPADOS);

        if (inscritos.intValue() >= horario.getAforoMax()) {
            throw new BusinessException("Clase completa. Aforo máximo: " + horario.getAforoMax());
        }

        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .horario(horario)
                .fecha(fechaClase)
                .estado(EnumEstado.CONFIRMADA)
                .confirmado(false)
                .build();

        Reserva guardada = reservaRepository.save(reserva);

        // NOTIFICACIÓN: Calculamos plazas restantes después de la reserva
        int plazasRestantes = horario.getAforoMax() - (inscritos.intValue() + 1);
        publicarEventoReserva(guardada, plazasRestantes);

        return mapToDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> obtenerHorariosConPlazas(LocalDate fecha) {
        log.info("Calculando disponibilidad de plazas para la fecha: {}", fecha);

        List<Horario> horarios = horarioRepository.findAll();

        return horarios.stream()
                .map(h -> {
                    // 1. Contamos reservas para este horario y fecha específica
                    Long ocupadas = reservaRepository.countByHorarioIdAndFechaAndEstadoIn(h.getId(), fecha,
                            ESTADOS_OCUPADOS);

                    // 2. Calculamos plazas libres usando .intValue()
                    int libres = h.getAforoMax() - ocupadas.intValue();

                    // 3. Mapeamos al DTO incluyendo datos de Actividad y Sala para Android
                    return HorarioResponseDto.builder()
                            .id(h.getId())
                            .horaInicio(h.getHoraInicio())
                            .horaFin(h.getHoraFin())
                            .diaSemana(h.getDiaSemana())
                            .aforoMax(h.getAforoMax())
                            .plazasLibres(libres)
                            .nombreActividad(h.getActividad() != null ? h.getActividad().getNombre() : "Sin actividad")
                            .nombreSala(h.getSala() != null ? h.getSala().getNombre() : "Sin sala")
                            .nombreEntrenador(
                                    h.getEntrenador() != null ? h.getEntrenador().getUsername() : "Sin monitor")
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDto> obtenerReservasPorDia(String dia) {
        log.info("Consultando todas las reservas activas para el día: {}", dia);
        return reservaRepository.findByHorario_DiaSemanaIgnoreCaseAndEstadoNot(dia, EnumEstado.CANCELADA)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDto> obtenerReservasUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioIdAndEstadoNot(usuarioId, EnumEstado.CANCELADA)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaHoraClase = LocalDateTime.of(reserva.getFecha(), reserva.getHorario().getHoraInicio());

        if (ahora.isAfter(fechaHoraClase)) {
            throw new BusinessException("No puedes cancelar: Esta clase ya ha pasado o está en curso.");
        }

        if (ahora.isAfter(fechaHoraClase.minusHours(2))) {
            throw new BusinessException("Límite de cancelación excedido (mínimo 2 horas antes).");
        }

        reserva.setEstado(EnumEstado.CANCELADA);
        reservaRepository.save(reserva);

        publicarEventoCancelacion(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hayCupoDisponible(Long horarioId) {
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        Long inscritos = reservaRepository.countByHorarioIdAndFechaAndEstadoIn(horarioId, LocalDate.now(),
                ESTADOS_OCUPADOS);

        return inscritos.intValue() < horario.getAforoMax();
    }

    @Override
    @Transactional
    public void confirmarAsistencia(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        //  Forzamos la zona horaria de la aplicación (Madrid)
        java.time.ZoneId zonaMadrid = java.time.ZoneId.of("Europe/Madrid");
        java.time.ZonedDateTime ahoraZoned = java.time.ZonedDateTime.now(zonaMadrid);

        LocalDate hoyLocal = ahoraZoned.toLocalDate();
        // Truncamos segundos y nanos para evitar errores de precisión
        LocalTime ahoraLocal = ahoraZoned.toLocalTime().withSecond(0).withNano(0);

        log.info("Intento Check-in: {} {} | Clase programada: {} {}",
                hoyLocal, ahoraLocal, reserva.getFecha(), reserva.getHorario().getHoraInicio());

        //  Validar fecha
        if (!reserva.getFecha().equals(hoyLocal)) {
            throw new BusinessException("Solo puedes hacer check-in el día de la clase. Hoy es: " + hoyLocal);
        }

        //  Validar ventana de tiempo (15 min antes, 10 min después)
        LocalTime inicio = reserva.getHorario().getHoraInicio();
        LocalTime apertura = inicio.minusMinutes(15);
        LocalTime cierre = inicio.plusMinutes(10);

        // Comprobación de rango
        if (ahoraLocal.isBefore(apertura) || ahoraLocal.isAfter(cierre)) {
            throw new BusinessException(
                    String.format("Check-in fuera de rango. Tu hora: %s. Ventana permitida: %s a %s",
                            ahoraLocal, apertura, cierre));
        }

        //  Actualizar estado
        reserva.setEstado(EnumEstado.ASISTIDA);
        reservaRepository.save(reserva);

        log.info("Check-in exitoso para reserva ID: {}", reservaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDto> obtenerAsistentesPorHorario(Long horarioId) {
        return reservaRepository.findByHorarioIdAndFechaAndEstadoIn(horarioId, LocalDate.now(), ESTADOS_OCUPADOS)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String obtenerResumenAforo(Long horarioId) {
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        Long inscritos = reservaRepository.countByHorarioIdAndFechaAndEstadoIn(horarioId, LocalDate.now(),
                ESTADOS_OCUPADOS);

        return inscritos.intValue() + " / " + horario.getAforoMax();
    }

    // --- MÉTODOS PRIVADOS DE APOYO ---

    private void validarDuplicadoYOcupado(Long usuarioId, Long horarioId, LocalDate fecha, LocalTime hora) {
        boolean yaTieneReserva = reservaRepository.existsByUsuarioIdAndHorarioIdAndFechaAndEstadoNot(
                usuarioId, horarioId, fecha, EnumEstado.CANCELADA);

        if (yaTieneReserva) {
            throw new BusinessException("Ya tienes una reserva para esta sesión el día " + fecha);
        }

        boolean estaOcupado = reservaRepository.existsByUsuarioIdAndFechaAndHorario_HoraInicioAndEstadoNot(
                usuarioId, fecha, hora, EnumEstado.CANCELADA);

        if (estaOcupado) {
            throw new BusinessException("Ya tienes otra actividad a las " + hora + " el día " + fecha);
        }
    }

    private void publicarEventoReserva(Reserva reserva, int plazasRestantes) {
        String mensaje = String.format("Plaza confirmada para %s. ¡Quedan %d plazas!",
                reserva.getHorario().getActividad().getNombre(), plazasRestantes);

        eventPublisher.publishEvent(
                new ReservaEvent(reserva.getId(), "Reserva Exitosa", mensaje, TipoNotificacion.RECORDATORIO));
    }

    private void publicarEventoCancelacion(Reserva reserva) {
        eventPublisher.publishEvent(new ReservaEvent(reserva.getId(), "Reserva Cancelada",
                "Has cancelado tu clase de " + reserva.getHorario().getActividad().getNombre(),
                TipoNotificacion.CANCELACION));
    }

    private LocalDate calcularProximaFecha(String diaSemana, LocalTime horaInicio) {
        java.time.DayOfWeek targetDay = switch (diaSemana.toUpperCase()) {
            case "LUNES" -> java.time.DayOfWeek.MONDAY;
            case "MARTES" -> java.time.DayOfWeek.TUESDAY;
            case "MIERCOLES", "MIÉRCOLES" -> java.time.DayOfWeek.WEDNESDAY;
            case "JUEVES" -> java.time.DayOfWeek.THURSDAY;
            case "VIERNES" -> java.time.DayOfWeek.FRIDAY;
            case "SABADO", "SÁBADO" -> java.time.DayOfWeek.SATURDAY;
            case "DOMINGO" -> java.time.DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Día no válido: " + diaSemana);
        };

        LocalDate proximaFecha = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(targetDay));

        if (proximaFecha.equals(LocalDate.now()) && LocalTime.now().isAfter(horaInicio.minusMinutes(5))) {
            proximaFecha = proximaFecha.plusWeeks(1);
        }
        return proximaFecha;
    }

    private ReservaResponseDto mapToDto(Reserva reserva) {
        return ReservaResponseDto.builder()
                .id(reserva.getId())
                .fecha(reserva.getFecha())
                .username(reserva.getUsuario() != null ? reserva.getUsuario().getUsername() : "Anónimo")
                .estado(reserva.getEstado().name())
                .diaSemana(reserva.getHorario() != null ? reserva.getHorario().getDiaSemana() : null)
                .horaInicio(reserva.getHorario() != null ? reserva.getHorario().getHoraInicio() : null)
                .usuarioId(reserva.getUsuario() != null ? reserva.getUsuario().getId() : null)
                .nombreActividad(reserva.getHorario() != null && reserva.getHorario().getActividad() != null
                        ? reserva.getHorario().getActividad().getNombre()
                        : "Sin Actividad")
                .nombreSala(reserva.getHorario() != null && reserva.getHorario().getSala() != null
                        ? reserva.getHorario().getSala().getNombre()
                        : "Sin sala")
                .nombreEntrenador(reserva.getHorario() != null && reserva.getHorario().getEntrenador() != null
                        ? reserva.getHorario().getEntrenador().getUsername()
                        : "Monitor pendiente")
                .build();
    }
}