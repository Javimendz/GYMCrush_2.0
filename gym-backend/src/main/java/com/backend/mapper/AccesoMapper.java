package com.backend.mapper;

import com.backend.domain.Acceso;
import com.backend.dto.AccesoResponseDto;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper manual para convertir entidades {@link Acceso} a DTOs de respuesta.
 * <p>
 * Este mapper utiliza el patrón Builder para construir los DTOs y extrae
 * información del usuario asociado al acceso.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Component
public class AccesoMapper {

    /**
     * Convierte una entidad Acceso a su correspondiente DTO de respuesta.
     * Extrae el username del usuario asociado al acceso.
     *
     * @param acceso la entidad Acceso a convertir
     * @return el DTO de respuesta con los datos del acceso, o null si la entidad es
     *         null
     */
    public AccesoResponseDto toDto(Acceso acceso) {
        if (acceso == null)
            return null;

        return AccesoResponseDto.builder()
                .id(acceso.getId())
                .username(acceso.getUsuario().getUsername())
                .fechaHoraEntrada(acceso.getFechaHoraEntrada())
                .fechaHoraSalida(acceso.getFechaHoraSalida())
                .tipo(acceso.getTipo())
                .build();
    }

    /**
     * Convierte una lista de entidades Acceso a una lista de DTOs de respuesta.
     *
     * @param accesos la lista de entidades a convertir
     * @return la lista de DTOs de respuesta
     */
    public List<AccesoResponseDto> toDtoList(List<Acceso> accesos) {
        return accesos.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}