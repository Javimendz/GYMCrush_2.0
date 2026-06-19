package com.backend.repository;

import com.backend.domain.Rutina;
import com.backend.domain.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;



import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// --- AÑADE ESTA LÍNEA SI ESTÁS USANDO LA ANOTACIÓN @Repository ---
import org.springframework.stereotype.Repository; 

import java.time.LocalDate;
import java.util.List;


@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    /**
     * Busca todas las rutinas asignadas a un usuario para un día concreto.
     * Es la consulta principal para la pantalla "Mi Rutina de Hoy" en la App.
     */
@Query("SELECT r FROM Rutina r JOIN FETCH r.entrenamiento WHERE r.usuario.id = :usuarioId AND r.fechaAsignacion = :fecha ORDER BY r.orden")
List<Rutina> findByUsuarioIdAndFechaAsignacionOrderByOrdenAsc(@Param("usuarioId") Long usuarioId, @Param("fecha") LocalDate fecha);void deleteByUsuarioAndFechaAsignacionGreaterThanEqualAndCompletado(Usuario usuario, LocalDate fecha, boolean completado);
    /**
     * Busca rutinas por entrenamiento.  para ver qué usuarios 
     * tienen asignado un ejercicio específico.
     */
    List<Rutina> findByEntrenamientoId(Long entrenamientoId);


    List<Rutina> findByUsuarioIdAndFechaAsignacionBetweenOrderByOrden(
    Long usuarioId, 
    LocalDate fechaInicio, 
    LocalDate fechaFin
);
/**
     * Elimina las rutinas de los usuarios que tienen un plan específico activo
     * y cuyo estado de completado coincida con el parámetro (ej: false).
     */
    @Modifying
    @Query("DELETE FROM Rutina r WHERE r.usuario.planActivo.id = :planId AND r.completado = :completado")
    void deleteByUsuarioPlanActivoIdAndCompletado(@Param("planId") Long planId, @Param("completado") boolean completado);

    /**
     * Busca las rutinas pendientes (no completadas) de un usuario.
     */
    List<Rutina> findByUsuarioIdAndCompletadoFalse(Long usuarioId);

    boolean existsByUsuarioIdAndFechaAsignacionAndOrden(Long usuarioId, LocalDate fechaAsignacion, Integer orden);
}