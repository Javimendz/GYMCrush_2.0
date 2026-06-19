package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.domain.Ticket;
import com.backend.domain.enums.EnumEstadoTicket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Metodo para encontrar por usuario ordenado por fecha de creacion descendente
    List<Ticket> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    // Metodo para encontrar por estado del ticket
    List<Ticket> findByEstado(EnumEstadoTicket estado);

}
