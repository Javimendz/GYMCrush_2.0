package com.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.AccesoRequestDto;
import com.backend.dto.AccesoResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.security.jwt.JwtGenerator;
import com.backend.service.IAccesoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para gestionar las operaciones relacionadas con el acceso al
 * sistema.
 * <p>
 * Este controlador proporciona endpoints para:
 * <ul>
 * <li>Generación de tokens QR para acceso al gimnasio</li>
 * <li>Validación de entrada mediante escaneo de QR</li>
 * <li>Validación de salida mediante escaneo de QR</li>
 * <li>Consulta de aforo actual del establecimiento</li>
 * <li>Consulta de historial de accesos del usuario</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/accesos")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Acceso", description = "Operaciones relacionadas con el acceso al sistema")
public class AccesoController {

    private final IAccesoService accesoService;
    private final JwtGenerator jwtGenerator;

    /**
     * Genera un token QR para que el usuario pueda acceder al gimnasio.
     */
    @GetMapping("/generar-qr")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener token QR", description = "Genera un token QR válido por 30 segundos")
    public ResponseEntity<ApiResponseDto<String>> obtenerToken(Authentication auth) {
        log.info("Solicitud de generación de token QR para el usuario: {}", auth.getName());
        String tokenQr = jwtGenerator.generateQrToken(auth.getName(), null);
        log.info("Token QR generado para el usuario {}: {}", auth.getName(), tokenQr);

        return ResponseEntity.ok(ApiResponseDto.<String>builder()
                .mensaje("Token QR generado. Válido por 30 segundos.")
                .datos(tokenQr)
                .success(true)
                .build());
    }

    /**
     * Valida la entrada de un usuario al gimnasio mediante el escaneo de su token
     * QR.
     */
    @PostMapping("/validar-entrada")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Validar entrada", description = "Registra la entrada de un usuario por su token QR")
    public ResponseEntity<ApiResponseDto<Void>> validarEntrada(
            @Valid @RequestBody AccesoRequestDto dto) {

        log.info("Solicitud de validación de entrada para el token QR: {}", dto.getToken());
        accesoService.registrarEntrada(dto.getToken());
        log.info("Entrada registrada con éxito para el token QR: {}", dto.getToken());

        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Entrada registrada con éxito")
                .success(true)
                .build());
    }

    /**
     * Procesa un token QR genérico. 
     * Este es el endpoint que tu App de Android está buscando.
     */
    @PostMapping("/procesar-qr")
    @PreAuthorize("hasRole('ADMIN')") // Solo el personal del gimnasio escanea el QR
    @io.swagger.v3.oas.annotations.Operation(summary = "Procesar QR", description = "Punto de entrada genérico para registrar entrada o salida")
    public ResponseEntity<ApiResponseDto<Void>> procesarQr(@org.springframework.web.bind.annotation.RequestParam("token") String token) {
        
        log.info("Procesando escaneo de QR genérico. Token recibido por QueryParam");
        
        // Aquí llamamos al servicio. 
        // El servicio debe encargarse de validar el token y decidir si registra entrada o salida.
        accesoService.registrarEntrada(token); 

        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Acceso procesado correctamente")
                .success(true)
                .build());
    }

    /**
     * Valida la salida de un usuario del gimnasio mediante el escaneo de su token
     * QR.
     */
    @PostMapping("/validar-salida")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Validar salida", description = "Registra la salida de un usuario por su token QR")
    public ResponseEntity<ApiResponseDto<Void>> validarSalida(
            @Valid @RequestBody AccesoRequestDto dto) {

        log.info("Solicitud de validación de salida para el token QR: {}", dto.getToken());
        accesoService.registrarSalida(dto.getToken());
        log.info("Salida registrada con éxito para el token QR: {}", dto.getToken());

        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Salida registrada con éxito. ¡Buen descanso!")
                .success(true)
                .build());
    }

    /**
     * Consulta el aforo actual del establecimiento.
     */
    @GetMapping("/aforo")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Consultar aforo", description = "Obtiene el número actual de personas dentro del establecimiento")
    public ResponseEntity<ApiResponseDto<Long>> consultarAforo() {
        log.info("Solicitud de consulta de aforo");
        long personasDentro = accesoService.obtenerAforoActual();
        log.info("Aforo actual: {} personas dentro", personasDentro);

        return ResponseEntity.ok(ApiResponseDto.<Long>builder()
                .mensaje("Aforo actual recuperado")
                .datos(personasDentro)
                .success(true)
                .build());
    }

    
    /**
     * Obtiene el historial de accesos del usuario autenticado.
     */
    @GetMapping("/historial")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Ver historial de accesos", description = "Obtiene el historial de accesos del usuario autenticado")
    public ResponseEntity<ApiResponseDto<List<AccesoResponseDto>>> verHistorial(Authentication auth) {
        log.info("Solicitud de ver historial de accesos para el usuario: {}", auth.getName());
        List<AccesoResponseDto> historial = accesoService.obtenerHistorialUsuario(auth.getName());
        log.info("Historial de accesos recuperado para el usuario {}: {}", auth.getName(), historial);

        return ResponseEntity.ok(ApiResponseDto.<List<AccesoResponseDto>>builder()
                .mensaje("Historial de accesos recuperado correctamente")
                .datos(historial)
                .success(true)
                .build());
    }
}