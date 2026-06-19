package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.backend.domain.enums.TipoNotificacion;
import com.backend.domain.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Obtiene todas las notificaciones de un usuario específico.
     * Ordenadas por fecha de creación descendente (las más nuevas primero).
     */
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
boolean existsByTicket_IdAndTipo(Long ticketId, TipoNotificacion tipo);    /**
     * Obtiene SOLO las notificaciones que el usuario aún no ha leído.
     * Ideal para mostrar un filtro de "Nuevas".
     *
     * @param usuarioId ID del usuario
     * @return Lista de notificaciones no leídas
     */
    List<Notificacion> findByUsuarioIdAndLeidoFalseOrderByFechaCreacionDesc(Long usuarioId);

    /**
     * Cuenta cuántas notificaciones tiene un usuario que aún no han sido leídas.
     * Útil para mostrar el "punto rojo" o contador en la App de Android.
     */
    long countByUsuarioIdAndLeidoFalse(Long usuarioId);

    /**
     * Obtiene solo las notificaciones no leídas de un usuario.
     */
    List<Notificacion> findByUsuarioIdAndLeidoFalse(Long usuarioId);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leido = true WHERE n.usuario.id = :usuarioId AND n.leido = false")
    void marcarTodasComoLeidasPorUsuarioId(@Param("usuarioId") Long usuarioId);
}