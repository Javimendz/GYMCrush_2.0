package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) para solicitudes de creación de categorías de
 * tutoriales.
 * <p>
 * Esta clase representa la estructura de datos necesaria para crear una nueva
 * categoría que agrupará tutoriales relacionados en el sistema GYM Crush.
 * Las categorías ayudan a organizar y clasificar los tutoriales por temas
 * como "Deportes", "Nutrición", "Ejercicios", etc.
 * </p>
 * 
 * <p>
 * <b>Validaciones aplicadas:</b>
 * <ul>
 * <li>Nombre: obligatorio, máximo 50 caracteres</li>
 * <li>Descripción: opcional, máximo 255 caracteres</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Schema(description = "DTO para la solicitud de creación de una categoría de tutorial")
public class CategoriaTutorialRequestDto {

    /**
     * Nombre de la categoría de tutorial.
     * <p>
     * Es obligatorio y no puede estar vacío. Sirve como identificador
     * principal de la categoría y se mostrará en la interfaz de usuario.
     * Debe ser descriptivo y conciso (máximo 50 caracteres).
     * </p>
     * 
     * <p>
     * <b>Ejemplos:</b> "Deportes", "Nutrición", "Ejercicios", "Yoga", "Cardio"
     * </p>
     */
    @Schema(description = "Nombre de la categoría de tutorial", example = "Deportes")
    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 50, message = "El nombre es demasiado largo")
    private String nombre;

    /**
     * Descripción detallada de la categoría de tutorial.
     * <p>
     * Es opcional y puede tener hasta 255 caracteres.
     * Proporciona información adicional sobre el tipo de tutoriales
     * que se incluyen en esta categoría, helping a los usuarios
     * a entender su contenido.
     * </p>
     * 
     * <p>
     * <b>Ejemplo:</b> "Categoría que contiene tutoriales sobre deportes,
     * técnicas y entrenamientos específicos para diferentes disciplinas."
     * </p>
     */
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    @Schema(description = "Descripción detallada de la categoría de tutorial", example = "Categoría que contiene tutoriales sobre deportes")
    private String descripcion;
}