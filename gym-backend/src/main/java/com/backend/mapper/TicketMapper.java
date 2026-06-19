package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.Ticket;
import com.backend.dto.TicketResponseDto;

/**
 * Mapper de MapStruct para la entidad {@link Ticket}.
 * <p>
 * Convierte entre la entidad Ticket y sus DTOs de respuesta.
 * Incluye mapeos personalizados para extraer información del usuario y
 * su perfil asociado (nombre completo).
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring")
public interface TicketMapper {

    /**
     * Convierte una entidad Ticket a DTO de respuesta.
     * Extrae ID del usuario, email, username y nombre completo del perfil.
     *
     * @param ticket la entidad a convertir
     * @return el DTO de respuesta enriquecido
     */
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioEmail", source = "usuario.email")
    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "nombreCompletoUsuario", source = "ticket")

    TicketResponseDto toDto(Ticket ticket);

    /**
     * Método personalizado para obtener el nombre completo del usuario.
     * Navega desde Ticket -> Usuario -> Perfil para obtener nombre y apellidos.
     *
     * @param ticket la entidad ticket con su usuario y perfil
     * @return el nombre completo formateado o mensaje por defecto
     */
    default String mapNombreCompleto(Ticket ticket) {
        if (ticket.getUsuario() == null || ticket.getUsuario().getPerfil() == null) {
            return "Sin perfil configurado";
        }

        String nombre = ticket.getUsuario().getPerfil().getNombre();
        String apellidos = ticket.getUsuario().getPerfil().getApellidos();

        String resultado = (nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "");
        return resultado.trim().isEmpty() ? "Nombre no definido" : resultado.trim();
    }
}
