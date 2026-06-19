package com.backend.security.jwt;

//Imports necesarios para el filtrado de autenticación JWT
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtro personalizado para procesar tokens JWT en cada petición HTTP.
 * Este filtro se ejecuta una vez por petición y valida el token JWT,
 * estableciendo el contexto de autenticación si el token es válido.
 * 
 * @author Backend team
 * @version 1.0
 * @since 2026
 */
@Component // Componente para inyectar la clase en otras partes de la aplicación
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Generador de tokens JWT para validación y extracción de información */
    private final JwtGenerator jwtGenerator;
    
    /** Servicio de detalles de usuario para cargar información desde la base de datos */
    private final UserDetailsService userDetailsService;


    public JwtAuthenticationFilter(JwtGenerator jwtGenerator, @Lazy UserDetailsService userDetailsService) {
        this.jwtGenerator = jwtGenerator;
        this.userDetailsService = userDetailsService;
    }
    /**
     * Método principal del filtro que procesa cada petición HTTP.
     * Extrae el token JWT de la cabecera Authorization, lo valida y establece
     * el contexto de autenticación si es correcto.
     * 
     * @param request Objeto HttpServletRequest con los datos de la petición
     * @param response Objeto HttpServletResponse para la respuesta
     * @param filterChain Cadena de filtros a continuar si todo es correcto
     * @throws ServletException Si hay problemas con el procesamiento del servlet
     * @throws IOException Si hay problemas de E/S
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

                String path = request.getServletPath();
  if (path.startsWith("/ws")) {
        filterChain.doFilter(request, response);
        return;
    }

        // Extraer el token JWT de la petición HTTP
        String token = getJwtFromRequest(request);

        // Validar que el token exista, sea válido y no haya autenticación previa
        if (StringUtils.hasText(token) && jwtGenerator.validateToken(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Obtener el username directamente del JWT
            String username = jwtGenerator.getUsernameFromJwt(token);

            // Cargar los detalles actualizados del usuario desde la base de datos
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Crear la autenticación con las autoridades (roles) de la base de datos
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // Añadir detalles de la solicitud (IP, navegador, etc.)
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Establecer el usuario en el contexto de seguridad global
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Continuar con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT de la cabecera Authorization de la petición HTTP.
     * Busca la cabecera 'Authorization' y extrae el token que está en formato
     * 'Bearer <token>'.
     * 
     * @param request Objeto HttpServletRequest que contiene las cabeceras
     * @return Token JWT sin el prefijo 'Bearer ' o null si no existe
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }
}