package com.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para solicitudes de tickets de soporte.
 * <p>
 * Esta clase representa la estructura de datos necesaria para que
 * un usuario cree un ticket de soporte o incidencia en el sistema.
 * Es la interfaz principal para el sistema de gestión de tickets
 * del gimnasio GYM Crush.
 * </p>
 * 
 * <p>
 * <b>Flujo de creación de ticket:</b>
 * <ol>
 * <li>Usuario describe su problema o consulta</li>
 * <li>Se envía este DTO con los datos del ticket</li>
 * <li>Sistema valida y crea el ticket</li>
 * <li>Se asigna un ID único y se notifica al equipo de soporte</li>
 * </ol>
 * </p>
 * 
 * <p>
 * <b>Validaciones aplicadas:</b>
 * <ul>
 * <li>Asunto: obligatorio, entre 5-100 caracteres</li>
 * <li>Mensaje: obligatorio, mínimo 10 caracteres, máximo 2000</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Reporte de incidencias técnicas</li>
 * <li>Consultas sobre facturación</li>
 * <li>Solicitudes de información general</li>
 * <li>Reporte de problemas con reservas</li>
 * <li>Sugerencias y mejoras para el sistema</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Categorías comunes de tickets:</b>
 * <ul>
 * <li><b>Soporte Técnico:</b> Problemas con la aplicación</li>
 * <li><b>Facturación:</b> Dudas sobre pagos y facturas</li>
 * <li><b>Reservas:</b> Incidencias con clases y horarios</li>
 * <li><b>Sugerencias:</b> Mejoras y nuevas funcionalidades</li>
 * <li><b>Información General:</b> Consultas sobre servicios</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera constructor sin parámetros
@AllArgsConstructor // Genera constructor con todos los parámetros
@Builder // Permite la construcción fluida de objetos
@Schema(description = "DTO para representar una solicitud de ticket")
public class TicketRequestDto {

    /**
     * Asunto o título del ticket.
     * <p>
     * Es obligatorio y debe tener entre 5 y 100 caracteres.
     * Sirve como resumen del problema o consulta para facilitar
     * la clasificación y asignación rápida del ticket.
     * </p>
     * 
     * <p>
     * <b>Buenas prácticas:</b>
     * <ul>
     * <li>Ser específico y conciso</li>
     * <li>Incluir palabras clave relevantes</li>
     * <li>Evitar títulos genéricos como "Ayuda" o "Problema"</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Ejemplos:</b>
     * <ul>
     * <li>"Error al reservar clase de spinning"</li>
     * <li>"No puedo acceder a mi perfil"</li>
     * <li>"Duda sobre facturación de diciembre"</li>
     * <li>"Sugerencia para mejorar el calendario"</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Asunto del ticket")
    @NotBlank(message = "El asunto no puede estar vacío")
    @Size(min = 5, max = 100, message = "El asunto debe tener entre 5 y 100 caracteres")
    private String asunto;

    /**
     * Mensaje detallado del ticket.
     * <p>
     * Es obligatorio y debe tener al menos 10 caracteres.
     * Contiene la descripción completa del problema, consulta
     * o sugerencia que el usuario desea reportar.
     * </p>
     * 
     * <p>
     * <b>Contenido recomendado:</b>
     * <ul>
     * <li>Descripción clara del problema</li>
     * <li>Pasos para reproducir el error (si aplica)</li>
     * <li>Mensajes de error exactos</li>
     * <li>Información de contexto (dispositivo, navegador, etc.)</li>
     * <li>Expectativas o resultado deseado</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Formato:</b> Texto plano, se permite HTML básico
     * <b>Longitud máxima:</b> 2000 caracteres para mantener legibilidad
     * </p>
     */
    @Schema(description = "Mensaje del ticket")
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 10, max = 2000, message = "El mensaje debe tener al menos 10 caracteres")
    private String mensaje;

}
