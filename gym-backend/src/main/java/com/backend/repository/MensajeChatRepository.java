package com.backend.repository;

import com.backend.domain.MensajeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface MensajeChatRepository extends JpaRepository<MensajeChat, Long> {
    // Recupera los últimos 50 mensajes para que el chat no tarde en cargar
    List<MensajeChat> findTop50ByOrderByFechaEnvioAsc();
    Page<MensajeChat> findAllByOrderByFechaEnvioDesc(Pageable pageable);
    List<MensajeChat> findByTicketIdOrderByFechaEnvioAsc(String ticketId);
}