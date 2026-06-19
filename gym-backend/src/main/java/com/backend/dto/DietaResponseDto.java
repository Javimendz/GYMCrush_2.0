package com.backend.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para respuestas de dietas.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se solicita información sobre una dieta existente.
 * Incluye información nutricional completa como macronutrientes,
 * calorías totales y categorización de la dieta.
 * </p>
 * 
 * <p>
 * <b>Información nutricional incluida:</b>
 * <ul>
 * <li>Proteínas: fundamentales para el desarrollo muscular</li>
 * <li>Carbohidratos: fuente principal de energía</li>
 * <li>Grasas: esenciales para funciones hormonales y vitamínicas</li>
 * <li>Calorías totales: balance energético completo</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos típicos:</b>
 * <ul>
 * <li>Planes nutricionales personalizados</li>
 * <li>Seguimiento de objetivos fitness</li>
 * <li>Recomendaciones dietéticas</li>
 * <li>Integración con planes de entrenamiento</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Schema(description = "DTO para la respuesta de dieta")
public class DietaResponseDto {

    /**
     * Identificador único de la dieta en la base de datos.
     * <p>
     * Es generado automáticamente cuando se crea la dieta
     * y sirve como referencia única para todas las operaciones.
     * </p>
     */
    @Schema(description = "ID único de la dieta", example = "1")
    private Long id;

    /**
     * Nombre descriptivo de la dieta.
     * <p>
     * Identifica el propósito o característica principal de la dieta.
     * Ejemplos: "Dieta para perder peso", "Dieta de volumen", "Dieta de
     * mantenimiento".
     * </p>
     */
    @Schema(description = "Nombre de la dieta", example = "Dieta para perder peso")
    private String nombre;

    /**
     * Tipo o frecuencia de la dieta.
     * <p>
     * Define la temporalidad o periodicidad del plan nutricional.
     * Ejemplos: "Diaria", "Semanal", "Mensual", "Personalizada".
     * </p>
     */
    @Schema(description = "Tipo de dieta (diaria, semanal, etc.)", example = "Diaria")
    private String tipo;

    /**
     * Cantidad de proteínas en la dieta (en gramos).
     * <p>
     * Las proteínas son esenciales para la reparación y construcción
     * de tejidos, especialmente importante para usuarios que realizan
     * entrenamiento de fuerza.
     * </p>
     * 
     * <p>
     * <b>Referencia:</b> Generalmente 1.6-2.2g por kg de peso corporal
     * para personas activas.
     * </p>
     */
    @Schema(description = "Cantidad de proteínas en la dieta", example = "50")
    private Integer cantidadProteinas;

    /**
     * Cantidad de carbohidratos en la dieta (en gramos).
     * <p>
     * Principal fuente de energía para el cuerpo y el cerebro.
     * Fundamental para el rendimiento deportivo y la recuperación.
     * </p>
     * 
     * <p>
     * <b>Referencia:</b> Generalmente 3-5g por kg de peso corporal
     * para personas activas, ajustable según objetivos.
     * </p>
     */
    @Schema(description = "Cantidad de carbohidratos en la dieta", example = "200")
    private Integer cantidadCarbohidratos;

    /**
     * Cantidad de grasas en la dieta (en gramos).
     * <p>
     * Las grasas saludables son esenciales para la producción
     * de hormonas, absorción de vitaminas liposolubles y salud general.
     * </p>
     * 
     * <p>
     * <b>Referencia:</b> Generalmente 0.8-1.2g por kg de peso corporal,
     * enfocándose en grasas insaturadas.
     * </p>
     */
    @Schema(description = "Cantidad de grasas en la dieta", example = "50")
    private Integer cantidadGrasas;

    /**
     * Cantidad total de calorías en la dieta.
     * <p>
     * Representa el balance energético completo de la dieta,
     * calculado como: (proteínas × 4) + (carbohidratos × 4) + (grasas × 9).
     * Es fundamental para alcanzar objetivos de peso o rendimiento.
     * </p>
     * 
     * <p>
     * <b>Referencias comunes:</b>
     * <ul>
     * <li><b>Pérdida de peso:</b> 1500-2000 kcal/día</li>
     * <li><b>Mantenimiento:</b> 2000-2500 kcal/día</li>
     * <li><b>Volumen:</b> 2500-4000+ kcal/día</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Calorías totales objetivo", example = "2000")
    private Integer objetivoCalorico;

    // Datos para la categoria
    @Schema(description = "ID de la categoría")
    private Long categoriaDietaId;

    @Schema(description = "Nombre de la categoría")
    private String nombreCategoria;

}
