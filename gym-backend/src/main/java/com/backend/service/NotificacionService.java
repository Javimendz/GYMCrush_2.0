package com.backend.service;



    
import java.util.List;
import com.backend.domain.enums.TipoNotificacion;
import com.backend.dto.NotificacionRequestDto;
import com.backend.dto.NotificacionResponseDto;

public interface NotificacionService {
    
    // Cambiamos los parámetros sueltos por el DTO de entrada
    NotificacionResponseDto crear(NotificacionRequestDto dto);
    
    List<NotificacionResponseDto> listarPorUsuario(Long usuarioId);
    
    void marcarComoLeida(Long id);
    
   
    void enviarNotificacionRapida(Long usuarioId, String titulo, String mensaje, TipoNotificacion tipo);
    

     public void enviarNotificacionAUsuario(Long usuarioId, String mensaje);
    // Para el contador de la App de Android
    long contarNoLeidas(Long usuarioId);
    void eliminar(Long id);
}