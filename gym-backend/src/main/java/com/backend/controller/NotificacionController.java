package com.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import com.backend.dto.NotificacionRequestDto;
import com.backend.dto.NotificacionResponseDto;
import com.backend.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Slf4j
public class NotificacionController {

    private final NotificacionService notificacionService;

    // aplicacon del servidor
    // app/notificaciones
    @MessageMapping("/sendMessage") // Ruta a la que los clientes enviarán mensajes
    @SendTo("/topic/notificacion") // Ruta a la que se enviarán las notificaciones a los clientes suscritos
    public String sendMessage(String message) {

        System.out.println("Mensaje recibido: " + message);
        // clientes a través de WebSocket
        return "Mensaje procesado: " + message;

    }

    /**
     * Obtener todas las notificaciones de un usuario.
     * GET http://localhost:8080/api/notificaciones/usuario/1
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDto>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<NotificacionResponseDto> notificaciones = notificacionService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }

    /**
     * Crear una nueva notificación (Para uso del Admin o sistema).
     * POST http://localhost:8080/api/notificaciones
     */
    @PostMapping
    public ResponseEntity<NotificacionResponseDto> crear(@Valid @RequestBody NotificacionRequestDto dto) {
        return new ResponseEntity<>(notificacionService.crear(dto), HttpStatus.CREATED);
    }

    /**
     * Marcar una notificación como leída.
     * PATCH http://localhost:8080/api/notificaciones/1/leer
     */
    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener el número de notificaciones pendientes.
     * GET http://localhost:8080/api/notificaciones/usuario/1/count
     */
    @GetMapping("/usuario/{usuarioId}/count")
    public ResponseEntity<Long> contarNoLeidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.contarNoLeidas(usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 OK (vacío)
    }
}
