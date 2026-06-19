package com.backend.security.jwt;

//Imports necesarios para el manejo de autenticación fallida
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.backend.security.dto.ApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Punto de entrada personalizado para manejar excepciones de autenticación.
 * Esta clase se ejecuta cuando un usuario intenta acceder a un recurso protegido
 * sin estar autenticado o con credenciales inválidas.
 * 
 * @author backend team
 * @version 1.0
 * @since 2026
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    /**
     * Maneja las excepciones de autenticación y devuelve una respuesta JSON estandarizada.
     * Se invoca automáticamente cuando Spring Security detecta un intento de acceso
     * no autorizado a recursos protegidos.
     * 
     * @param request Objeto HttpServletRequest de la petición fallida
     * @param response Objeto HttpServletResponse para enviar la respuesta de error
     * @param authException Excepción que contiene los detalles del error de autenticación
     * @throws IOException Si hay problemas al escribir la respuesta
     * @throws ServletException Si hay problemas con el servlet
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
       // Configurar el tipo de respuesta como JSON
        response.setContentType("application/json");
        //  Establecer el código de estado 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Crear envoltorio ApiResponseDto personalizado
        ApiResponseDto<Void> apiResponse = ApiResponseDto.<Void>builder()
                .mensaje("Acceso denegado: No tienes autorización o el token es inválido.")
                .success(false)
                .datos(null)
                .build();

        //  Convertir el objeto Java a un String JSON usando Jackson
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(apiResponse);

        //  Escribir el JSON en el cuerpo de la respuesta
        response.getWriter().write(jsonResponse);
    }
}