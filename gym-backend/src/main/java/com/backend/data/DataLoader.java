package com.backend.data;

import java.time.LocalDate;
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
import com.backend.domain.Entrenamiento;
import com.backend.domain.enums.EnumGenero;
import com.backend.repository.CategoriaDietaRepository;
import com.backend.repository.DietaRepository;
import com.backend.repository.PerfilRepository;
import com.backend.repository.RoleRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.repository.EntrenamientoRepository;
import com.backend.service.WgerSyncService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Componente que carga datos iniciales en la base de datos al iniciar la aplicación.
 * El proceso se ejecuta de manera segura aislando las transacciones críticas del negocio 
 * de las consultas externas integradas por pasarelas HTTP.
 * * @author Backend Team
 * @version 1.3
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    // --- REPOSITORIOS Y SERVICIOS CORE ---
    private final UsuarioRepository usuarioRepository; 
    private final RoleRepository roleRepository; 
    private final PasswordEncoder passwordEncoder; 
    private final PerfilRepository perfilRepository; 
    private final CategoriaDietaRepository categoriaDietaRepository;
    private final DietaRepository dietaRepository;
    
    // --- INTEGRACIÓN DE EJERCICIOS (WGER) ---
    private final WgerSyncService wgerSyncService; 
    private final EntrenamientoRepository entrenamientoRepository; 

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando el proceso de carga de datos (DataLoader)...");
        
        // 1. Ejecutar inserción de infraestructura crítica (Roles, Usuarios y Perfiles)
        inicializarInfraestructuraSistema();

        // 2. Inicialización de módulos del catálogo (Nutrición)
        inicializarNutricion();
        
        // 3. Inicialización de ejercicios mediante pasarela externa (Wger) con contingencia local
        inicializarEjercicios(); 
    }

    /**
     * Inserta los datos esenciales requeridos para la disponibilidad operativa básica.
     * Mantiene el aislamiento transaccional independiente de los catálogos secundarios.
     */
    @Transactional
    public void inicializarInfraestructuraSistema() {
        // --- 1. Inicialización de Roles del sistema ---
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

        // --- 2. Inicialización de Cuentas base ---
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin1234"));

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(usuarioRole);
            admin.setRoles(adminRoles);

            usuarioRepository.save(admin);
            log.info("Usuario admin creado.");
        }

        if (usuarioRepository.findByUsername("usuario").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername("usuario");
            usuario.setEmail("usuario@example.com");
            usuario.setPassword(passwordEncoder.encode("123456"));

            Set<Role> usuarioRoles = new HashSet<>();
            usuarioRoles.add(usuarioRole);
            usuario.setRoles(usuarioRoles);

            usuarioRepository.save(usuario);
            log.info("Usuario básico creado.");
        }

        // --- 3. Inicialización de Perfiles asociados ---
        if (!perfilRepository.existsByDni("12345678A")) {
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
            log.info("Perfil de usuario creado.");
        }

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
                    .telefono("000000000") 
                    .dni("00000000A") 
                    .fechaNacimiento(LocalDate.of(1985, 1, 1))
                    .genero(EnumGenero.Hombre)
                    .bio("Cuenta de administración principal. Acceso total al sistema de gestión.")
                    .usuario(adminUser)
                    .build();

            perfilRepository.save(perfilAdmin);
            log.info("Perfil de administrador creado.");
        }
    }

    /**
     * Gestiona la sincronización del catálogo con el API REST remoto de Wger.
     * Si detecta bloqueos de red o errores de parseo, implementa un fallback local inmediato.
     */
  private void inicializarEjercicios() {
    long count = entrenamientoRepository.count();
    if (count == 0) {
        log.info("La tabla de entrenamientos está vacía. Invocando Wger API...");
        try {
            wgerSyncService.sincronizarEjercicios();
        } catch (Exception e) {
            log.error("Error al conectar con Wger: {}", e.getMessage());
        }
        
        // RE-VERIFICACIÓN: Si después de intentar la API sigue vacía, cargamos datos manuales
        if (entrenamientoRepository.count() == 0) {
            log.info("Wger no devolvió datos, inyectando catálogo base manualmente...");
            cargarCatalogoManual();
        }
    }
}

private void cargarCatalogoManual() {
    // Ejemplo de cómo insertar manualmente para asegurar que los datos existan
    Entrenamiento e1 = Entrenamiento.builder()
            .nombre("Press de Banca")
            .descripcion("Ejercicio fundamental para el pecho.")
            .intensidad("ALTA")
            .esGlobal(true)
            .build();
    entrenamientoRepository.save(e1);
    log.info("Catálogo manual cargado con éxito.");
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

        // --- DIETAS BASE ---
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
                .objetivoCalorico(1800.0)
                .cantidadProteinas(160.0)
                .cantidadCarbohidratos(150.0)
                .cantidadGrasas(60)
                .categoriaDieta(catDefinicion)
                .build());

            dietaRepository.save(Dieta.builder()
                .nombre("Equilibrio Nutricional")
                .tipo("Normocalórica")
                .descripcion("Reparto equilibrado de macros para salud general.")
                .objetivoCalorico(2300.0)
                .cantidadProteinas(140.0)
                .cantidadCarbohidratos(250.0)
                .cantidadGrasas(75)
                .categoriaDieta(catMantenimiento)
                .build());
            
            log.info("Dietas base de catálogo creadas.");
        }
    }
}