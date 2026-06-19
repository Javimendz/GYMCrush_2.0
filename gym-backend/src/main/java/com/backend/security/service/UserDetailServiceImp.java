package com.backend.security.service;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.repository.UsuarioRepository;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import lombok.RequiredArgsConstructor;

/**
 * Implementación personalizada de UserDetailsService para Spring Security.
 * Esta clase es responsable de cargar los detalles del usuario desde la base de datos
 * durante el proceso de autenticación.
 * 
 * @author backend team
 * @version 1.0
 * @since 2026
 */
@Service
@RequiredArgsConstructor
public class UserDetailServiceImp implements UserDetailsService {

    /** Repositorio para acceder a los datos de usuarios en la base de datos */
    private final UsuarioRepository usuarioRepository;

    /**
     * Carga los detalles del usuario por su nombre de usuario.
     * Este método es llamado automáticamente por Spring Security durante la autenticación.
     * 
     * @param username Nombre de usuario a buscar
     * @return UserDetails con la información del usuario y sus autoridades
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true) // Evita errores de carga de roles, Lazy Loading
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));

        // Usamos el método privado para no repetir código
        return new User(
                usuario.getUsername(), 
                usuario.getPassword(), 
                mapRolesToAuthorities(usuario.getRoles()) 
        );
    }

    /**
     * Convierte un conjunto de roles de la entidad a autoridades de Spring Security.
     * Transforma los objetos Role a SimpleGrantedAuthority con el prefijo ROLE_.
     * 
     * @param roles Conjunto de roles del usuario
     * @return Colección de autoridades para Spring Security
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(java.util.Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // Ej: "ROLE_USER"
                .collect(Collectors.toSet());
    }
}