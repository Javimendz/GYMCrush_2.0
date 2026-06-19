package com.backend.controller;

import java.util.List;
import com.backend.domain.MensajeChat;
import com.backend.repository.MensajeChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/chat") // Para que Android no dé problemas de CORS
public class ChatRestController {

    @Autowired
    private MensajeChatRepository mensajeRepository;

    // --- REST: Historial con Paginación ---
    @GetMapping("/historial")
    public List<MensajeChat> getHistorial(
            @PageableDefault(size = 50) Pageable pageable) {

        // Trae los últimos 50 mensajes de forma eficiente
        Page<MensajeChat> pagina = mensajeRepository.findAllByOrderByFechaEnvioDesc(pageable);

        return pagina.getContent();
    }

    @GetMapping("/historial/{ticketId}")
    public List<MensajeChat> getHistorialTicket(@PathVariable String ticketId) {
        // Necesitarás crear este método en tu repositorio
        return mensajeRepository.findByTicketIdOrderByFechaEnvioAsc(ticketId);
    }
}