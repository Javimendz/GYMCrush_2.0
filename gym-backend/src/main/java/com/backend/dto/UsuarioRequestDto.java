package com.backend.dto;

// Importaciones necesarias para validaciones, anotaciones y tipos de datos
import lombok.Data;
import jakarta.validation.constraints.*; // Importante para validaciones de campos

import java.time.LocalDate;
import java.util.Set;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.backend.domain.enums.EnumGenero;

/**
 * Data Transfer Object (DTO) para solicitudes de registro y actualización de
 * usuarios.
 * <p>
 * Esta clase representa la estructura de datos recibida del cliente
 * cuando se crea o actualiza un usuario en el sistema. Incluye todas
 * las validaciones necesarias para garantizar la integridad de los datos.
 * </p>
 * 
 * <p>
 * <b>Información incluida:</b>
 * <ul>
 * <li><b>Datos personales:</b> nombre, apellidos, DNI, fecha de nacimiento</li>
 * <li><b>Datos de contacto:</b> correo, teléfono, dirección completa</li>
 * <li><b>Datos de autenticación:</b> username, contraseña</li>
 * <li><b>Datos adicionales:</b> género, roles de usuario</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Validaciones aplicadas:</b>
 * <ul>
 * <li><b>Campos obligatorios:</b> @NotBlank para strings, @NotNull para
 * objetos</li>
 * <li><b>Formatos específicos:</b> @Email, @Pattern para teléfono y código
 * postal</li>
 * <li><b>Longitudes:</b> @Size para limitar caracteres</li>
 * <li><b>Valores numéricos:</b> @Positive para duraciones</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Registro de nuevos usuarios en el sistema</li>
 * <li>Actualización de datos de usuarios existentes</li>
 * <li>Validación de formularios de registro</li>
 * <li>Documentación automática de API con Swagger</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
// Anotación de Lombok para generar automáticamente getters, setters, toString,
// hashCode y equals, evitando código repetitivo
@Data
@Tag(name = "UsuarioRequestDto", description = "DTO para representar la solicitud de registro de un usuario")
public class UsuarioRequestDto {

    /**
     * Nombre del usuario.
     * <p>
     * Campo obligatorio que representa el nombre de pila del usuario.
     * Se valida que no esté vacío y que tenga una longitud adecuada
     * para mantener la consistencia de los datos.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede estar vacío (@NotBlank)</li>
     * <li>Entre 2 y 50 caracteres (@Size)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar el nombre de un usuario")
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    /**
     * Apellidos del usuario.
     * <p>
     * Campo obligatorio que contiene los apellidos completos del usuario.
     * Permite identificar al usuario de forma más completa y se utiliza
     * en comunicaciones formales y búsquedas.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede estar vacío (@NotBlank)</li>
     * <li>Entre 2 y 150 caracteres (@Size)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar los apellidos de un usuario")
    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Size(min = 2, max = 150, message = "Los apellidos deben tener entre 2 y 150 caracteres")
    private String apellidos;

    /**
     * DNI (Documento Nacional de Identidad) del usuario.
     * <p>
     * Campo obligatorio que contiene el identificador fiscal único del usuario.
     * Es fundamental para la identificación legal y se utiliza en procesos
     * de verificación y cumplimiento normativo.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede estar vacío (@NotBlank)</li>
     * <li>Formato válido de 9-20 caracteres (@Size)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar el DNI de un usuario")
    @NotBlank(message = "El DNI no puede estar vacío")
    @Size(min = 9, max = 20, message = "El DNI debe tener un formato válido")
    private String dni;

    /**
     * Username del usuario.
     * <p>
     * Identificador único para el inicio de sesión del usuario.
     * Debe ser único en todo el sistema y se utiliza para autenticación
     * y como identificador público en el sistema.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede estar vacío (@NotBlank)</li>
     * <li>Entre 3 y 50 caracteres (@Size)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar el username de un usuario")
    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;

    /**
     * Correo electrónico del usuario.
     * <p>
     * Campo obligatorio para comunicación electrónica y notificaciones.
     * También se utiliza como alternativa para el inicio de sesión
     * y recuperación de contraseña.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede estar vacío (@NotBlank)</li>
     * <li>Formato de email válido (@Email)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar el correo de un usuario")
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe proporcionar un formato de correo válido (ejemplo@dominio.com)")
    private String correo;

    /**
     * Contraseña del usuario.
     * <p>
     * Campo obligatorio para la autenticación del usuario.
     * Se almacena de forma cifrada en la base de datos y debe cumplir
     * con requisitos mínimos de seguridad.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede estar vacía (@NotBlank)</li>
     * <li>Mínimo 8 caracteres (@Size)</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Nota:</b> Se recomienda incluir mayúsculas, minúsculas, números y símbolos
     * </p>
     */
    @Schema(description = "Para representar la contraseña de un usuario")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres para mayor seguridad")
    private String password;

    /**
     * Teléfono del usuario.
     * <p>
     * Campo opcional para contacto telefónico del usuario.
     * Se utiliza para comunicación directa y notificaciones importantes.
     * Acepta formatos internacionales y locales.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>Máximo 20 caracteres (@Size)</li>
     * <li>Formato telefónico válido (@Pattern)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar el telefono de un usuario")
    @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", message = "El formato del teléfono no es válido")
    private String telefono;

    /**
     * Dirección del usuario.
     * <p>
     * Campo obligatorio que contiene la dirección postal completa.
     * Se utiliza para envíos, facturación y verificación de identidad.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede estar vacía (@NotBlank)</li>
     * <li>Máximo 255 caracteres (@Size)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar la dirección de un usuario")
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección es demasiado larga")
    private String direccion;

    /**
     * Ciudad del usuario.
     * <p>
     * Campo obligatorio que indica la ciudad de residencia del usuario.
     * Se utiliza para segmentación geográfica y análisis demográfico.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede estar vacía (@NotBlank)</li>
     * <li>Máximo 100 caracteres (@Size)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar la ciudad de un usuario")
    @NotBlank(message = "La Ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad es demasiado larga")
    private String ciudad;

    /**
     * País del usuario.
     * <p>
     * Campo obligatorio que indica el país de residencia del usuario.
     * Es fundamental para cumplimiento fiscal y regulaciones locales.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede estar vacío (@NotBlank)</li>
     * <li>Máximo 100 caracteres (@Size)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar el pais de un usuario")
    @NotBlank(message = "El Pais es obligatorio")
    @Size(max = 100, message = "El país es demasiado largo")
    private String pais;

    /**
     * Código postal del usuario.
     * <p>
     * Campo obligatorio que contiene el código postal de la dirección.
     * Se utiliza para validación de direcciones y cálculos de envío.
     * Formato específico para códigos postales españoles.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede estar vacío (@NotBlank)</li>
     * <li>Formato de 5 dígitos (@Pattern)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar el codigo postal de un usuario")
    @NotBlank(message = "El codigo postal es obligatorio")
    @Pattern(regexp = "^[0-9]{5}$", message = "El código postal debe tener 5 dígitos (ejemplo: 28001)")
    private String codigoPostal;

    /**
     * Fecha de nacimiento del usuario.
     * <p>
     * Campo obligatorio que contiene la fecha exacta de nacimiento.
     * Se utiliza para verificar la mayoría de edad y cálculos
     * de edad en el sistema.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede ser nula (@NotNull)</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Formato:</b> ISO 8601 (yyyy-MM-dd)
     * </p>
     */
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Schema(description = "Para representar la fecha de nacimiento de un usuario")
    private LocalDate fechaNacimiento;

    /**
     * Género del usuario.
     * <p>
     * Campo obligatorio que indica el género del usuario.
     * Utiliza el enumerado EnumGenero para mantener consistencia
     * en los valores posibles en todo el sistema.
     * </p>
     * 
     * <p>
     * <b>Valores posibles:</b>
     * <ul>
     * <li>MASCULINO</li>
     * <li>FEMENINO</li>
     * <li>OTRO</li>
     * <li>NO_ESPECIFICADO</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>No puede ser nulo (@NotNull)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar el genero de un usuario")
    @NotNull(message = "El género es obligatorio")
    private EnumGenero genero;

    /**
     * Roles asignados al usuario.
     * <p>
     * Campo opcional que contiene los roles de usuario en el sistema.
     * Se utiliza para control de acceso y permisos.
     * Por defecto, los nuevos usuarios pueden tener un rol básico.
     * </p>
     * 
     * <p>
     * <b>Roles comunes:</b>
     * <ul>
     * <li>ROLE_USER: Usuario básico</li>
     * <li>ROLE_ADMIN: Administrador del sistema</li>
     * <li>ROLE_MODERATOR: Moderador de contenido</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>Campo opcional (puede ser null)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Para representar el rol de un usuario")
    private Set<String> rol;

}
