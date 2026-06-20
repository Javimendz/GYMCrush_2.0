package com.backend.security.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.backend.security.jwt.JwtAuthEntryPoint;
import com.backend.security.jwt.JwtAuthenticationFilter;

/**
 * Configuración principal de seguridad de la aplicación.
 * <p>
 * Esta clase configura:
 * <ul>
 * <li>Autenticación JWT con filtros personalizados</li>
 * <li>Autorización basada en roles y perfiles</li>
 * <li>CORS para comunicación con frontend</li>
 * <li>Codificación de contraseñas con BCrypt</li>
 * <li>Gestión de sesiones stateless (sin estado)</li>
 * <li>Logout y limpieza del contexto de seguridad</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    /** Manejador de errores de autenticación JWT */
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    /** Filtro de autenticación JWT */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    /** Entorno de Spring para detectar perfiles activos */
    private final Environment env;

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * <p>
     * Define las reglas de autorización para diferentes rutas:
     * <ul>
     * <li>Rutas públicas: /api/v1/auth/**, /chats/**</li>
     * <li>Rutas de documentación (solo en dev): /swagger-ui/**,
     * /v3/api-docs/**</li>
     * <li>Resto de rutas: requieren autenticación</li>
     * </ul>
     * </p>
     *
     * @param http el objeto HttpSecurity a configurar
     * @return la cadena de filtros de seguridad configurada
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilita CSRF (usamos JWT, no sesiones con cookies)
                .csrf(AbstractHttpConfigurer::disable)
                // Habilita CORS con configuración por defecto
                .cors(Customizer.withDefaults())
                
                // Configura el manejador de errores de autenticación
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
                // Configura sesiones stateless (sin estado)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configura las reglas de autorización
                .authorizeHttpRequests(auth -> {
                    // Rutas siempre públicas (login, registro)
                    auth.requestMatchers("/api/v1/auth/**").permitAll();
                    auth.requestMatchers("/api/v1/auth/google-login").permitAll();
                    auth.requestMatchers("/ws/**").permitAll();
                    auth.requestMatchers("/ws-gym/**").permitAll();
                    auth.requestMatchers("/chats/**").permitAll();
                    auth.requestMatchers("/api/chat/historial").permitAll();
                    auth.requestMatchers("/chats-sockjs/**").permitAll();
                    auth.requestMatchers("/api/v1/usuarios/**").hasAnyRole("ADMIN", "USUARIO");
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/usuarios/entrenadores").hasAnyRole("ADMIN", "USUARIO");
                    auth.requestMatchers("/api/v1/admin/tutoriales/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/tutoriales/**").permitAll(); // Para la App móvil
                    // Rutas de documentación Swagger (solo en perfil 'dev')
                    if (env.acceptsProfiles(Profiles.of("dev"))) {
                        auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                                "/v3/api-docs.yaml", "/webjars/**", "/swagger-resources/**").permitAll();

                    }

                    // Cualquier otra solicitud requiere autenticación
                    auth.anyRequest().authenticated();
                })
                // Configura el endpoint de logout
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            // Limpia el contexto de seguridad al cerrar sesión
                            // Nota: Aquí se podría implementar una lista de tokens invalidados (blacklist)
                            SecurityContextHolder.clearContext();
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Devuelve 200 OK en lugar de redireccionar (comportamiento REST)
                            response.setStatus(200);
                        }));

        // Añade el filtro JWT antes del filtro de autenticación por usuario/contraseña
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Configura el codificador de contraseñas.
     * <p>
     * Utiliza BCrypt para hashing seguro de contraseñas.
     * </p>
     *
     * @return el codificador de contraseñas BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    

    /**
     * Configura el administrador de autenticación.
     * <p>
     * Gestiona la autenticación de usuarios utilizando los providers configurados.
     * </p>
     *
     * @param authenticationConfiguration la configuración de autenticación
     * @return el administrador de autenticación
     * @throws Exception si ocurre un error al obtener el administrador
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
