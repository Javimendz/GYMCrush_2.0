package com.backend.dto;

import java.time.LocalDate;
import com.backend.domain.enums.EnumGenero;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para solicitudes de perfil de usuario.
 * <p>
 * Esta clase representa la estructura de datos necesaria para crear
 * o actualizar el perfil completo de un usuario en el sistema GYM Crush.
 * Incluye información personal, de contacto y preferencias básicas.
 * </p>
 * 
 * <p>
 * <b>Validaciones aplicadas:</b>
 * <ul>
 * <li>Nombre: obligatorio, entre 2-80 caracteres</li>
 * <li>Apellidos: obligatorios, entre 2-150 caracteres</li>
 * <li>Teléfono: obligatorio, formato internacional válido</li>
 * <li>DNI/NIE: obligatorio, formato español válido</li>
 * <li>Fecha de nacimiento: obligatoria, debe ser pasada</li>
 * <li>Dirección completa: obligatoria</li>
 * <li>Género: obligatorio, del enum EnumGenero</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Campos opcionales:</b>
 * <ul>
 * <li>Biografía: descripción personal del usuario</li>
 * <li>Foto: imagen de perfil en base64</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@JsonInclude(JsonInclude.Include.NON_NULL) // No incluye campos null en JSON
@Schema(description = "DTO para la solicitud de perfil")
public class PerfilRequestDto {

    /**
     * Nombre del usuario.
     * <p>
     * Es obligatorio y debe tener entre 2 y 80 caracteres.
     * Se utiliza para identificar al usuario en el sistema
     * y en comunicaciones personalizadas.
     * </p>
     */
    @Schema(description = "Nombre del usuario", example = "Juan")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    private String nombre;

    /**
     * Apellidos del usuario.
     * <p>
     * Son obligatorios y deben tener entre 2 y 150 caracteres.
     * Completan el nombre completo del usuario para fines
     * administrativos y de comunicación.
     * </p>
     */
    @Schema(description = "Apellidos del usuario", example = "Pérez García")
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 150, message = "Los apellidos deben tener entre 2 y 150 caracteres")
    private String apellidos;

    /**
     * Número de teléfono del usuario.
     * <p>
     * Es obligatorio y debe seguir un formato de teléfono
     * internacional válido. Se utiliza para comunicaciones
     * importantes y verificación de cuenta.
     * </p>
     * 
     * <p>
     * <b>Formato aceptado:</b> +34612345678, 0034612345678, 612345678
     * </p>
     */
    @Schema(description = "Teléfono del usuario", example = "+34612345678")
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", message = "Formato de teléfono no válido")
    private String telefono;

    /**
     * DNI o NIE del usuario.
     * <p>
     * Es obligatorio y debe tener un formato válido español.
     * Se utiliza para identificación legal y fines
     * administrativos del gimnasio.
     * </p>
     * 
     * <p>
     * <b>Formatos aceptados:</b>
     * <ul>
     * <li><b>DNI:</b> 12345678A (8 números + 1 letra)</li>
     * <li><b>NIE:</b> X1234567A, Y1234567Z (letra + 7 números + letra)</li>
     * </ul>
     * </p>
     */
    @Schema(description = "DNI o NIE del usuario", example = "12345678A")
    @NotBlank(message = "El DNI/NIE es obligatorio")
    @Size(min = 9, max = 20, message = "El DNI debe tener un formato válido")
    private String dni;

    /**
     * Fecha de nacimiento del usuario.
     * <p>
     * Es obligatoria y debe ser una fecha pasada.
     * Se utiliza para verificar la mayoría de edad
     * y para personalización de servicios.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> ISO 8601 (yyyy-MM-dd)
     * <b>Ejemplo:</b> 1990-01-01
     * </p>
     */
    @Schema(description = "Fecha de nacimiento del usuario", example = "1990-01-01")
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    /**
     * Dirección postal del usuario.
     * <p>
     * Es obligatoria y debe incluir calle, número y detalles
     * adicionales si es necesario. Se utiliza para correspondencia
     * y ubicación de emergencia.
     * </p>
     * 
     * <p>
     * <b>Ejemplo:</b> "Calle Falsa 123, 2ºA"
     * </p>
     */
    @Schema(description = "Dirección del usuario", example = "Calle Falsa 123")
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    /**
     * Ciudad de residencia del usuario.
     * <p>
     * Es obligatoria y se utiliza junto con la dirección
     * para completar la información postal y para
     * segmentación geográfica de servicios.
     * </p>
     */
    @Schema(description = "Ciudad del usuario", example = "Madrid")
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    /**
     * País de residencia del usuario.
     * <p>
     * Es obligatorio y se utiliza para fines administrativos,
     * fiscales y de cumplimiento normativo.
     * </p>
     */
    @Schema(description = "País del usuario", example = "España")
    @NotBlank(message = "El país es obligatorio")
    private String pais;

    /**
     * Código postal de la dirección del usuario.
     * <p>
     * Es obligatorio y facilita la entrega de correspondencia
     * y la validación de direcciones.
     * </p>
     * 
     * <p>
     * <b>Formato español:</b> 5 dígitos (ej: 28001)
     * </p>
     */
    @Schema(description = "Código postal del usuario", example = "28001")
    @NotBlank(message = "El código postal es obligatorio")
    private String codigoPostal;

    /**
     * Género del usuario.
     * <p>
     * Es obligatorio y debe ser uno de los valores definidos
     * en el enum {@link EnumGenero}. Se utiliza para
     * personalización de servicios y estadísticas.
     * </p>
     * 
     * <p>
     * <b>Valores posibles:</b> MASCULINO, FEMENINO, OTRO, NO_ESPECIFICADO
     * </p>
     */
    @Schema(description = "Género del usuario", example = "MASCULINO")
    @NotNull(message = "El género es obligatorio")
    private EnumGenero genero;

    /**
     * Biografía o descripción personal del usuario.
     * <p>
     * Es opcional y permite al usuario compartir información
     * sobre sus intereses, objetivos fitness o cualquier
     * información relevante para su perfil.
     * </p>
     * 
     * <p>
     * <b>Uso:</b> Se muestra en el perfil público del usuario
     * y ayuda a otros miembros a conocerle mejor.
     * </p>
     */
    @Schema(description = "Biografía del usuario", example = "Soy un desarrollador backend")
    private String bio;

    /**
     * Fotografía de perfil del usuario.
     * <p>
     * Es opcional y debe estar codificada en base64.
     * Se utiliza para personalizar el perfil y mejorar
     * la experiencia social en la aplicación.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> String en base64 que representa la imagen.
     * <b>Tamaño recomendado:</b> Hasta 5MB para rendimiento óptimo.
     * </p>
     */
    @Schema(description = "Foto del usuario", example = "base64encodedstring")
    private String foto; // Opcional
}
