package com.backend.dto;

import java.time.LocalDate;
import java.util.List;
import com.backend.domain.enums.EnumGenero;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para respuestas de perfil de usuario completo.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se solicita información completa del perfil de un usuario.
 * Incluye información personal, de contacto, datos de salud,
 * y credenciales de acceso en un solo DTO consolidado.
 * </p>
 * 
 * <p>
 * <b>Secciones de información:</b>
 * <ul>
 * <li><b>Datos personales:</b> nombre, apellidos, DNI, fecha de nacimiento</li>
 * <li><b>Contacto:</b> teléfono, dirección completa, correo electrónico</li>
 * <li><b>Salud:</b> peso, estatura, nivel de actividad, IMC</li>
 * <li><b>Cuenta:</b> username, roles asignados</li>
 * <li><b>Social:</b> foto de perfil, biografía</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Visualización completa del perfil propio</li>
 * <li>Consulta de perfiles de otros usuarios (con permisos)</li>
 * <li>Exportación de datos del usuario</li>
 * <li>Actualización masiva de información</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@AllArgsConstructor // Genera constructor con todos los parámetros
@NoArgsConstructor // Genera constructor sin parámetros
@Schema(description = "DTO para representar la respuesta de un perfil de usuario")
@JsonInclude(JsonInclude.Include.NON_NULL) // No incluye campos null en JSON
public class PerfilResponseDto {

    /**
     * Identificador único del perfil en la base de datos.
     * <p>
     * Es generado automáticamente cuando se crea el usuario
     * y sirve como referencia única para todas las operaciones.
     * </p>
     */
    @Schema(description = "ID único del perfil", example = "123")
    private Long id;

    /**
     * Identificador único del usuario asociado.
     */
    @Schema(description = "ID único del usuario asociado", example = "456")
    private Long usuarioId;


    
    /**
     * Nombre del usuario.
     * <p>
     * Primer nombre del usuario, utilizado para identificación
     * personal y comunicaciones personalizadas.
     * </p>
     */
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre;

    /**
     * Apellidos del usuario.
     * <p>
     * Apellidos paterno y materno del usuario,
     * completan el nombre completo para fines administrativos.
     * </p>
     */
    @Schema(description = "Apellidos del usuario", example = "Pérez")
    private String apellidos;

    /**
     * Número de teléfono del usuario.
     * <p>
     * Teléfono de contacto principal para comunicaciones
     * importantes y verificación de identidad.
     * </p>
     */
    @Schema(description = "Teléfono del usuario", example = "600123456")
    private String telefono;

    /**
     * DNI (Documento Nacional de Identidad) del usuario.
     * <p>
     * Identificador oficial español utilizado para fines
     * legales y administrativos del gimnasio.
     * </p>
     */
    @Schema(description = "DNI del usuario", example = "12345678A")
    private String dni;

    /**
     * Fecha de nacimiento del usuario.
     * <p>
     * Utilizada para calcular la edad, verificar mayoría
     * de edad y personalizar servicios según edad.
     * </p>
     */
    @Schema(description = "Fecha de nacimiento del usuario", example = "2000-01-01")
    private LocalDate fechaNacimiento;

    /**
     * Género del usuario.
     * <p>
     * Define el género autodeclarado del usuario
     * según los valores del enum {@link EnumGenero}.
     * </p>
     */
    @Schema(description = "Género del usuario", example = "MASCULINO")
    private EnumGenero genero;

    /**
     * Fotografía de perfil del usuario.
     * <p>
     * Imagen codificada en base64 que representa
     * el avatar del usuario en la aplicación.
     * </p>
     */
    @Schema(description = "Foto del usuario", example = "base64encodedstring")
    private String foto;

    /**
     * Dirección postal completa del usuario.
     * <p>
     * Incluye calle, número y detalles adicionales
     * necesarios para correspondencia y ubicación.
     * </p>
     */
    @Schema(description = "Dirección del usuario", example = "Calle Falsa 123")
    private String direccion;

    /**
     * Ciudad de residencia del usuario.
     * <p>
     * Ciudad donde reside el usuario, utilizada
     * para segmentación geográfica y logística.
     * </p>
     */
    @Schema(description = "Ciudad del usuario", example = "Madrid")
    private String ciudad;

    /**
     * País de residencia del usuario.
     * <p>
     * País donde reside el usuario, importante
     * para cumplimiento normativo y fiscales.
     * </p>
     */
    @Schema(description = "País del usuario", example = "España")
    private String pais;

    /**
     * Código postal de la dirección del usuario.
     * <p>
     * Código postal que facilita la validación
     * de direcciones y servicios de mensajería.
     * </p>
     */
    @Schema(description = "Código postal del usuario", example = "28001")
    private String codigoPostal;

    /**
     * Biografía o descripción personal del usuario.
     * <p>
     * Texto libre donde el usuario puede describir
     * sus intereses, objetivos o información relevante.
     * </p>
     */
    @Schema(description = "Biografía del usuario", example = "Soy un apasionado por la salud")
    private String bio;

    // Datos de salud del usuario

    /**
     * Peso actual del usuario en kilogramos.
     * <p>
     * Peso corporal registrado en el sistema,
     * utilizado para cálculo de IMC y seguimiento.
     * </p>
     */
    @Schema(description = "Peso del usuario", example = "70.5")
    private Double peso;

    /**
     * Estatura del usuario en metros.
     * <p>
     * Altura corporal utilizada para cálculo
     * de IMC y evaluación de progreso físico.
     * </p>
     */
    @Schema(description = "Estatura del usuario", example = "1.75")
    private Double estatura;

    /**
     * Nivel de actividad física del usuario.
     * <p>
     * Clasificación del nivel de actividad habitual
     * del usuario para personalizar recomendaciones.
     * </p>
     * 
     * <p>
     * <b>Valores comunes:</b> "BAJA", "MEDIA", "ALTA"
     * </p>
     */
    @Schema(description = "Nivel de actividad del usuario", example = "ALTA")
    private String nivelActividad;

    /**
     * Índice de Masa Corporal (IMC) del usuario.
     * <p>
     * Valor calculado automáticamente basado en peso y estatura.
     * Utilizado para evaluación del estado nutricional.
     * </p>
     * 
     * <p>
     * <b>Clasificación:</b>
     * <ul>
     * <li>&lt;18.5: Bajo peso</li>
     * <li>18.5-24.9: Normal</li>
     * <li>25-29.9: Sobrepeso</li>
     * <li>≥30: Obesidad</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Índice de masa corporal del usuario", example = "22.5")
    private Double imc;

    // Campos de la cuenta de usuario

    /**
     * Nombre de usuario para inicio de sesión.
     * <p>
     * Identificador único del usuario en el sistema
     * utilizado para autenticación y acceso a la plataforma.
     * </p>
     */
    @Schema(description = "Nombre de usuario del usuario", example = "juanperez")
    private String username;

    /**
     * Correo electrónico del usuario.
     * <p>
     * Email principal para comunicaciones,
     * notificaciones y recuperación de cuenta.
     * </p>
     */
    @Schema(description = "Correo electrónico del usuario", example = "juanperez@example.com")
    private String correo;

    /**
     * Lista de roles asignados al usuario.
     * <p>
     * Permisos y niveles de acceso del usuario
     * en el sistema. Determina qué funcionalidades
     * puede utilizar el usuario.
     * </p>
     * 
     * <p>
     * <b>Roles comunes:</b>
     * <ul>
     * <li><b>ROLE_USER</b>: Usuario básico</li>
     * <li><b>ROLE_ADMIN</b>: Administrador del sistema</li>
     * <li><b>ROLE_TRAINER</b>: Entrenador</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Roles asignados al usuario", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private List<String> rol;
}
