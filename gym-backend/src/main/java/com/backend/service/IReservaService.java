package com.backend.service;

import java.time.LocalDate;
import java.util.List;

import com.backend.dto.HorarioResponseDto;
import com.backend.dto.ReservaResponseDto;
import com.backend.exceptions.ResourceNotFoundException;

/**
 * Interfaz del servicio para la gestión de reservas de clases.
 * <p>
 * Define los contratos para las operaciones de reservas, incluyendo
 * creación, consulta, cancelación y confirmación de asistencia.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public interface IReservaService {

    /**
     * Crea una nueva reserva para un usuario en un horario específico.
     * <p>
     * Valida que el usuario no tenga una reserva activa para el mismo día,
     * verifica disponibilidad de aforo y crea la reserva con estado ACTIVO.
     * </p>
     *
     * @param usuarioId ID del usuario que realiza la reserva
     * @param horarioId ID del horario a reservar
     * @return DTO con los datos de la reserva creada
     * @throws ResourceNotFoundException si el horario no existe
     * @throws RuntimeException          si el usuario ya tiene reserva activa o no
     *                                   hay cupo
     */
    ReservaResponseDto crearReserva(Long usuarioId, Long horarioId);

    /**
     * Obtiene el listado de reservas de un usuario.
     * <p>
     * Retorna todas las reservas del usuario excluyendo las que
     * han sido canceladas, útil para mostrar solo las activas.
     * </p>
     *
     * @param usuarioId ID del usuario del cual se desean obtener las reservas
     * @return Lista de DTO con las reservas activas del usuario
     */
    List<ReservaResponseDto> obtenerReservasUsuario(Long usuarioId);

List<HorarioResponseDto> obtenerHorariosConPlazas(LocalDate fecha);
    List<ReservaResponseDto> obtenerReservasPorDia(String dia);
    /**
     * Cancela una reserva existente cambiando su estado a CANCELADA.
     * <p>
     * Busca la reserva por su ID y actualiza el estado para liberar
     * el cupo en el horario correspondiente.
     * </p>
     *
     * @param reservaId ID de la reserva a cancelar
     * @throws ResourceNotFoundException si la reserva no existe
     */
    void cancelarReserva(Long reservaId);

    /**
     * Verifica si hay cupo disponible para un horario específico.
     * <p>
     * Compara el número de reservas activas con el aforo máximo
     * del horario para determinar si aún hay plazas disponibles.
     * </p>
     *
     * @param horarioId ID del horario a verificar disponibilidad
     * @return true si hay cupo disponible, false en caso contrario
     */
    boolean hayCupoDisponible(Long horarioId);

    /**
     * Confirma la asistencia de un usuario a una clase reservada.
     * <p>
     * Cambia el estado de la reserva a ASISTIDA, útil para
     * control de acceso mediante código QR en la entrada del gimnasio.
     * </p>
     *
     * @param reservaId ID de la reserva a confirmar asistencia
     * @throws ResourceNotFoundException si la reserva no existe
     */
    void confirmarAsistencia(Long reservaId);

    List<ReservaResponseDto> obtenerAsistentesPorHorario(Long horarioId);

    String obtenerResumenAforo(Long horarioId);
}
