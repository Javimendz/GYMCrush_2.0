package com.backend.event;


import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.backend.domain.Notificacion;
import com.backend.domain.Reserva;
import com.backend.mapper.NotificacionMapper;
import com.backend.repository.NotificacionRepository;
import com.backend.repository.ReservaRepository;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class ReservaEventListener {

   private final NotificacionRepository notificacionRepo;
    private final ReservaRepository reservaRepo;
    private final SimpMessagingTemplate messagingTemplate; 
    private final NotificacionMapper notificacionMapper; // Inyecta  mapper

    @Async
    @EventListener
    @Transactional
    public void handleReservaEvent(ReservaEvent event) {
        log.info("Procesando notificación para Reserva ID: {}", event.reservaId());

        Reserva reserva = reservaRepo.findById(event.reservaId())
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        //  Crear y guardar la entidad
        Notificacion n = new Notificacion();
        n.setUsuario(reserva.getUsuario());
        n.setTitulo(event.titulo());
        n.setMensaje(event.mensaje());
        n.setTipo(event.tipo());
        n = notificacionRepo.save(n); // Guardamos para tener el ID y la fecha

        // Convertir a DTO antes de enviar
        var notificacionDto = notificacionMapper.toResponseDto(n);

        log.info("Enviando DTO por WebSocket a usuario: {}", reserva.getUsuario().getUsername());

        // Enviar el DTO
        messagingTemplate.convertAndSendToUser(
            reserva.getUsuario().getUsername(), 
            "/queue/notificaciones", 
            notificacionDto 
        );
    }
}