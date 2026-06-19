package com.backend.service;

import java.util.List;
import com.backend.dto.AccesoResponseDto;

/**
 * Interfaz del servicio para la gestión de accesos al gimnasio.
 * <p>
 * Define los contratos para el registro de entradas y salidas,
 * validación de tokens QR y control de aforo en tiempo real.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public interface IAccesoService {

    /**
     * Registra la entrada de un usuario al gimnasio mediante token QR.
     * <p>
     * Valida el token JWT temporal, extrae el usuario y crea
     * un registro de entrada con la fecha y hora actual.
     * </p>
     *
     * @param token Token QR temporal generado para acceso
     * @throws RuntimeException si el token es inválido o el usuario no existe
     */
    void registrarEntrada(String token);

    /**
     * Registra la salida de un usuario del gimnasio mediante token QR.
     * <p>
     * Valida el token, busca la última entrada sin registrar salida
     * y actualiza el registro con la fecha y hora de salida.
     * </p>
     *
     * @param token Token QR temporal generado para acceso
     * @throws RuntimeException si el token es inválido o no hay entrada activa
     */
    void registrarSalida(String token);

    /**
     * Obtiene el historial completo de accesos de un usuario.
     * <p>
     * Retorna todos los registros de entrada y salida del usuario
     * ordenados por fecha y hora descendente.
     * </p>
     *
     * @param username Nombre de usuario del cual se desea el historial
     * @return Lista de DTOs con todos los accesos del usuario
     */
    List<AccesoResponseDto> obtenerHistorialUsuario(String username);

    /**
     * Obtiene el número actual de personas dentro del gimnasio.
     * <p>
     * Cuenta las entradas registradas que aún no tienen
     * una salida registrada, útil para control de aforo.
     * </p>
     *
     * @return Número de usuarios actualmente dentro del gimnasio
     */
    long obtenerAforoActual();
}
