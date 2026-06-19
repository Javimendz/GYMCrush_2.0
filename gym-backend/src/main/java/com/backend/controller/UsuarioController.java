package com.backend.controller;

import com.backend.domain.Perfil;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.PerfilRepository;
import com.backend.repository.RoleRepository;
import com.backend.service.IUsuarioService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.backend.security.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.backend.domain.enums.EnumRol;

/**
 * Controlador REST para gestionar los usuarios del sistema.
 * <p>
 * Este controlador proporciona endpoints para el CRUD completo de usuarios:
 * <ul>
 * <li>Listar todos los usuarios</li>
 * <li>Obtener usuario por ID</li>
 * <li>Crear nuevos usuarios (ADMIN)</li>
 * <li>Actualizar usuarios existentes</li>
 * <li>Eliminar usuarios (ADMIN)</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Usuario", description = "Operaciones relacionadas con los usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PerfilRepository perfilRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Lista todos los usuarios registrados en el sistema.
     *
     * @return ResponseEntity con la lista de usuarios
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todos los usuarios", description = "Permite a un administrador o usuario listar todos los usuarios registrados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de usuarios recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<UsuarioResponseDto>> getAllUsuarios() {
        log.info("Listando todos los usuarios");
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        log.debug("Total de usuarios recuperados: {}", usuarios.size());
        List<UsuarioResponseDto> responseDtoList = usuarioMapper.toUsuarioResponseDtoList(usuarios);
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/entrenadores")
    public ResponseEntity<ApiResponseDto<List<UsuarioResponseDto>>> getEntrenadores() {
        List<Usuario> entrenadores = usuarioService.listarEntrenadores();

        // Cambia toDtoList por el nombre real en tu UsuarioMapper (probablemente
        // toResponseDtoList)
        List<UsuarioResponseDto> dtos = usuarioMapper.toUsuarioResponseDtoList(entrenadores);

        return ResponseEntity.ok(ApiResponseDto.<List<UsuarioResponseDto>>builder()
                .mensaje("Entrenadores recuperados")
                .success(true)
                .datos(dtos)
                .build());
    }

    @PostMapping("/admin/entrenadores")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> crearEntrenador(
            @Valid @RequestBody UsuarioRequestDto dto) {
        log.info("Admin creando nuevo entrenador: {}", dto.getUsername());
        UsuarioResponseDto nuevo = usuarioService.crearUsuarioConRol(dto, EnumRol.ENTRENADOR);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDto.<UsuarioResponseDto>builder()
                        .mensaje("Entrenador dado de alta correctamente")
                        .success(true)
                        .datos(nuevo)
                        .build());
    }

    @DeleteMapping("/{id}/quitar-entrenador")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> quitarRolEntrenador(@PathVariable Long id) {
        usuarioService.quitarRolEntrenador(id);
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Rol ENTRENADOR quitado con éxito")
                .success(true)
                .build());
    }

    @PatchMapping("/{id}/asignar-entrenador")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> asignarRolEntrenador(@PathVariable Long id) {
        usuarioService.asignarRolEntrenador(id);
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Usuario actualizado a rol ENTRENADOR con éxito")
                .success(true)
                .build());
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * <p>
     * Solo los usuarios con rol ADMIN pueden crear nuevos usuarios.
     * El proceso incluye asignación de roles, codificación de contraseña
     * y creación de perfil asociado.
     * </p>
     *
     * @param usuarioDto los datos del usuario a crear
     * @return ResponseEntity con el usuario creado y estado 201 (CREATED)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear un nuevo usuario", description = "Permite al administrador crear un nuevo usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario creado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @Transactional
    public ResponseEntity<UsuarioResponseDto> createUsuario(@Valid @RequestBody UsuarioRequestDto usuarioDto) {
        log.info("Creando nuevo usuario: {}", usuarioDto.getUsername());

        // Convertir DTO a entidad
        Usuario usuarioToSave = usuarioMapper.toEntity(usuarioDto);

        // Asignar roles al usuario
        Set<Role> rolesReales = new HashSet<>();
        if (usuarioDto.getRol() != null && !usuarioDto.getRol().isEmpty()) {
            for (String nombreRol : usuarioDto.getRol()) {
                Role roleBd = roleRepository.findByName(nombreRol)
                        .orElseThrow(() -> new RuntimeException("Error: El rol " + nombreRol + " no existe en la DB"));
                rolesReales.add(roleBd);
            }
        } else {
            // Asignar rol por defecto si no se especifica
            Role defaultRole = roleRepository.findByName("ROLE_USUARIO")
                    .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_USUARIO no encontrado en la DB"));
            rolesReales.add(defaultRole);
        }
        usuarioToSave.setRoles(rolesReales);

        // Codificar la contraseña por seguridad
        usuarioToSave.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));

        // Crear perfil asociado (inicialmente null)
        Perfil perfil = usuarioToSave.getPerfil();
        usuarioToSave.setPerfil(null);

        // Guardar usuario y limpiar contexto de persistencia
        Usuario savedUsuario = usuarioService.save(usuarioToSave);
        entityManager.flush();
        entityManager.clear();

        // Crear y asociar perfil si se proporcionaron datos
        savedUsuario = usuarioService.findById(savedUsuario.getId());
        if (perfil != null) {
            perfil.setUsuario(savedUsuario);
            Perfil savedPerfil = perfilRepository.save(perfil);
            savedUsuario.setPerfil(savedPerfil);
        }

        // Convertir a DTO y retornar respuesta
        UsuarioResponseDto responseDto = usuarioMapper.toUsuarioResponseDto(savedUsuario);
        log.info("Usuario creado exitosamente con ID: {}", savedUsuario.getId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Obtiene los detalles de un usuario específico por su ID.
     * <p>
     * Permite a administradores y usuarios autenticados obtener
     * información completa de un usuario específico.
     * </p>
     *
     * @param id el ID del usuario a buscar
     * @return ResponseEntity con los datos del usuario encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener usuario por ID", description = "Permite a un administrador o usuario obtener un usuario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> getUsuarioById(
            @PathVariable("id") Long id) {

        log.info("Obteniendo usuario por ID: {}", id);
        Usuario usuario = usuarioService.findById(id);
        log.debug("Usuario encontrado: {} - {}", usuario.getId(), usuario.getUsername());

        UsuarioResponseDto responseDto = usuarioMapper.toUsuarioResponseDto(usuario);

        return ResponseEntity.ok(ApiResponseDto.<UsuarioResponseDto>builder()
                .mensaje("Detalles del usuario recuperados")
                .success(true)
                .datos(responseDto)
                .build());
    }

    /**
     * Actualiza los datos de un usuario existente.
     * <p>
     * Permite a administradores y usuarios actualizar información
     * de perfiles, roles y credenciales de seguridad.
     * </p>
     *
     * @param id         el ID del usuario a actualizar
     * @param usuarioDto los nuevos datos del usuario
     * @return ResponseEntity con el usuario actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar usuario por ID", description = "Permite a un administrador o usuario actualizar un usuario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> updateUsuario(
            @PathVariable("id") Long id,
            @Valid @RequestBody UsuarioRequestDto usuarioDto) {

        log.info("Actualizando usuario ID: {}", id);
        Usuario updatedUsuario = usuarioService.findById(id);

        usuarioMapper.updateUsuarioFromDto(usuarioDto, updatedUsuario);

        if (usuarioDto.getRol() != null && !usuarioDto.getRol().isEmpty()) {
            Set<Role> rolesReales = new HashSet<>();
            for (String nombreRol : usuarioDto.getRol()) {
                Role roleBd = roleRepository.findByName(nombreRol)
                        .orElseThrow(() -> new RuntimeException("Error: El rol " + nombreRol + " no existe en la DB"));
                rolesReales.add(roleBd);
            }
            updatedUsuario.setRoles(rolesReales);
        }

        if (usuarioDto.getPassword() != null && !usuarioDto.getPassword().isEmpty()) {
            log.debug("Actualizando contraseña para usuario: {}", updatedUsuario.getUsername());
            updatedUsuario.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
        }

        Usuario updateUsuario = usuarioService.save(updatedUsuario);
        log.info("Usuario actualizado exitosamente: {}", updateUsuario.getId());

        return ResponseEntity.ok(ApiResponseDto.<UsuarioResponseDto>builder()
                .mensaje("Usuario actualizado exitosamente")
                .success(true)
                .datos(usuarioMapper.toUsuarioResponseDto(updateUsuario))
                .build());
    }

    /**
     * Elimina un usuario del sistema.
     * <p>
     * Solo los usuarios con rol ROLE_ADMIN pueden eliminar usuarios.
     * Busca al usuario por ID y lo elimina permanentemente de la base de datos.
     * </p>
     *
     * @param id el ID del usuario a eliminar
     * @return ResponseEntity con estado 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar usuario por ID", description = "Permite al administrador eliminar un usuario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<Void>> deleteUsuario(
            @PathVariable("id") Long id) {
        log.info("Eliminando usuario ID: {}", id);

        // Eliminar usuario por ID
        usuarioService.deleteById(id);
        log.info("Usuario eliminado exitosamente");

        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Usuario eliminado permanentemente del sistema")
                .success(true)
                .build());
    }
}
