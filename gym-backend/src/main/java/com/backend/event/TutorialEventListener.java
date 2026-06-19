package com.backend.event;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TutorialEventListener {

    private final SimpMessagingTemplate messagingTemplate; 

    // Clase DTO interna solo para dar formato al JSON que viaja por WebSocket
    @Data
    @Builder
    public static class NotificacionPublicaDto {
        private String titulo;
        private String mensaje;
        private String tipo;
        private String fecha;
        private Long referenciaId;
    }

    @Async
    @EventListener
    public void handleTutorialEvent(TutorialEvent event) {
        log.info("Procesando evento de nuevo tutorial: ID {}", event.tutorialId());

        //  Construimos el DTO que se convertirá en JSON
        NotificacionPublicaDto payload = NotificacionPublicaDto.builder()
                .titulo(event.titulo())
                .mensaje(event.mensaje())
                .tipo(event.tipo().name())
                .fecha(LocalDateTime.now().toString())
                .referenciaId(event.tutorialId())
                .build();

        log.info("Haciendo broadcast del nuevo tutorial por WebSocket al canal /topic/notificaciones");

        //  ENVIAR A TODOS LOS USUARIOS (BROADCAST)
        messagingTemplate.convertAndSend(
            "/topic/notificaciones", 
            payload 
        );
    }
}