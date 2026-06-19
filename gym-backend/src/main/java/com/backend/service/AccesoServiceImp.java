package com.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.domain.Acceso;
import com.backend.domain.Usuario;
import com.backend.dto.AccesoResponseDto;
import com.backend.mapper.AccesoMapper;
import com.backend.repository.AccesoRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.security.jwt.JwtGenerator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio para la gestión de accesos al gimnasio.
 * <p>
 * Esta clase maneja el registro de entradas y salidas del gimnasio,
 * validando tokens QR y manteniendo un historial completo de accesos.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccesoServiceImp implements IAccesoService {
    /** Repositorio para el acceso a datos de accesos */
    private final AccesoRepository accesoRepository;

    /** Repositorio para el acceso a datos de usuarios */
    private final UsuarioRepository usuarioRepository;

    /** Generador de tokens para validación QR */
    private final JwtGenerator jwtGenerator;

    /** Mapper para convertir entidades a DTOs */
    private final AccesoMapper accesoMapper;

    /**
     * Registra la entrada de un usuario al gimnasio mediante token QR.
     * <p>
     * Valida el token JWT temporal, extrae el usuario y crea un registro
     * de entrada con la fecha y hora actual.
     * </p>
     *
     * @param token Token QR temporal generado para acceso
     * @throws RuntimeException si el token es inválido o el usuario no existe
     */
    @Override
    @Transactional
    public void registrarEntrada(String token) {
        log.info("Registrando entrada con token QR");

        // Validar que el token QR sea correcto
        if (!jwtGenerator.validateToken(token)) {
            log.warn("Token QR inválido proporcionado");
            throw new RuntimeException("QR Inválido");
        }

        // Extraer el nombre de usuario desde el token
        String username = jwtGenerator.getUsernameFromToken(token);
        log.debug("Token válido para usuario: {}", username);

        // Buscar al usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear y guardar el registro de entrada
        Acceso entrada = Acceso.builder()
                .usuario(usuario)
                .fechaHoraEntrada(LocalDateTime.now())
                .tipo("ENTRADA")
                .build();

        accesoRepository.save(entrada);
        log.info("Entrada registrada exitosamente para usuario: {}", username);
    }

    @Override
    @Transactional
    public void registrarSalida(String token) {
        log.info("Registrando salida con token QR");
        if (!jwtGenerator.validateToken(token)) {
            log.warn("Token QR inválido proporcionado");
            throw new RuntimeException("QR Inválido");
        }

        // Obtener username del token
        String username = jwtGenerator.getUsernameFromToken(token);
        log.debug("Token válido para usuario: {}", username);

        // último registro de entrada que no tenga salida aún
        Acceso ultimaEntrada = accesoRepository
                .findFirstByUsuarioUsernameAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(username)
                .orElseThrow(() -> new RuntimeException("No se encontró una entrada activa para este usuario"));

        // Actualizar la salida
        ultimaEntrada.setFechaHoraSalida(LocalDateTime.now());
        ultimaEntrada.setTipo("SALIDA");

        accesoRepository.save(ultimaEntrada);
        log.info("Salida registrada exitosamente para usuario: {}", username);
    }

    @Override
    public List<AccesoResponseDto> obtenerHistorialUsuario(String username) {
        log.info("Obteniendo historial de accesos para usuario: {}", username);
        List<Acceso> listaEntidades = accesoRepository.findByUsuarioUsernameOrderByFechaHoraEntradaDesc(username);
        log.debug("Total de registros de acceso encontrados: {}", listaEntidades.size());

        // conversión para el mapper
        return accesoMapper.toDtoList(listaEntidades);
    }

    @Override
    public long obtenerAforoActual() {
        long aforo = accesoRepository.countByFechaHoraSalidaIsNull();
        log.debug("Aforo actual: {} usuarios en el gimnasio", aforo);
        return aforo;
    }
}