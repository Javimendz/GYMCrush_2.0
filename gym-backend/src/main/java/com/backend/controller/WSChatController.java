package com.backend.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.backend.dto.PeticionEditarDto;
import com.backend.domain.MensajeChat;
import com.backend.dto.ChatRequestDto;
import com.backend.dto.ChatResponseDto;
import com.backend.mapper.ChatMapper;
import com.backend.repository.MensajeChatRepository;
import com.backend.dto.PeticionBorradoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WSChatController {
    
    private final MensajeChatRepository mensajeRepository;
    private final ChatMapper chatMapper;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * CANAL 1: CHAT NORMAL (Público)
     * Para que todos los usuarios hablen en general.
     */
    @MessageMapping("/chat1") // Envío desde Android a: /app/chat1
    @SendTo("/topic/canal1")  // Suscripción en Android a: /topic/canal1
    public ChatResponseDto processMessage(ChatRequestDto request, Principal principal) {
        String username = (principal != null) ? principal.getName() : "Anónimo";
        
        MensajeChat entidad = MensajeChat.builder()
                .contenido(request.contenido())
                .emisor(username)
                .fechaEnvio(LocalDateTime.now())
                .tipo(request.tipo())
                .ticketId(null) // Es nulo porque es el chat global
                .build();
        
        return chatMapper.toDto(mensajeRepository.save(entidad));
    }

    @MessageMapping("/chat1.borrar")
    public void borrarMensajeGlobal(PeticionBorradoDto request) {
        log.info("Solicitud para borrar mensaje ID: {}", request.getId());
        
        try {
            // 1. Borrar de la base de datos
            if (mensajeRepository.existsById(request.getId())) {
                mensajeRepository.deleteById(request.getId());
                
                // 2. Notificar a TODOS los usuarios conectados
                // Enviamos el objeto de petición para que Android sepa qué ID eliminar
                messagingTemplate.convertAndSend("/topic/canal1.borrados", request);
                
                log.info("Mensaje borrado y notificación enviada");
            }
        } catch (Exception e) {
            log.error("Error al borrar mensaje: {}", e.getMessage());
        }
    }



    @MessageMapping("/chat1.editar") 
    public void editarMensajeGlobal(PeticionEditarDto request) {
        log.info("Solicitud para editar mensaje ID: {}", request.getId());
        
        try {
            //  Buscar el mensaje en la base de datos
            mensajeRepository.findById(request.getId()).ifPresent(mensaje -> {
                
                //  Actualizar el contenido
                mensaje.setContenido(request.getContenido());
                // mensaje.setFechaEnvio(LocalDateTime.now()); 

                MensajeChat actualizado = mensajeRepository.save(mensaje);
                ChatResponseDto response = chatMapper.toDto(actualizado);
                
                // Notificar la edición a TODOS los suscritos
                // Enviamos el mensaje completo actualizado para que Android reemplace el anterior
                messagingTemplate.convertAndSend("/topic/canal1.editados", response);
                
                log.info("Mensaje editado con éxito");
            });
        } catch (Exception e) {
            log.error("Error al editar mensaje: {}", e.getMessage());
        }
    }

    /**
     * CANAL 2: CHAT DE TICKETS (Salas dinámicas)
     * Para comunicación privada por cada ticket de soporte/entreno.
     */
    @MessageMapping("/chat.ticket.{ticketId}") // Envío: /app/chat.ticket.5
    public void procesarMensajeTicket(@DestinationVariable String ticketId, 
                                      ChatRequestDto request, 
                                      Principal principal) {
        
        String username = (principal != null) ? principal.getName() : "Anónimo";
        log.info("Mensaje para Ticket #{}: de {}", ticketId, username);

        MensajeChat entidad = MensajeChat.builder()
                .contenido(request.contenido())
                .emisor(username)
                .fechaEnvio(LocalDateTime.now())
                .tipo(request.tipo())
                .ticketId(ticketId) // Guardamos el ID del ticket para el historial
                .build();
        
        MensajeChat guardado = mensajeRepository.save(entidad);
        ChatResponseDto response = chatMapper.toDto(guardado);

        // Difusión dinámica: Solo llega a los suscritos a ese ticket
        messagingTemplate.convertAndSend("/topic/tickets." + ticketId, response);
    }
}