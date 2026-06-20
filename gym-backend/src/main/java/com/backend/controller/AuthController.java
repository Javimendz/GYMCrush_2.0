//Paquete
package com.backend.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.Perfil;
import com.backend.domain.Usuario;
import com.backend.domain.enums.TipoNotificacion;
import com.backend.dto.FaceLoginRequest;
import com.backend.dto.GoogleLoginRequest;
import com.backend.dto.UsuarioResponseDto;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.UsuarioRepository;
import com.backend.security.dto.ApiResponseDto;
import com.backend.security.dto.JwtAuthResponseDto;
import com.backend.security.dto.LoginDto;
import com.backend.security.dto.RegisterDto;
import com.backend.security.jwt.JwtGenerator;
import com.backend.service.NotificacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.backend.dto.FaceLoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para gestionar la autenticación de usuarios.
 * <p>
 * Este controlador proporciona endpoints para:
 * <ul>
 * <li>Inicio de sesión de usuarios existentes</li>
 * <li>Registro de nuevos usuarios</li>
 * </ul>
 * Maneja la generación de tokens JWT para la autenticación segura.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Auth", description = "Operaciones relacionadas con la autenticacion de usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final NotificacionService notificacionService;
    private final UserDetailsService userDetailsService;
    // Instanciamos el mapeador de JSON para leer el vector del usuario de la BD
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Umbral de tolerancia geométrico (0.4 es el estándar industrial para biometría
    // facial)
    private static final double UMBRAL_TOLERANCIA = 0.5;
    // Inyectamos el Client ID desde tu application.properties
    @Value("${google.client-id}")
    private final String googleClientId;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtGenerator jwtGenerator,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioMapper usuarioMapper,
            NotificacionService notificacionService,
            UserDetailsService userDetailsService,
            @Value("${google.client-id}") String googleClientId) { // Inyección directa aquí
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.notificacionService = notificacionService;
        this.userDetailsService = userDetailsService;
        this.googleClientId = googleClientId;
    }

    /**
     * Autentica un usuario y genera un token JWT.
     * <p>
     * Valida las credenciales del usuario y, si son correctas, genera un token JWT
     * que debe ser usado en las peticiones subsiguientes para autenticación.
     * </p>
     *
     * @param loginDto las credenciales de inicio de sesión (username y password)
     * @return ResponseEntity con el token JWT y datos del usuario autenticado
     */
    @PostMapping("/login")
    @io.swagger.v3.oas.annotations.Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para iniciar sesión")
    public ResponseEntity<ApiResponseDto<JwtAuthResponseDto>> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("Intento de inicio de sesión para el usuario: {}", loginDto.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Inicio de sesión exitoso para el usuario: {}", loginDto.getUsername());

        String jwt = jwtGenerator.generateToken(authentication);
        log.debug("Token JWT generado para el usuario: {}", loginDto.getUsername());

        Usuario usuario = usuarioRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado"));
        String nombreAMostrar = (usuario.getPerfil() != null) ? usuario.getPerfil().getNombre() : usuario.getUsername();
        log.debug("Usuario autenticado: {} - Perfil: {}", usuario.getUsername(), nombreAMostrar);

        List<String> roles = usuario.getRoles().stream().map(r -> r.getName()).toList();

        JwtAuthResponseDto data = JwtAuthResponseDto.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(nombreAMostrar)
                .roles(roles)
                .accessToken(jwt)
                .tokenType("Bearer")
                .build();
        log.info("Login exitoso - Usuario: {} con roles: {}", usuario.getUsername(), roles);
        return ResponseEntity.ok(ApiResponseDto.<JwtAuthResponseDto>builder()
                .mensaje("¡Bienvenido de nuevo!")
                .datos(data)
                .success(true)
                .build());
    }

    /**
     * NUEVO ENDPOINT: Autentica un usuario mediante el token de Google (OAuth2).
     */
    @PostMapping("/google-login")
    @io.swagger.v3.oas.annotations.Operation(summary = "Iniciar sesión con Google", description = "Valida el idToken de Google y devuelve un token JWT del ecosistema")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Autenticación de Google exitosa")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "idToken de Google no válido o caducado")
    public ResponseEntity<ApiResponseDto<JwtAuthResponseDto>> loginConGoogle(
            @Valid @RequestBody GoogleLoginRequest request) {
        log.info("Intento de autenticación federada con Google recibido en el controlador");
        try {
            // 1. Instanciar el verificador de Google usando su API cliente
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // 2. Comprobar validez criptográfica de la firma del token enviado por Android
            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                throw new BadCredentialsException("El token proporcionado por Google no es válido.");
            }

            // 3. Extraer el perfil del payload verificado de Google
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // 4. Buscar si el usuario ya existe en tu DB PostgreSQL o registrarlo automáticamente si es nuevo
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElse(null);

            if (usuario == null) {
                log.info("Primer inicio de sesión para el correo {}. Creando registro de usuario federado...", email);
                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setEmail(email);
                nuevoUsuario.setUsername(email); // Asignamos el email como username por defecto
                nuevoUsuario.setPassword(passwordEncoder.encode("OAUTH2_FEDERATED_ACCOUNT_PROTECTED"));
                nuevoUsuario.setFaceLoginEnabled(false);

                Perfil nuevoPerfil = new Perfil();
                nuevoPerfil.setNombre(name);

                // Inicializar campos con restricciones NOT NULL en la base de datos
                nuevoPerfil.setCiudad("Por definir");
                nuevoPerfil.setApellidos("Por definir");
                nuevoPerfil.setDireccion("Por definir");
                nuevoPerfil.setUsuario(nuevoUsuario);
                nuevoUsuario.setPerfil(nuevoPerfil);
                nuevoPerfil.setDni("Por definir");
                nuevoPerfil.setTelefono("Por definir"); 
                nuevoPerfil.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
                nuevoPerfil.setGenero(com.backend.domain.enums.EnumGenero.Hombre);
                nuevoPerfil.setPais("Por definir");
                nuevoPerfil.setCodigoPostal("Por definir");
                
                usuario = usuarioRepository.save(nuevoUsuario);
            } else {
                log.info("Usuario federado existente localizado en la base de datos: {}", usuario.getUsername());
                // 🛠️ TRUCO: Si el usuario ya existía pero por flujo de Lazy Loading de Hibernate 
                // el perfil no se ha cargado en memoria, nos aseguramos de que el DTO reciba el nombre real de Google
                if (usuario.getPerfil() == null) {
                    Perfil perfilFallback = new Perfil();
                    perfilFallback.setNombre(name);
                    usuario.setPerfil(perfilFallback);
                }
            }

            // 5. Cargar las credenciales y autoridades en Spring Security para mantener la consistencia
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 6. Generar el JWT reutilizando tu JwtGenerator
            String jwt = jwtGenerator.generateToken(authentication);
            log.info("Autenticación exitosa vía Google para la cuenta: {}", usuario.getUsername());

            return ResponseEntity.ok(ApiResponseDto.<JwtAuthResponseDto>builder()
                    .mensaje("¡Sesión iniciada con Google correctamente!")
                    .datos(construirJwtAuthResponseDto(usuario, jwt))
                    .success(true)
                    .build());

        } catch (Exception e) {
            log.error("Fallo durante el proceso de verificación OAuth2: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.<JwtAuthResponseDto>builder()
                            .mensaje("Error de autenticación externa: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Crea una cuenta de usuario con el nombre de usuario y contraseña
     * proporcionados.
     * Si el nombre de usuario ya existe, retorna un error 400.
     * Crea automáticamente un perfil básico para el usuario.
     * </p>
     *
     * @param registerDto los datos de registro del nuevo usuario
     * @return ResponseEntity con los datos del usuario creado o mensaje de error
     */
    @PostMapping("/register")
    @io.swagger.v3.oas.annotations.Operation(summary = "Registro de usuario", description = "Registra un nuevo usuario en el sistema con soporte de biometría facial")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario registrado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de registro inválidos (username o correo ya existen)")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> registerUser(
            @Valid @RequestBody RegisterDto registerDto) {
        log.info("Intento de registro de usuario: {} con email: {}", registerDto.getUsername(),
                registerDto.getEmail());

        if (usuarioRepository.existsByUsername(registerDto.getUsername())) {
            log.warn("Registro fallido - Username ya existe: {}", registerDto.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto
                            .error("El nombre de usuario '" + registerDto.getUsername() + "' ya está en uso"));
        }

        if (usuarioRepository.existsByEmail(registerDto.getEmail())) {
            log.warn("Registro fallido - Email ya existe: {}", registerDto.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto
                            .error("El correo electrónico '" + registerDto.getEmail() + "' ya está registrado"));
        }

        try {
            // Mapeo y cifrado de contraseña
            Usuario user = usuarioMapper.registerDtoToUser(registerDto);
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            // =================================================================
            // INTEGRACIÓN BIOMÉTRICA (OPCIÓN A)
            // =================================================================
            if (registerDto.getFaceVector() != null && !registerDto.getFaceVector().isEmpty()) {
                // Serializamos el List<Float> a String JSON usando el objectMapper de la clase
                String vectorJson = objectMapper.writeValueAsString(registerDto.getFaceVector());
                user.setFaceEmbedding(vectorJson);
                user.setFaceLoginEnabled(true);
                log.info("Patrón biométrico (FaceNet 512) adjuntado con éxito para el nuevo usuario");
            } else {
                user.setFaceLoginEnabled(false);
            }
            // =================================================================

            // Gestión del Perfil
            if (user.getPerfil() != null) {
                user.getPerfil().setUsuario(user);
            } else {
                Perfil nuevoPerfil = new Perfil();
                nuevoPerfil.setNombre(user.getUsername());
                nuevoPerfil.setUsuario(user);
                user.setPerfil(nuevoPerfil);
            }

            // Guardado
            Usuario savedUser = usuarioRepository.save(user);
            log.info("Usuario registrado exitosamente: {} con ID: {}", savedUser.getUsername(), savedUser.getId());

            try {
                notificacionService.enviarNotificacionRapida(
                        savedUser.getId(),
                        "¡Bienvenido a la App!",
                        "Hola " + savedUser.getUsername()
                                + ", tu cuenta ha sido creada con éxito. ¡Ya puedes reservar tus clases!",
                        TipoNotificacion.CONFIRMACION);
                log.info("Notificación de bienvenida enviada al usuario: {}", savedUser.getId());
            } catch (Exception e) {
                log.error("Error al enviar notificación de bienvenida al usuario {}: {}", savedUser.getId(),
                        e.getMessage());
            }

            // Respuesta de éxito
            UsuarioResponseDto response = usuarioMapper.toUsuarioResponseDto(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("¡Bienvenido! Usuario registrado con éxito", response));

        } catch (Exception e) {
            log.error("Error inesperado durante el registro: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error interno al procesar el registro: " + e.getMessage()));
        }
    }

    /**
     * Autentica un usuario mediante su patrón biométrico facial directamente en el
     * controlador.
     */
    @PostMapping("/face-login")
    @io.swagger.v3.oas.annotations.Operation(summary = "Iniciar sesión por rostro", description = "Autentica al usuario comparando su vector facial y devuelve un token JWT")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reconocimiento facial exitoso")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "El rostro no coincide o el servicio no está activo")
    public ResponseEntity<ApiResponseDto<JwtAuthResponseDto>> loginPorRostro(
            @Valid @RequestBody FaceLoginRequest request) {
        log.info("Intento de inicio de sesión facial en controlador para el correo: {}", request.getEmail());
        try {
            // 1. Localizar al usuario por el correo mandado desde Android
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("No existe ninguna cuenta vinculada a este correo"));

            // 2. Verificar que tenga la plantilla biométrica registrada en su perfil
            if (usuario.getFaceEmbedding() == null || usuario.getFaceEmbedding().trim().isEmpty()) {
                throw new RuntimeException("El inicio de sesión por rostro no está configurado en tu cuenta");
            }

            // 3. Transformar la plantilla TEXT de la base de datos a un array de Floats
            List<Float> vectorGuardado;
            try {
                vectorGuardado = objectMapper.readValue(usuario.getFaceEmbedding(), new TypeReference<List<Float>>() {
                });
            } catch (Exception e) {
                log.error("Error leyendo vector de la BD para usuario: {}", usuario.getUsername(), e);
                throw new RuntimeException("Error en el formato del patrón facial almacenado");
            }

            // 4. Ejecutar la comparación espacial (Distancia Euclídea)
            double distancia = calcularDistanciaEuclidea(request.getFaceVector(), vectorGuardado);
            log.debug("Distancia calculada: {} | Umbral máximo: {}", distancia, UMBRAL_TOLERANCIA);

            if (distancia > UMBRAL_TOLERANCIA) {
                throw new RuntimeException("El rostro no coincide con el perfil del usuario");
            }

            // 5. Cargar roles utilizando tu UserDetailServiceImp inyectado automáticamente
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());

            // 6. Autenticar el contexto de Spring Security de manera limpia
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 7. Generar el JWT reutilizando tu generador actual
            String jwt = jwtGenerator.generateToken(authentication);
            log.info("Inicio de sesión facial correcto para el usuario: {}", usuario.getUsername());

            return ResponseEntity.ok(ApiResponseDto.<JwtAuthResponseDto>builder()
                    .mensaje("¡Acceso biométrico concedido!")
                    .datos(construirJwtAuthResponseDto(usuario, jwt))
                    .success(true)
                    .build());

        } catch (Exception e) {
            log.warn("Fallo en la autenticación por rostro para {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.<JwtAuthResponseDto>builder()
                            .mensaje(e.getMessage())
                            .success(false)
                            .build());
        }
    }

    /**
     * Lógica matemática interna para medir la desviación del rostro.
     */
    private double calcularDistanciaEuclidea(List<Float> v1, List<Float> v2) {
        if (v1.size() != v2.size()) {
            throw new RuntimeException("Los patrones no tienen la misma longitud estructural.");
        }
        double suma = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            double dif = v1.get(i) - v2.get(i);
            suma += dif * dif;
        }
        return Math.sqrt(suma);
    }

    /**
     * Método auxiliar para evitar duplicar la lógica de creación del DTO de
     * respuesta JWT.
     */
    private JwtAuthResponseDto construirJwtAuthResponseDto(Usuario usuario, String jwt) {
        String nombreAMostrar = (usuario.getPerfil() != null) ? usuario.getPerfil().getNombre() : usuario.getUsername();
        List<String> roles = usuario.getRoles().stream().map(r -> r.getName()).toList();

        return JwtAuthResponseDto.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(nombreAMostrar)
                .roles(roles)
                .accessToken(jwt)
                .tokenType("Bearer")
                .build();
    }
}
