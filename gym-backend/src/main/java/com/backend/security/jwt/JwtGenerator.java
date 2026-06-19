package com.backend.security.jwt;

//Imports necesarios para la generación y validación de tokens JWT
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

/**
 * Clase responsable de generar, validar y extraer información de tokens JWT.
 * Proporciona métodos para crear tokens de acceso y QR, además de
 * validar la integridad y autenticidad de los tokens recibidos.
 * 
 * @author backend team
 * @version 1.0
 * @since 2026
 */
@Component // Componente para inyectar la clase en otras partes de la aplicación
@ConfigurationProperties(prefix = "jwt") // Propiedades de configuración para la clase, todos con prefijo jwt
public class JwtGenerator {

    /** Clave secreta para firmar los tokens JWT (configurada en application.properties) */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /** Tiempo de expiración del token en milisegundos (configurado en application.properties) */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Genera la clave de firma para los tokens JWT usando el algoritmo HMAC-SHA512.
     * Esta clave se usa tanto para firmar como para verificar los tokens.
     * 
     * @return SecretKey clave criptográfica para firmar tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     * Incluye el username, roles, fecha de emisión y expiración.
     * 
     * @param authentication Objeto de autenticación de Spring Security
     * @return Token JWT firmado y codificado
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        
        // Extraer los roles del usuario autenticado
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles) // Agregar los roles al token
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (subject) de un token JWT válido.
     * 
     * @param token Token JWT del cual extraer el username
     * @return Nombre de usuario contenido en el token
     * @throws Exception si el token es inválido o está expirado
     */
    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Extrae la lista de roles de un token JWT.
     * Los roles son almacenados como un claim personalizado en el token.
     * 
     * @param token Token JWT del cual extraer los roles
     * @return Lista de roles del usuario
     * @throws Exception si el token es inválido
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoleFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("roles", List.class);
    }

    /**
     * Valida la integridad, autenticidad y expiración de un token JWT.
     * Verifica la firma, el formato y la fecha de expiración.
     * 
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            // La forma moderna para validación también
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token); // Aquí solo nos interesa que no lance excepción
            return true;
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        } catch (SignatureException e) { // Importante para HS512
            System.out.println("Signature validation failed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Genera un token JWT temporal para acceso mediante código QR.
     * Este token tiene un tiempo de vida corto (30 segundos) y está diseñado
     * específicamente para validación de acceso físico al gimnasio.
     * 
     * @param username Nombre de usuario del titular del QR
     * @param userId ID único del usuario para trazabilidad
     * @return Token JWT temporal para acceso QR
     */
    public String generateQrToken(String username, Long userId) {
        // Fecha actual
        Date currentDate = new Date();

        // Tiempo de expiración corto (30 segundos)
        Long qrExpiration = 30000L;
        // Fecha de expiración
        Date expireDate = new Date(currentDate.getTime() + qrExpiration);
        
        // Construir el token con claims específicos para QR
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("purpose", "QR_ACCESS")
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Método alternativo para extraer el username de un token.
     * Realiza la misma función que getUsernameFromJwt pero con diferente implementación.
     * 
     * @param token Token JWT del cual extraer el username
     * @return Nombre de usuario contenido en el token
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
