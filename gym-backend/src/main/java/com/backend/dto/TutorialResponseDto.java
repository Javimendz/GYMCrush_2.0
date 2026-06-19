package com.backend.dto;

// Importaciones necesarias para anotaciones Lombok y Swagger
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para respuestas de tutoriales.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se solicita información sobre un tutorial existente.
 * Incluye información completa del tutorial más datos de la categoría
 * para facilitar el consumo del API.
 * </p>
 * 
 * <p>
 * <b>Información incluida:</b>
 * <ul>
 * <li><b>Datos del tutorial:</b> ID, título, descripción, URL del video</li>
 * <li><b>Metadata:</b> duración en minutos</li>
 * <li><b>Datos de categoría:</b> ID y nombre de la categoría asociada</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Consulta de tutoriales disponibles</li>
 * <li>Listado de tutoriales por categoría</li>
 * <li>Búsqueda y filtrado de contenido</li>
 * <li>Visualización de detalles de tutorial específico</li>
 * <li>Documentación automática de API con Swagger</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Notas:</b>
 * <ul>
 * <li>La URL del video debe ser válida y accesible</li>
 * <li>La duración se expresa en minutos para facilitar cálculos</li>
 * <li>Los datos de categoría permiten mostrar información contextual</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
// Anotaciones de Lombok para generar código repetitivo automáticamente
@Data // Genera getters, setters, toString, equals y hashCode
@Builder // Permite construcción fluida de objetos
@NoArgsConstructor // Constructor sin parámetros
@AllArgsConstructor // Constructor con todos los parámetros
@Schema(description = "DTO para representar la respuesta de un tutorial")
public class TutorialResponseDto {
    /**
     * Identificador único del tutorial.
     * <p>
     * ID numérico generado automáticamente en la base de datos.
     * Sirve como referencia única para todas las operaciones
     * relacionadas con este tutorial específico.
     * </p>
     * 
     * <p>
     * <b>Usos:</b>
     * <ul>
     * <li>Consultas específicas del tutorial</li>
     * <li>Operaciones de actualización y eliminación</li>
     * <li>Referencias en relaciones con otras entidades</li>
     * </ul>
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Identificador único del tutorial")
    private Long id;

    /**
     * Título del tutorial.
     * <p>
     * Nombre descriptivo del tutorial que resume brevemente
     * el contenido que se va a enseñar. Se utiliza en listados,
     * búsquedas y como encabezado principal en la vista detallada.
     * </p>
     * 
     * <p>
     * <b>Características:</b>
     * <ul>
     * <li>Debe ser claro y conciso</li>
     * <li>Se utiliza para SEO y búsquedas</li>
     * <li>Aparece en vistas previas y listados</li>
     * </ul>
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Título del tutorial")
    private String titulo;

    /**
     * Descripción del tutorial.
     * <p>
     * Texto detallado que explica el contenido, objetivos
     * y requisitos del tutorial. Ayuda al usuario a entender
     * qué aprenderá y si el tutorial es adecuado para sus necesidades.
     * </p>
     * 
     * <p>
     * <b>Contenido típico:</b>
     * <ul>
     * <li>Objetivos de aprendizaje</li>
     * <li>Prerrequisitos necesarios</li>
     * <li>Tecnologías o herramientas utilizadas</li>
     * <li>Nivel de dificultad</li>
     * </ul>
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Descripción del tutorial")
    private String descripcion;

    /**
     * URL del video del tutorial.
     * <p>
     * Enlace directo al video que contiene el contenido
     * visual del tutorial. Puede ser de plataformas como YouTube,
     * Vimeo, o servidores de video personalizados.
     * </p>
     * 
     * <p>
     * <b>Formatos soportados:</b>
     * <ul>
     * <li>YouTube: https://youtube.com/watch?v=...</li>
     * <li>Vimeo: https://vimeo.com/...</li>
     * <li>Directo: https://dominio.com/video.mp4</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Nota:</b> La URL debe ser válida y accesible públicamente
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "URL del video del tutorial")
    private String urlVideo;

    /**
     * Duración del tutorial en minutos.
     * <p>
     * Tiempo total de duración del video expresado en minutos.
     * Se utiliza para informar al usuario sobre la inversión
     * de tiempo requerida y para cálculos de estadísticas.
     * </p>
     * 
     * <p>
     * <b>Usos:</b>
     * <ul>
     * <li>Mostrar duración en listados</li>
     * <li>Calcular tiempo total de cursos</li>
     * <li>Filtrar por duración preferida</li>
     * <li>Estadísticas de consumo de contenido</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Formato:</b> Número entero positivo (ej: 45 = 45 minutos)
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Duración del tutorial en minutos")
    private Integer duracionMin;

    /**
     * Identificador único de la categoría del tutorial.
     * <p>
     * ID numérico de la categoría a la que pertenece este tutorial.
     * Permite agrupar tutoriales por temas y facilitar la navegación
     * y búsqueda de contenido relacionado.
     * </p>
     * 
     * <p>
     * <b>Usos:</b>
     * <ul>
     * <li>Filtrar tutoriales por categoría</li>
     * <li>Mostrar tutoriales relacionados</li>
     * <li>Navegación por categorías</li>
     * <li>Estadísticas por tema</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Relación:</b> Foreign key hacia la entidad Categoria
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Identificador único de la categoría del tutorial")
    private Long categoriaId;

    /**
     * Nombre de la categoría del tutorial.
     * <p>
     * Nombre descriptivo de la categoría a la que pertenece
     * el tutorial. Se incluye para evitar consultas adicionales
     * y facilitar la visualización en interfaces de usuario.
     * </p>
     * 
     * <p>
     * <b>Ejemplos de categorías:</b>
     * <ul>
     * <li>Programación Web</li>
     * <li>Bases de Datos</li>
     * <li>Desarrollo Móvil</li>
     * <li>Inteligencia Artificial</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Beneficios:</b>
     * <ul>
     * <li>Mejora la experiencia de usuario</li>
     * <li>Reduce consultas adicionales al servidor</li>
     * <li>Facilita el filtrado y búsqueda</li>
     * </ul>
     * </p>
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Nombre de la categoría del tutorial")
    private String nombreCategoria;

    private String musculoObjetivo;
    private String equipamiento;
    private Boolean esGlobal;
    private Long usuarioId;
}
