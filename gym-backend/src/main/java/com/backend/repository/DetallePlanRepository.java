package com.backend.repository;

import com.backend.domain.DetallePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePlanRepository extends JpaRepository<DetallePlan, Long> {

    /**
     * Obtiene todos los ejercicios de un plan maestro.
     * Es vital que estén ordenados por día y luego por orden de ejecución
     * para que el usuario no vea el postre antes que la sopa.
     */
    List<DetallePlan> findByPlanIdOrderByDiaSemanaAscOrdenAsc(Long planId);

    /**
     * Busca en qué planes está incluido un ejercicio específico.
     * Útil si vas a borrar un Entrenamiento y quieres avisar:
     * "Ojo, este ejercicio se usa en 3 planes".
     */
    List<DetallePlan> findByEntrenamientoId(Long entrenamientoId);

    /**
     * Cuenta cuántos ejercicios tiene asignados un plan.
     */
    long countByPlanId(Long planId);

    /**
     * Borra todos los ejercicios de un plan.
     * Útil si el admin decide resetear la composición de un plan.
     */
    void deleteByPlanId(Long planId);
}