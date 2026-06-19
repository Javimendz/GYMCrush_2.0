//Paquete
package com.backend.service;

//Imorts
import java.util.List;
import com.backend.dto.UsuarioResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.domain.enums.EnumRol;
import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;

/**
 * Interfaz del servicio para la gestión de usuarios del sistema.
 * <p>
 * Define los contratos para las operaciones CRUD de usuarios,
 * incluyendo generación de tokens QR y validación de acceso.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
public interface IUsuarioService {

    /**
     * Guarda un nuevo usuario en la base de datos.
     * <p>
     * Codifica la contraseña antes de guardarla por seguridad.
     * </p>
     *
     * @param usuario Entidad Usuario con todos los datos a guardar
     * @return Entidad Usuario guardada con ID asignado
     */
    Usuario save(Usuario usuario);
    void quitarRolEntrenador(Long id);
    List<Usuario> listarEntrenadores();

    /**
     * Obtiene una lista de todos los usuarios registrados en el sistema.
     *
     * @return Lista de entidades Usuario
     */
    List<Usuario> getAllUsuarios();

    void asignarRolEntrenador(Long id);

    /**
     * Genera un token QR temporal para acceso físico al gimnasio.
     * <p>
     * Crea un UUID único como token con validez de 30 segundos,
     * útil para control de acceso mediante código QR en la entrada.
     * </p>
     *
     * @param id ID del usuario que solicita el token de acceso
     * @return Entidad Usuario actualizada con el token QR generado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    Usuario generarTokenAcceso(Long id);

    /**
     * Busca un usuario por su identificador único.
     *
     * @param id Identificador único del usuario a buscar
     * @return Entidad Usuario encontrada
     */
    Usuario findById(Long id);

    /**
     * Elimina un usuario del sistema por su identificador.
     * <p>
     * Busca al usuario por ID y lo elimina permanentemente
     * de la base de datos.
     * </p>
     *
     * @param id Identificador único del usuario a eliminar
     * @throws ResourceNotFoundException si el usuario no existe
     */
    void deleteById(Long id);

    /**
     * Actualiza un usuario existente en el sistema.
     * <p>
     * Busca al usuario por ID y actualiza sus datos.
     * </p>
     *
     * @param usuarioRequestDto Entidad con los datos actualizados
     * @param id                Identificador único del usuario a actualizar
     */
    void update(UsuarioRequestDto usuarioRequestDto, Long id);

    /**
     * Valida un token QR para un usuario específico.
     * <p>
     * Comprueba que el token coincida y no haya expirado,
     * útil para control de acceso en la entrada del gimnasio.
     * </p>
     *
     * @param token  Token QR a validar
     * @param userId ID del usuario propietario del token
     * @return true si el token es válido y no ha expirado, false en caso contrario
     */
    boolean validarTokenQR(String token, Long userId);

    UsuarioResponseDto crearUsuarioConRol(UsuarioRequestDto dto, EnumRol nombreRol);
}
