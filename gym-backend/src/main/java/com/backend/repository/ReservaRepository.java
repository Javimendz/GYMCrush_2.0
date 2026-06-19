//Paquete de repositorio
package com.backend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.backend.domain.Reserva;
import com.backend.domain.enums.EnumEstado;

//Interfaz de repositorio para la entidad Reserva
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
        List<Reserva> findByUsuarioId(Long usuarioId);

        List<Reserva> findByUsuarioIdAndEstadoNot(Long usuarioId, EnumEstado estado);

        long countByHorarioIdAndFechaAndEstadoIn(Long horarioId, LocalDate fecha, List<EnumEstado> estados);

        List<Reserva> findByHorario_DiaSemanaIgnoreCaseAndEstadoNot(String diaSemana, EnumEstado estado);

        List<Reserva> findByHorario_DiaSemanaIgnoreCase(String diaSemana);

        boolean existsByUsuarioIdAndHorarioIdAndFecha(Long usuarioId, Long horarioId, LocalDate fecha);

        boolean existsByUsuarioIdAndHorarioIdAndFechaAndEstadoNot(
                        Long usuarioId,
                        Long horarioId,
                        LocalDate fecha,
                        EnumEstado estado);

        List<Reserva> findByHorarioIdAndFechaAndEstadoIn(Long horarioId, LocalDate fecha, List<EnumEstado> estados);

        long countByHorarioIdAndFechaAndEstado(Long horarioId, LocalDate fecha, String estado);

        @Query("SELECT r FROM Reserva r JOIN r.horario h " +
                        "WHERE r.fecha = :fecha " +
                        "AND h.horaInicio BETWEEN :inicio AND :fin " +
                        "AND r.estado = com.backend.domain.enums.EnumEstado.CONFIRMADA")
        List<Reserva> findReservasParaRecordatorio(
                        @Param("fecha") LocalDate fecha,
                        @Param("inicio") LocalTime inicio,
                        @Param("fin") LocalTime fin);

        List<Reserva> findByHorarioIdAndEstado(Long horarioId, EnumEstado estado);

        long countByHorarioIdAndEstadoNot(Long horarioId, EnumEstado estado);

        boolean existsByUsuarioIdAndFechaAndHorario_HoraInicioAndEstadoNot(
                        Long usuarioId, LocalDate fecha, java.time.LocalTime horaInicio, EnumEstado estado);

        long countByHorarioIdAndEstadoIn(Long horarioId, List<EnumEstado> estados);

}
