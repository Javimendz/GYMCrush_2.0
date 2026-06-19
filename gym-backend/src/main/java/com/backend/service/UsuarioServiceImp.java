package com.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.backend.domain.enums.EnumRol;
import com.backend.dto.UsuarioResponseDto;
import com.backend.repository.RoleRepository;
import com.backend.domain.Role;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class UsuarioServiceImp implements IUsuarioService {

    private final UsuarioRepository usuarioRepository; 
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarEntrenadores() {
        log.info("Recuperando lista de entrenadores para asignación de horarios");
        return usuarioRepository.findEntrenadores();
    }

    @Override
    @Transactional
    public UsuarioResponseDto crearUsuarioConRol(UsuarioRequestDto dto, EnumRol nombreRol) {
        log.info("Iniciando creación de usuario con rol: {}", nombreRol);

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        String nombreCompletoRol = "ROLE_" + nombreRol.name();
        //  Búsqueda del Rol
       Role role = roleRepository.findByName(nombreCompletoRol)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Error: El rol " + nombreCompletoRol + " no existe en la base de datos"));
        //  Asignación del Rol
        usuario.getRoles().add(role);

        //  Guardado
        Usuario guardado = usuarioRepository.save(usuario);

        log.info("Usuario {} creado exitosamente con ID: {}", guardado.getUsername(), guardado.getId());

        //  Retorno mapeado
        return usuarioMapper.toUsuarioResponseDto(guardado);
    }


    @Override
@Transactional
public void quitarRolEntrenador(Long id) {
    //  Buscamos al usuario
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

    //  Construimos el nombre del rol (consistente con tu RoleInitializer)
    String nombreRolBusqueda = "ROLE_" + EnumRol.ENTRENADOR.name();

    //  Buscamos el objeto Role en la base de datos
    Role rolEntrenador = roleRepository.findByName(nombreRolBusqueda)
            .orElseThrow(() -> new ResourceNotFoundException("Error: El rol " + nombreRolBusqueda + " no existe en la DB"));

    //  Usamos el método helper de tu entidad para una desvinculación limpia
    usuario.removeRole(rolEntrenador);

    //  Guardamos los cambios
    usuarioRepository.save(usuario);
    
    log.info("Rol ENTRENADOR quitado exitosamente al usuario: {}", usuario.getUsername());
}

    @Override
    public Usuario save(Usuario usuario) {
        log.info("Guardando usuario: {}", usuario.getUsername());
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario generarTokenAcceso(Long id) {
        log.info("Generando token de acceso para usuario ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuario.setQrToken(java.util.UUID.randomUUID().toString());
        usuario.setTokenExpiraEn(LocalDateTime.now().plusSeconds(30));

        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        log.info("Obteniendo todos los usuarios");
        return usuarioRepository.findAll();
    }

@Override
@Transactional
public void asignarRolEntrenador(Long id) {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    String nombreRolBusqueda = "ROLE_" + EnumRol.ENTRENADOR.name(); 
    
    Role rolEntrenador = roleRepository.findByName(nombreRolBusqueda) 
                .orElseThrow(() -> new RuntimeException("Error: El rol " + nombreRolBusqueda + " no existe en la DB"));

    usuario.getRoles().add(rolEntrenador);
    
    usuarioRepository.save(usuario);
    log.info("Rol de entrenador asignado al usuario: {}", usuario.getUsername());
}

    @Override
    public Usuario findById(Long id) {
        log.info("Buscando usuario por ID: {}", id);
       return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Borrando usuario ID: {}", id);
        Usuario usuario = findById(id);

        // Limpieza de relaciones para evitar errores de integridad
        if (usuario.getRoles() != null) {
            usuario.getRoles().clear();
        }
        if (usuario.getReservas() != null) {
            usuario.getReservas().clear();
        }
        if (usuario.getRutinas() != null) {
            usuario.getRutinas().clear();
        }

        usuarioRepository.saveAndFlush(usuario);
        usuarioRepository.delete(usuario);
    }

    @Override
    public boolean validarTokenQR(String token, Long userId) {
        log.info("Validando token QR para usuario ID: {}", userId);
        Usuario usuario = findById(userId);
        if (usuario.getQrToken() == null || usuario.getTokenExpiraEn() == null) {
            return false;
        }
        return token.equals(usuario.getQrToken()) &&
                LocalDateTime.now().isBefore(usuario.getTokenExpiraEn());
    }

    @Override
    public void update(UsuarioRequestDto usuarioRequestDto, Long id) {
        log.info("Actualizando usuario con ID: {}", id);
        Usuario existente = this.findById(id);

        if (usuarioRequestDto.getPassword() != null && !usuarioRequestDto.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(usuarioRequestDto.getPassword()));
        }

        usuarioMapper.updateUsuarioFromDto(usuarioRequestDto, existente);
        usuarioRepository.save(existente);
    }

}