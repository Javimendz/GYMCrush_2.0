package com.backend.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.backend.domain.Perfil;
import com.backend.domain.Role;
import com.backend.dto.PerfilRequestDto;
import com.backend.dto.PerfilResponseDto;

/**
 * Mapper de MapStruct para la entidad {@link Perfil}.
 * <p>
 * Convierte entre la entidad Perfil y sus DTOs de request/response.
 * Incluye mapeos personalizados para extraer información del usuario asociado
 * y convertir los roles a strings.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PerfilMapper {

  /**
   * Convierte una entidad Perfil a DTO de respuesta.
   * Extrae username, correo y roles del usuario asociado.
   * Los campos de salud (peso, estatura, imc, nivelActividad) se ignoran
   * y se enriquecen posteriormente en el servicio.
   *
   * @param perfil la entidad a convertir
   * @return el DTO de respuesta
   */
  @Mapping(target = "username", source = "usuario.username")
  @Mapping(target = "usuarioId", source = "usuario.id")
  @Mapping(target = "correo", source = "usuario.email")
  @Mapping(target = "rol", source = "usuario.roles", qualifiedByName = "mapRolesToStrings") // <-- CAMBIO AQUÍ
  @Mapping(target = "peso", ignore = true)
  @Mapping(target = "estatura", ignore = true)
  @Mapping(target = "imc", ignore = true)
  @Mapping(target = "nivelActividad", ignore = true)
  PerfilResponseDto toDto(Perfil perfil);

  /**
   * Actualiza una entidad Perfil existente con datos del DTO de request.
   * Ignora el ID y el usuario para preservar la integridad referencial.
   *
   * @param dto    el DTO con los datos actualizados
   * @param perfil la entidad a actualizar
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "usuario", ignore = true)
  void updatePerfilFromDto(PerfilRequestDto dto, @MappingTarget Perfil perfil);

  /**
   * Convierte una lista de entidades Perfil a lista de DTOs de respuesta.
   *
   * @param perfiles la lista de entidades
   * @return la lista de DTOs
   */
  List<PerfilResponseDto> toDtoList(List<Perfil> perfiles);

  /**
   * Método personalizado para convertir un Set de Roles a lista de strings.
   *
   * @param roles el conjunto de roles del usuario
   * @return lista con los nombres de los roles
   */
  @Named("mapRolesToStrings")
  default List<String> mapRolesToStrings(Set<Role> roles) {
    if (roles == null || roles.isEmpty())
      return Collections.emptyList();
    return roles.stream()
        .map(Role::getName) // Asegúrate que el método sea getNombre()
        .collect(Collectors.toList());
  }
}
