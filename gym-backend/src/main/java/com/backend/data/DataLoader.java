//Paquete
package com.backend.data;

import java.time.LocalDate;
//Imports
import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.backend.domain.CategoriaDieta;
import com.backend.domain.Dieta;
import com.backend.domain.Perfil;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.domain.enums.EnumGenero;
import com.backend.repository.CategoriaDietaRepository;
import com.backend.repository.DietaRepository;
import com.backend.repository.PerfilRepository;
import com.backend.repository.RoleRepository;
import com.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Componente que carga datos iniciales en la base de datos al iniciar la aplicación.
 * <p>
 * Esta clase implementa {@link CommandLineRunner} para ejecutar operaciones de inicialización
 * automáticamente después de que el contexto de Spring se haya cargado.
 * </p>
 * <p>
 * Se encarga de:
 * <ul>
 *   <li>Crear los roles ROLE_ADMIN y ROLE_USUARIO si no existen</li>
 *   <li>Crear usuarios de prueba (admin y usuario)</li>
 *   <li>Crear perfiles asociados a los usuarios</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository; // Repositorio de usuarios
    private final RoleRepository roleRepository; // Repositorio de roles
    private final PasswordEncoder passwordEncoder; // Codificador de contraseñas
    private final PerfilRepository perfilRepository; // Repositorio de perfiles

    /**
     * Ejecuta la carga de datos iniciales al iniciar la aplicación.
     * <p>
     * Se ejecuta dentro de una transacción para garantizar la consistencia de los datos.
     * Los datos solo se crean si no existen previamente en la base de datos.
     * </p>
     *
     * @param args argumentos de línea de comandos pasados a la aplicación
     * @throws Exception si ocurre algún error durante la inicialización
     */
    private final CategoriaDietaRepository categoriaDietaRepository;
    private final DietaRepository dietaRepository;
    @Override
    @Transactional // Usamos transactional para que lo haga todo a la vez
    public void run(String... args) throws Exception {

        

        // Se ejecta todo antes de hacer peticiones, asi creara los usuarios lo primero.

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_ADMIN");
                    return roleRepository.save(newRole);

                });

        Role usuarioRole = roleRepository.findByName("ROLE_USUARIO")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_USUARIO");
                    return roleRepository.save(newRole);

                });

        if (usuarioRepository.findByUsername("admin").isEmpty()) {

            Usuario admin = new Usuario();
            // admin.setNombre("Administrador");
            admin.setUsername("admin");
            // admin.setApellidos("admin admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin1234"));

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(usuarioRole);
            admin.setRoles(adminRoles);

            usuarioRepository.save(admin);
            System.out.println("Usuario admin creado!!..");
        }

        if (usuarioRepository.findByUsername("usuario").isEmpty()) {

            Usuario usuario = new Usuario();
            // usuario.setNombre("Usuario normal");
            usuario.setUsername("usuario");
            // usuario.setApellidos("de prueba");
            usuario.setEmail("usuario@example.com");
            usuario.setPassword(passwordEncoder.encode("123456"));

            Set<Role> usuarioRoles = new HashSet<>();

            usuarioRoles.add(usuarioRole);
            usuario.setRoles(usuarioRoles);

            usuarioRepository.save(usuario);
            System.out.println("Usuario creado!!..");
        }

        // Perfil para usuario
        if (!perfilRepository.existsByDni("12345678A")) {
            // Crear perfil
            Perfil perfil = new Perfil();
            perfil.setNombre("Perfil de prueba");
            perfil.setApellidos("de prueba");
            perfil.setTelefono("123456789");
            perfil.setDni("12345678A");
            perfil.setDireccion("Calle Falsa 123");
            perfil.setCiudad("Madrid");
            perfil.setPais("España");
            perfil.setCodigoPostal("28001");
            perfil.setFechaNacimiento(LocalDate.of(1999, 5, 15));
            perfil.setGenero(EnumGenero.Hombre);
            perfil.setUsuario(usuarioRepository.findByUsername("usuario").get());
            perfil.setBio("bio de prueba");
            perfilRepository.save(perfil);
            System.out.println("Perfil creado!!..");
        }

        // Perfil para admin
        if (!perfilRepository.existsByDni("00000000A")) {
            Usuario adminUser = usuarioRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Error: Usuario admin no encontrado"));

            Perfil perfilAdmin = Perfil.builder()
                    .nombre("Admin")
                    .apellidos("Sistema GymCrush")
                    .direccion("Calle Falsa 123")
                    .ciudad("Madrid")
                    .pais("España")
                    .codigoPostal("28001")
                    .telefono("000000000") // Debe ser unico
                    .dni("00000000A") // Debe ser unico
                    .fechaNacimiento(LocalDate.of(1985, 1, 1))
                    .genero(EnumGenero.Hombre)
                    .bio("Cuenta de administración principal. Acceso total al sistema de gestión.")
                    .usuario(adminUser)
                    .build();

            perfilRepository.save(perfilAdmin);
            System.out.println("Perfil del Administrador creado!!..");
        }

        // cuando tenga SaludRepository, añadir esto:
        /*
         * Salud inicial = Salud.builder()
         * .peso(85.5)
         * .estatura(1.80)
         * .nivelActividad("Moderado")
         * .fechaMedicion(LocalDateTime.now())
         * .usuario(user)
         * .build();
         * saludRepository.save(inicial);
         */
        inicializarNutricion();
    }


    private void inicializarNutricion() {
        // --- CATEGORÍAS ---
        CategoriaDieta catVolumen = categoriaDietaRepository.findByNombreIgnoreCase("VOLUMEN")
                .orElseGet(() -> categoriaDietaRepository.save(
                    CategoriaDieta.builder().nombre("VOLUMEN").descripcion("Plan para ganar masa muscular").build()));

        CategoriaDieta catDefinicion = categoriaDietaRepository.findByNombreIgnoreCase("DEFINICION")
                .orElseGet(() -> categoriaDietaRepository.save(
                    CategoriaDieta.builder().nombre("DEFINICION").descripcion("Plan para quema de grasa y definición").build()));

        CategoriaDieta catMantenimiento = categoriaDietaRepository.findByNombreIgnoreCase("MANTENIMIENTO")
                .orElseGet(() -> categoriaDietaRepository.save(
                    CategoriaDieta.builder().nombre("MANTENIMIENTO").descripcion("Plan para mantener el peso actual").build()));

        //  DIETAS BASE 
        // Solo creamos si la tabla de dietas está vacía para no duplicar
        if (dietaRepository.count() == 0) {
            
            dietaRepository.save(Dieta.builder()
                .nombre("Dieta Hipercalórica Estándar")
                .tipo("Hipercalórica")
                .descripcion("Enfoque en superávit de carbohidratos y proteínas.")
                .objetivoCalorico(3000.0)
                .cantidadProteinas(180.0)
                .cantidadCarbohidratos(400.0)
                .cantidadGrasas(80)
                .categoriaDieta(catVolumen)
                .build());

            dietaRepository.save(Dieta.builder()
                .nombre("Déficit Calórico Agresivo")
                .tipo("Hipocalórica")
                .descripcion("Baja en carbohidratos, alta en fibra y proteína.")
                .objetivoCalorico(1800.0    )
                .cantidadProteinas(160.0)
                .cantidadCarbohidratos(150.0)
                .cantidadCarbohidratos(150.0)
                .cantidadGrasas(60)
                .categoriaDieta(catDefinicion)
                .build());

            dietaRepository.save(Dieta.builder()
                .nombre("Equilibrio Nutricional")
                .tipo("Normocalórica")
                .descripcion("Reparto equilibrado de macros para salud general.")
                .objetivoCalorico(2300.0    )
                .cantidadProteinas(140.0    )
                .cantidadCarbohidratos(250.0    )
                .cantidadGrasas(75   )
                .categoriaDieta(catMantenimiento)
                .build());
            
            log.info("Dietas base de catálogo creadas.");
        }
    }
}