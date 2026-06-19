package com.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para solicitudes de datos de salud.
 * <p>
 * Esta clase representa la estructura de datos necesaria para registrar
 * o actualizar información médica y física de un usuario en el sistema.
 * Incluye métricas corporales esenciales para el seguimiento
 * del progreso físico y cálculo de indicadores de salud.
 * </p>
 * 
 * <p>
 * <b>Métricas incluidas:</b>
 * <ul>
 * <li><b>Peso:</b> Masa corporal en kilogramos</li>
 * <li><b>Estatura:</b> Altura en metros</li>
 * <li><b>Nivel de actividad:</b> Clasificación del ejercicio regular</li>
 * <li><b>Comentarios:</b> Observaciones adicionales</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Validaciones aplicadas:</b>
 * <ul>
 * <li>Peso: obligatorio, debe ser positivo</li>
 * <li>Estatura: obligatoria, debe ser positiva</li>
 * <li>Nivel de actividad: opcional, máximo 50 caracteres</li>
 * <li>Comentario: opcional, sin límite específico</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Proceso automático:</b>
 * Al procesar este DTO, el sistema calculará automáticamente
 * el IMC (Índice de Masa Corporal) utilizando la fórmula:
 * <br>
 * <b>IMC = peso (kg) / estatura² (m)</b>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Registro inicial de datos de salud</li>
 * <li>Seguimiento periódico del progreso</li>
 * <li>Actualización de métricas corporales</li>
 * <li>Evaluación de estado físico</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Schema(description = "DTO para representar la solicitud de salud de un usuario")
public class SaludRequestDto {

    /**
     * Peso del usuario en kilogramos.
     * <p>
     * Es obligatorio y debe ser un valor positivo.
     * Se utiliza para el cálculo del IMC y seguimiento
     * del progreso de peso del usuario.
     * </p>
     * 
     * <p>
     * <b>Rango saludable típico:</b>
     * <ul>
     * <li>IMC 18.5-24.9: Peso normal</li>
     * <li>Varía según estatura y complexión</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Precisión recomendada:</b> 1 decimal (ej: 70.5)
     * </p>
     */
    @Schema(description = "Peso del usuario en kilogramos")
    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor a 0")
    private Double peso;

    /**
     * Estatura del usuario en metros.
     * <p>
     * Es obligatoria y debe ser un valor positivo.
     * Se utiliza para el cálculo del IMC y evaluación
     * del desarrollo físico y proporciones corporales.
     * </p>
     * 
     * <p>
     * <b>Formato estándar:</b> Decimal con 2 dígitos (ej: 1.75)
     * <b>Conversión:</b> 1.75m = 175cm
     * </p>
     * 
     * <p>
     * <b>Rango adulto típico:</b> 1.50 - 2.10 metros
     * </p>
     */
    @Schema(description = "Estatura del usuario en metros")
    @NotNull(message = "La estatura es obligatoria")
    @Positive(message = "La estatura debe ser mayor a 0")
    private Double estatura;

    /**
     * Nivel de actividad física habitual del usuario.
     * <p>
     * Es opcional y describe el nivel de ejercicio regular
     * que realiza el usuario. Se utiliza para personalizar
     * recomendaciones y planes de entrenamiento.
     * </p>
     * 
     * <p>
     * <b>Categorías comunes:</b>
     * <ul>
     * <li><b>SEDENTARIO</b>: Poco o ningún ejercicio regular</li>
     * <li><b>LIGERO</b>: Ejercicio ligero 1-3 días/semana</li>
     * <li><b>MODERADO</b>: Ejercicio moderado 3-5 días/semana</li>
     * <li><b>ACTIVO</b>: Ejercicio intenso 6-7 días/semana</li>
     * <li><b>ATLETA</b>: Entrenamiento profesional diario</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Importancia:</b> Fundamental para calcular
     * necesidades calóricas y diseñar programas personalizados.
     * </p>
     */
    @Schema(description = "Nivel de actividad del usuario")
    @Size(max = 50, message = "El nivel de actividad no puede superar 50 caracteres")
    private String nivelActividad;

    /**
     * Comentarios adicionales sobre la salud del usuario.
     * <p>
     * Es opcional y permite añadir observaciones,
     * restricciones médicas, objetivos personales o cualquier
     * información relevante para el entrenamiento y seguimiento.
     * </p>
     * 
     * <p>
     * <b>Ejemplos de contenido:</b>
     * <ul>
     * <li>"Restricción de rodilla, evitar ejercicios de impacto"</li>
     * <li>"Objetivo: perder 5kg en 3 meses"</li>
     * <li>"Hipertensión controlada, monitorizar frecuencia cardíaca"</li>
     * <li>"Prefiero entrenamientos matutinos"</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Uso en entrenamiento:</b> Los entrenadores pueden
     * utilizar esta información para adaptar los programas
     * y garantizar la seguridad del usuario.
     * </p>
     */
    @Schema(description = "Comentario adicional sobre la salud del usuario")
    private String comentario;
}