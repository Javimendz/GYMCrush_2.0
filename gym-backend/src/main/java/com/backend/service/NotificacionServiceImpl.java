package com.backend.service;

import com.backend.domain.Notificacion;
import com.backend.domain.Usuario;
import com.backend.domain.enums.TipoNotificacion;
import com.backend.dto.NotificacionRequestDto;
import com.backend.dto.NotificacionResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.NotificacionMapper;
import com.backend.repository.NotificacionRepository;
import com.backend.repository.UsuarioRepository; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor // Crea el constructor para la inyección de dependencias de Lombok
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionMapper notificacionMapper;

    @Override
    @Transactional
    public NotificacionResponseDto crear(NotificacionRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
        .orElseThrow(() -> new ResourceNotFoundException("El socio con ID " + dto.getUsuarioId() + " no existe."));

        Notificacion entidad = notificacionMapper.toEntity(dto);
        entidad.setUsuario(usuario);
        
        return notificacionMapper.toResponseDto(notificacionRepository.save(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDto> listarPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(notificacionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void marcarComoLeida(Long id) {
        notificacionRepository.findById(id).ifPresent(n -> {
            n.setLeido(true);
            notificacionRepository.saveAndFlush(n); 
            System.out.println("DEBUG: Notificación " + id + " marcada como leída.");
        });
    }

    @Override
    @Transactional
    public long contarNoLeidas(Long usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidoFalse(usuarioId);
    }

    @Override
    @Transactional
    public void enviarNotificacionRapida(Long usuarioId, String titulo, String mensaje, TipoNotificacion tipo) {
        NotificacionRequestDto dto = NotificacionRequestDto.builder()
                .usuarioId(usuarioId)
                .titulo(titulo)
                .mensaje(mensaje)
                .tipo(tipo)
                .build();
        crear(dto);
    }

    @Override
@Transactional
public void eliminar(Long id) {
    // Verificamos si existe antes de borrar para lanzar una excepción controlada
    Notificacion n = notificacionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: La notificación con ID " + id + " no existe."));
    
    notificacionRepository.delete(n);
}

   @Override
@Transactional
public void enviarNotificacionAUsuario(Long usuarioId, String mensaje) {
    this.enviarNotificacionRapida(usuarioId, "Aviso del Gimnasio", mensaje, TipoNotificacion.RECORDATORIO);
}
}