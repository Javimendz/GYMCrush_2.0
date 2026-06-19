//Imports
package com.backend.repository;

//Paquete de repositorio
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.domain.Horario;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    /**
     * Busca todos los horarios de un día específico (ej: "LUNES").
     * Uso IgnoreCase para que no importe si el cliente envía "lunes" o "Lunes".
     */
    List<Horario> findByDiaSemanaIgnoreCase(String diaSemana);

    /**
     * Busca todos los horarios asignados a una actividad concreta.
     * Útil para ver cuándo se imparte "Crossfit" a lo largo de la semana.
     */
    List<Horario> findByActividadId(Long actividadId);

    /**
     * Filtro combinado: Actividad + Día.
     */
    List<Horario> findByActividadIdAndDiaSemanaIgnoreCase(Long actividadId, String diaSemana);

    /**
     * Ordena los horarios de un día por hora de inicio.
     * Crucial para que el cronograma no aparezca desordenado en el móvil.
     */
    List<Horario> findByDiaSemanaIgnoreCaseOrderByHoraInicioAsc(String diaSemana);

    // Metodo para buscar si existe un solapamiento de horarios en una sala dada,
    // para evitar conflictos al asignar horarios a salas.
    @Query("SELECT COUNT(h) > 0 FROM Horario h WHERE h.sala.id = :salaId " +
            "AND h.diaSemana = :dia " +
            "AND ((h.horaInicio < :fin AND h.horaFin > :inicio))")
    boolean existeSolapamiento(
            @Param("salaId") Long salaId,
            @Param("dia") String dia,
            @Param("inicio") LocalTime inicio,
            @Param("fin") LocalTime fin);

    @Query("SELECT COUNT(h) > 0 FROM Horario h " +
            "WHERE h.sala.id = :salaId " +
            "AND h.diaSemana = :dia " +
            "AND h.id <> :id " +
            "AND ((h.horaInicio < :fin AND h.horaFin > :inicio))")
    boolean existeSolapamientoActualizar(
            @Param("id") Long id,
            @Param("salaId") Long salaId,
            @Param("dia") String dia,
            @Param("inicio") LocalTime inicio,
            @Param("fin") LocalTime fin);

}
