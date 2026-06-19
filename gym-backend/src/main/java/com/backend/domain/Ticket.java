//Paquete
package com.backend.domain;

//Imports
import java.time.LocalDateTime;
import com.backend.domain.enums.EnumEstadoTicket;
import jakarta.persistence.*; //Importar todos los paquetes
import lombok.*; //Importar todos los paquetes de Lombok

/**
 * Entidad que representa un ticket de soporte técnico.
 * <p>
 * Almacena información sobre incidencias o dudas de usuarios,
 * incluyendo estado, fechas de creación y resolución.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity // Indica que esta clase es una entidad JPA
@Table(name = "tickets") // Nombre de la tabla en la base de datos
@Data // Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor // Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor // Lombok para generar un constructor sin argumentos
@Builder // Lombok para generar un constructor con argumentos usando el patrón Builder
public class Ticket {

    /**
     * Identificador único del ticket.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Asunto o título del ticket.
     * <p>
     * Opcional, describe brevemente el problema o consulta.
     * </p>
     */
    @Column(name = "asunto", nullable = true, length = 150)
    private String asunto;

    /**
     * Mensaje detallado del ticket.
     * <p>
     * Obligatorio, contiene la descripción completa del problema.
     * </p>
     */
    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    /**
     * Estado actual del ticket.
     * <p>
     * Obligatorio, clasifica el estado del seguimiento.
     * </p>
     */
    @Column(name = "estado", length = 50, nullable = false)
    @Enumerated(EnumType.STRING) // Indicamos que es un enum de tipo string
    private EnumEstadoTicket estado;

    /**
     * Fecha y hora de resolución del ticket.
     * <p>
     * Opcional, registra cuándo se resolvió la incidencia.
     * </p>
     */
    @Column(name = "fecha_resolucion", nullable = true)
    private LocalDateTime fechaResolucion;

    /**
     * Fecha y hora de creación del ticket.
     * <p>
     * Obligatorio, no modificable después de creación.
     * </p>
     */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Usuario que crea el ticket.
     * <p>
     * Relación muchos-a-uno con carga eager para obtener
     * información del usuario automáticamente.
     * </p>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id")
    @ToString.Exclude
    private Usuario usuario;

    /**
     * Método callback que se ejecuta antes de persistir el ticket.
     * <p>
     * Establece la fecha de creación actual y el estado por defecto.
     * </p>
     */
    @PrePersist // Automatizacion de fecha con la fecha actual
    public void onCreate() {
        // Automatizar la fecha de creación
        this.fechaCreacion = LocalDateTime.now();

        // Establecer estado por defecto si no se especifica
        if (this.estado == null) {
            this.estado = EnumEstadoTicket.ABIERTO; // Estado por defecto
        }
    }
}
