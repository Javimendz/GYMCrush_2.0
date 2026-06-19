package com.backend.data;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.backend.domain.Role;
import com.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

/**
 * Componente que inicializa los roles del sistema al iniciar la aplicación.
 * <p>
 * Esta clase se ejecuta cuando la aplicación está completamente lista
 * (evento {@link ApplicationReadyEvent}), asegurando que los roles
 * esenciales existan en la base de datos antes de que la aplicación
 * acepte peticiones.
 * </p>
 * <p>
 * Los roles creados son:
 * <ul>
 *   <li>ROLE_USUARIO - Rol básico para usuarios estándar</li>
 *   <li>ROLE_ADMIN - Rol con privilegios administrativos</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2024
 */
@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleRepository roleRepository;

    /**
     * Inicializa los roles del sistema si no existen.
     * <p>
     * Este método se ejecuta automáticamente cuando la aplicación
     * emite el evento {@link ApplicationReadyEvent}, indicando que
     * todos los beans han sido inicializados y la aplicación está lista.
     * </p>
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeRoles() {
        if (!roleRepository.existsByName("ROLE_USUARIO")) {
            Role usuarioRole = new Role();
            usuarioRole.setName("ROLE_USUARIO");
            roleRepository.save(usuarioRole);
            System.out.println("Rol ROLE_USUARIO creado");
        }

        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
            System.out.println("Rol ROLE_ADMIN creado");
        }
        if (!roleRepository.existsByName("ROLE_ENTRENADOR")) {
            Role entrenadorRole = new Role();
            entrenadorRole.setName("ROLE_ENTRENADOR");
            roleRepository.save(entrenadorRole);
            System.out.println("Rol ROLE_ENTRENADOR creado");
        }
    }
    
}
