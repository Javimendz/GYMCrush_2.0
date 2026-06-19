package com.backend.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.backend.domain.Perfil;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.domain.enums.EnumGenero;
import com.backend.dto.PerfilSummaryDto;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;
import com.backend.exceptions.*;
import com.backend.repository.RoleRepository;
import com.backend.security.dto.RegisterDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, uses = RoleMapper.class, builder = @org.mapstruct.Builder(disableBuilder = true))
public abstract class UsuarioMapper {

    @Autowired
    protected RoleRepository roleRepository;

    /**
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", source = "rol", qualifiedByName = "mapStringsToRoles") // <-- Aquí
    @Mapping(target = "perfil", source = ".", qualifiedByName = "mapRegisterDtoToPerfil")
    public abstract Usuario registerDtoToUser(RegisterDto registerDto);

    @Mapping(target = "nombre", source = "perfil.nombre")
    @Mapping(target = "apellidos", source = "perfil.apellidos")
    @Mapping(target = "dni", source = "perfil.dni")
    @Mapping(target = "telefono", source = "perfil.telefono")
    @Mapping(target = "foto", source = "perfil.foto")
    @Mapping(target = "bio", source = "perfil.bio")
    @Mapping(target = "direccion", source = "perfil.direccion")
    @Mapping(target = "ciudad", source = "perfil.ciudad")
    @Mapping(target = "pais", source = "perfil.pais")
    @Mapping(target = "codigoPostal", source = "perfil.codigoPostal")
    public abstract UsuarioResponseDto toUsuarioResponseDto(Usuario usuario);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "nombre", source = "perfil.nombre")
    @Mapping(target = "apellidos", source = "perfil.apellidos")
    @Mapping(target = "foto", source = "perfil.foto")
    public abstract PerfilSummaryDto toSummaryDto(Usuario usuario);

    public abstract List<PerfilSummaryDto> toSummaryDtoList(List<Usuario> usuarios);

    /**
     * CORRECCIÓN: target cambiado de "rol" a "roles"
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", source = "rol", qualifiedByName = "mapStringsToRoles") // <-- Aquí
    @Mapping(target = "perfil.nombre", source = "nombre")
    @Mapping(target = "perfil.apellidos", source = "apellidos")
    @Mapping(target = "perfil.dni", source = "dni")
    @Mapping(target = "perfil.telefono", source = "telefono")
    @Mapping(target = "perfil.direccion", source = "direccion")
    @Mapping(target = "perfil.ciudad", source = "ciudad")
    @Mapping(target = "perfil.pais", source = "pais")
    @Mapping(target = "perfil.codigoPostal", source = "codigoPostal")
    @Mapping(target = "perfil.fechaNacimiento", source = "fechaNacimiento")
    @Mapping(target = "perfil.genero", source = "genero")
    @Mapping(target = "perfil", source = ".", qualifiedByName = "mapRequestDtoToPerfil")
    public abstract Usuario toEntity(UsuarioRequestDto dto);

    /**
     * CORRECCIÓN: target cambiado de "rol" a "roles"
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", source = "rol", qualifiedByName = "mapStringsToRoles") // <-- Aquí
    @Mapping(target = "perfil.nombre", source = "nombre")
    @Mapping(target = "perfil.apellidos", source = "apellidos")
    @Mapping(target = "perfil.dni", source = "dni")
    @Mapping(target = "perfil.telefono", source = "telefono")
    @Mapping(target = "perfil.direccion", source = "direccion")
    @Mapping(target = "perfil.ciudad", source = "ciudad")
    @Mapping(target = "perfil.pais", source = "pais")
    @Mapping(target = "perfil.codigoPostal", source = "codigoPostal")
    @Mapping(target = "perfil.fechaNacimiento", source = "fechaNacimiento")
    @Mapping(target = "perfil.genero", source = "genero")
    public abstract void updateUsuarioFromDto(UsuarioRequestDto dto, @MappingTarget Usuario usuario);

    public abstract List<UsuarioResponseDto> toUsuarioResponseDtoList(List<Usuario> usuarios);

    @Named("mapRegisterDtoToPerfil")
    protected Perfil mapRegisterDtoToPerfil(RegisterDto dto) {
        if (dto == null)
            return null;
        Perfil perfil = new Perfil();
        perfil.setNombre(dto.getNombre());
        perfil.setApellidos(dto.getApellidos());
        perfil.setDni(dto.getDni());
        perfil.setTelefono(dto.getTelefono());
        perfil.setDireccion(dto.getDireccion());
        perfil.setCiudad(dto.getCiudad());
        perfil.setPais(dto.getPais());
        perfil.setCodigoPostal(dto.getCodigoPostal());
        perfil.setFechaNacimiento(dto.getFechaNacimiento());
        perfil.setGenero(dto.getGenero());
        return perfil;
    }

    @Named("mapRequestDtoToPerfil")
    protected Perfil mapToPerfil(UsuarioRequestDto dto) {
        if (dto == null)
            return null;
        Perfil perfil = new Perfil();
        perfil.setNombre(dto.getNombre());
        perfil.setApellidos(dto.getApellidos());
        perfil.setDni(dto.getDni());
        perfil.setTelefono(dto.getTelefono());
        perfil.setDireccion(dto.getDireccion());
        perfil.setCiudad(dto.getCiudad());
        perfil.setPais(dto.getPais());
        perfil.setCodigoPostal(dto.getCodigoPostal());
        perfil.setFechaNacimiento(dto.getFechaNacimiento());
        perfil.setGenero(dto.getGenero());
        return perfil;
    }

    @Named("mapStringsToRoles")
    public Set<Role> mapStringsToRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return roleRepository.findByName("ROLE_USUARIO")
                    .map(Collections::singleton)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Rol por defecto no encontrado"));
        }
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Role no encontrado: " + name)))
                .collect(Collectors.toSet());
    }

    @Named("stringToEnumGenero")
    public EnumGenero stringToEnumGenero(String genero) {
        if (genero == null)
            return null;
        try {
            return EnumGenero.valueOf(genero.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}