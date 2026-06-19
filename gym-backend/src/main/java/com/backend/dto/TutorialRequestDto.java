package com.backend.dto;

// Importaciones necesarias para validaciones y anotaciones
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para solicitudes de creación de tutoriales.
 * <p>
 * Esta clase representa la estructura de datos recibida del cliente
 * cuando se crea un nuevo tutorial en el sistema. Incluye todas
 * las validaciones necesarias para garantizar la integridad de los datos
 * antes de ser procesados por el backend.
 * </p>
 * 
 * <p>
 * <b>Información requerida:</b>
 * <ul>
 *   <li><b>Contenido del tutorial:</b> título, descripción técnica</li>
 *   <li><b>Media:</b> URL del video del tutorial</li>
 *   <li><b>Metadata:</b> duración en minutos</li>
 *   <li><b>Categorización:</b> ID de la categoría asociada</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Validaciones aplicadas:</b>
 * <ul>
 *   <li><b>Campos obligatorios:</b> @NotBlank para strings, @NotNull para objetos</li>
 *   <li><b>Formatos específicos:</b> @URL para enlaces de video</li>
 *   <li><b>Valores numéricos:</b> @Positive para duraciones positivas</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 *   <li>Creación de nuevos tutoriales</li>
 *   <li>Validación de formularios de creación</li>
 *   <li>Documentación automática de API con Swagger</li>
 *   <li>Transformación a entidades Tutorial</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Flujo típico:</b>
 * <ol>
 *   <li>Cliente envía TutorialRequestDto con datos del tutorial</li>
 *   <li>Backend valida los campos según las anotaciones</li>
 *   <li>Si es válido, se crea la entidad Tutorial</li>
 *   <li>Se devuelve TutorialResponseDto con los datos guardados</li>
 * </ol>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
// Anotación de Lombok para generar automáticamente getters, setters,
// toString, equals y hashCode, evitando código repetitivo
@Data
@Schema(description = "DTO para representar la solicitud de creación de un tutorial")
public class TutorialRequestDto {

    /**
     * Título del tutorial.
     * <p>
     * Campo obligatorio que representa el nombre descriptivo del tutorial.
     * Debe ser claro, conciso y representativo del contenido que se va
     * a enseñar. Se utiliza para búsquedas, SEO y como identificador principal.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     *   <li>No puede estar vacío (@NotBlank)</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Recomendaciones:</b>
     * <ul>
     *   <li>Ser específico y descriptivo</li>
     *   <li>Incluir tecnologías clave</li>
     *   <li>Mantener longitud razonable (50-100 caracteres)</li>
     * </ul>
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Título del tutorial")
    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    /**
     * Descripción técnica del tutorial.
     * <p>
     * Campo obligatorio que contiene los detalles técnicos del contenido.
     * Debe explicar qué se va a enseñar, los prerrequisitos necesarios,
     * tecnologías utilizadas y los objetivos de aprendizaje.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     *   <li>No puede estar vacía (@NotBlank)</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Contenido recomendado:</b>
     * <ul>
     *   <li>Objetivos de aprendizaje claros</li>
     *   <li>Prerrequisitos y nivel de dificultad</li>
     *   <li>Tecnologías y herramientas utilizadas</li>
     *   <li>Resultados esperados al completar</li>
     * </ul>
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Descripción técnica del tutorial")
    @NotBlank(message = "La descripción técnica es obligatoria")
    private String descripcion;

    /**
     * URL del video del tutorial.
     * <p>
     * Campo obligatorio que contiene el enlace directo al video
     * con el contenido visual del tutorial. Puede ser de plataformas
     * de streaming como YouTube, Vimeo, o servidores propios.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     *   <li>No puede estar vacía (@NotBlank)</li>
     *   <li>Debe ser una URL válida (@URL)</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Plataformas soportadas:</b>
     * <ul>
     *   <li>YouTube: https://youtube.com/watch?v=...</li>
     *   <li>Vimeo: https://vimeo.com/...</li>
     *   <li>Directo: https://dominio.com/ruta/video.mp4</li>
     *   <li>Otras plataformas de video streaming</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Nota:</b> La URL debe ser públicamente accesible y el video
     * debe estar disponible para visualización inmediata.
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "URL del video del tutorial")
    @NotBlank(message = "La URL del video es obligatoria")
    @URL(message = "Debe ser una URL válida (Youtube, Vimeo, etc.)")
    private String urlVideo;

    /**
     * Duración del tutorial en minutos.
     * <p>
     * Campo obligatorio que indica el tiempo total de duración
     * del video expresado en minutos. Esta información es crucial
     * para que los usuarios puedan planificar su tiempo de aprendizaje.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     *   <li>No puede ser nula (@NotNull)</li>
     *   <li>Debe ser mayor a 0 (@Positive)</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Usos:</b>
     * <ul>
     *   <li>Informar al usuario sobre la inversión de tiempo</li>
     *   <li>Calcular estadísticas de consumo</li>
     *   <li>Filtrar tutoriales por duración</li>
     *   <li>Planificar rutas de aprendizaje</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Formato:</b> Número entero positivo (ej: 45 = 45 minutos)
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Duración del tutorial en minutos")
    @NotNull(message = "La duración es obligatoria")
    @Positive(message = "La duración debe ser mayor a 0")
    private Integer duracionMin;

    /**
     * Identificador único de la categoría del tutorial.
     * <p>
     * Campo obligatorio que contiene el ID numérico de la categoría
     * a la que pertenece este tutorial. Este ID debe corresponder
     * a una categoría existente en la base de datos.
     * </p>
     * 
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     *   <li>No puede ser nulo (@NotNull)</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Relación:</b>
     * <ul>
     *   <li>Foreign key hacia la entidad Categoria</li>
     *   <li>Debe existir en la tabla de categorías</li>
     *   <li>Se utiliza para agrupar y filtrar tutoriales</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Proceso de validación:</b>
     * <ol>
     *   <li>Se verifica que el ID no sea nulo</li>
     *   <li>Se comprueba que exista la categoría</li>
     *   <li>Se establece la relación con el tutorial</li>
     * </ol>
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Identificador único de la categoría a la que pertenece el tutorial")
    @NotNull(message = "Debes asignar una categoría al tutorial")
    private Long categoriaId;

   @Schema(description = "Músculo principal que trabaja el ejercicio")
    @NotBlank(message = "El músculo objetivo es obligatorio")
    private String musculoObjetivo;

    @Schema(description = "Equipamiento necesario (Mancuernas, Barra, etc.)")
    @NotBlank(message = "El equipamiento es obligatorio")
    private String equipamiento;

    @Schema(description = "Si el tutorial es global (Biblioteca) o no")
    @NotNull(message = "Debe indicar si el tutorial es global o no")
    private Boolean esGlobal;
}