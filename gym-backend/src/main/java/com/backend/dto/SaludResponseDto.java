package com.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Data Transfer Object (DTO) para respuestas de datos de salud.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se solicita información sobre las mediciones de salud
 * de un usuario. Incluye todas las métricas registradas
 * más información de auditoría y contexto.
 * </p>
 * 
 * <p>
 * <b>Información incluida:</b>
 * <ul>
 * <li><b>Métricas corporales:</b> peso, estatura, IMC calculado</li>
 * <li><b>Nivel de actividad:</b> clasificación del ejercicio</li>
 * <li><b>Observaciones:</b> comentarios adicionales</li>
 * <li><b>Auditoría:</b> fecha de medición y usuario</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Características especiales:</b>
 * <ul>
 * <li>Uso de BigDecimal para precisión en peso y estatura</li>
 * <li>IMC calculado automáticamente por el sistema</li>
 * <li>Timestamp para seguimiento temporal</li>
 * <li>Username para identificación del responsable</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Historial de mediciones de salud</li>
 * <li>Seguimiento del progreso físico</li>
 * <li>Evaluación de tendencias de peso/IMC</li>
 * <li>Informes de salud para entrenadores</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Schema(description = "DTO para representar la respuesta de salud de un usuario")
public class SaludResponseDto {

    /**
     * Identificador único de la medición de salud.
     * <p>
     * Es generado automáticamente cuando se registra la medición
     * y sirve como referencia única para este registro específico.
     * </p>
     */
    @Schema(description = "Identificador único de la medición de salud")
    private Long id;

    /**
     * Peso del usuario en kilogramos.
     * <p>
     * Utiliza BigDecimal para mayor precisión decimal
     * en el registro del peso corporal. Evita problemas
     * de redondeo en cálculos sucesivos.
     * </p>
     * 
     * <p>
     * <b>Precisión recomendada:</b> 2 decimales (ej: 70.50)
     * </p>
     */
    @Schema(description = "Peso del usuario en kilogramos")
    private BigDecimal peso;

    /**
     * Estatura del usuario en metros.
     * <p>
     * Utiliza BigDecimal para mayor precisión en la medición
     * de la altura. Fundamental para cálculos precisos del IMC.
     * </p>
     * 
     * <p>
     * <b>Precisión recomendada:</b> 2 decimales (ej: 1.75)
     * </p>
     */
    @Schema(description = "Estatura del usuario en metros")
    private BigDecimal estatura;

    /**
     * Índice de Masa Corporal (IMC) calculado.
     * <p>
     * Valor calculado automáticamente por el sistema utilizando
     * la fórmula: peso (kg) / estatura² (m).
     * Se utiliza para evaluación del estado nutricional.
     * </p>
     * 
     * <p>
     * <b>Clasificación OMS:</b>
     * <ul>
     * <li>&lt;18.5: Bajo peso</li>
     * <li>18.5-24.9: Peso normal</li>
     * <li>25.0-29.9: Sobrepeso</li>
     * <li>30.0-34.9: Obesidad grado I</li>
     * <li>35.0-39.9: Obesidad grado II</li>
     * <li>≥40.0: Obesidad grado III</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Índice de Masa Corporal (IMC) del usuario")
    private Double imc;

    /**
     * Nivel de actividad física del usuario.
     * <p>
     * Clasificación del nivel de ejercicio regular que realiza
     * el usuario. Se utiliza para personalizar recomendaciones
     * y planes de entrenamiento.
     * </p>
     */
    @Schema(description = "Nivel de actividad del usuario")
    private String nivelActividad;

    /**
     * Comentarios adicionales sobre la medición de salud.
     * <p>
     * Observaciones, restricciones médicas, objetivos personales
     * o cualquier información relevante registrada en esta medición.
     * </p>
     */
    @Schema(description = "Comentario adicional sobre la salud del usuario")
    private String comentario;

    /**
     * Fecha y hora en que se realizó la medición de salud.
     * <p>
     * Timestamp automático que registra cuándo se capturaron
     * estos datos. Es fundamental para seguimiento temporal
     * y análisis de tendencias.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> yyyy-MM-dd HH:mm:ss
     * <b>Ejemplo:</b> 2024-12-25 14:30:00
     * </p>
     */
    @Schema(description = "Fecha y hora en que se realizó la medición de salud")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Formateo limpio para el JSON
    private LocalDateTime fechaMedicion;

    /**
     * Nombre de usuario del usuario que realizó la medición.
     * <p>
     * Identifica quién registró estos datos de salud.
     * Puede ser el propio usuario o un entrenador/administrador.
     * </p>
     * 
     * <p>
     * <b>Usos:</b>
     * <ul>
     * <li>Auditoría de cambios en datos de salud</li>
     * <li>Seguimiento de quién realiza las mediciones</li>
     * <li>Validación de permisos de acceso</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Nombre de usuario del usuario que realizó la medición de salud")
    private String username;
}
