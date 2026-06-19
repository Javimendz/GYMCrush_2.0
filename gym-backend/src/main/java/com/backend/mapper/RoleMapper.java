package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.Role;
import com.backend.dto.RoleDto;

/**
 * Mapper de MapStruct para la entidad {@link Role}.
 * <p>
 * Convierte entre la entidad Role y sus DTOs.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * Convierte una entidad Role a su DTO.
     *
     * @param role la entidad a convertir
     * @return el DTO de respuesta
     */
    @Mapping(target = "name", source = "name")
    RoleDto toDto(Role role);

    /**
     * Convierte un DTO a entidad Role.
     * Ignora la lista de usuarios asociados.
     *
     * @param roleDto el DTO a convertir
     * @return la entidad Role
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "usuarios", ignore = true)
    Role toEntity(RoleDto roleDto);

    /**
     * Convierte una lista de entidades Role a lista de DTOs.
     *
     * @param roles la lista de entidades
     * @return la lista de DTOs
     */
    List<RoleDto> toDtoList(List<Role> roles);
}
