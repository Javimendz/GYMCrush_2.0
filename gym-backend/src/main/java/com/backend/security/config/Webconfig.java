package com.backend.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing) para la API.
 * <p>
 * Esta clase configura las reglas de CORS que permiten solicitudes desde
 * orígenes externos (como la aplicación Android o web frontend) hacia la API.
 * </p>
 * <p>
 * Características configuradas:
 * <ul>
 *   <li>Orígenes permitidos (configurables vía application.properties)</li>
 *   <li>Métodos HTTP permitidos (GET, POST, PUT, DELETE, OPTIONS, PATCH)</li>
 *   <li>Cabeceras permitidas (incluido Authorization para JWT)</li>
 *   <li>Credenciales (cookies/tokens) permitidas</li>
 *   <li>Cache de pre-flight requests (1 hora)</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Configuration
public class Webconfig {

    /** 
     * Lista de orígenes permitidos para CORS.
     * Se carga desde la propiedad 'app.cors.origins' en application.properties
     */
    @Value("${app.cors.origins}")
    private String[] allowedOrigins;

    /**
     * Configura el CORS para toda la aplicación.
     * <p>
     * Permite solicitudes desde los orígenes configurados y expone
     * la cabecera Authorization necesaria para la autenticación JWT.
     * </p>
     *
     * @return el configurador de CORS personalizado
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Aplica configuración CORS a todas las rutas de la API
                registry.addMapping("/**")
                        // Orígenes permitidos (desde variable de entorno o properties)
                        .allowedOriginPatterns("*")
                        // Métodos HTTP permitidos para CORS
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        // Permite cualquier cabecera (necesario para Authorization con JWT)
                        .allowedHeaders("*")
                        // Permite envío de credenciales (cookies, headers de auth)
                        .allowCredentials(true)
                        // Cachea respuestas pre-flight durante 1 hora (3600 segundos)
                        .maxAge(3600);
            }
        };
    }


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // permite que Jackson maneje fechas modernas (LocalDateTime)
        mapper.registerModule(new JavaTimeModule()); 
        return mapper;
    }
}