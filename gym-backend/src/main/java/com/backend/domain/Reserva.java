//Paquete
package com.backend.domain;

//Imports
import java.time.LocalDate;
import com.backend.domain.enums.EnumEstado;
import jakarta.persistence.*; //Importo todo el paquete
import lombok.*;// Importo todo el paquete

/**
 * Entidad que representa una reserva de clase del gimnasio.
 * <p>
 * Almacena información sobre reservas de usuarios,
 * incluyendo estado, fecha y relaciones con usuarios y horarios.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity // Indica que esta clase es una entidad JPA
@Table(name = "reservas") // Nombre de la tabla en la base de datos
@Data // Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor // Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor // Lombok para generar un constructor sin argumentos
@Builder // Lombok para generar un constructor con argumentos usando el patrón Builder
public class Reserva {
    /**
     * Identificador único de la reserva.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha en que se realiza la clase reservada.
     * <p>
     * Obligatorio, indica cuándo está programada la reserva.
     * </p>
     */
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    /**
     * Estado actual de la reserva.
     * <p>
     * Obligatorio, clasifica el estado (ej: CONFIRMADA, CANCELADA).
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 25)
    private EnumEstado estado;

    /**
     * Usuario que realiza la reserva.
     * <p>
     * Relación muchos-a-uno con carga lazy para optimizar
     * consultas de reservas por usuario.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @ToString.Exclude
    private Usuario usuario;

    /**
     * Horario específico de la clase reservada.
     * <p>
     * Relación muchos-a-uno con carga lazy para optimizar
     * consultas de reservas por horario.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    // Evita bucles en logs
    @EqualsAndHashCode.Exclude
    private Horario horario;

    /**
     * Método callback que se ejecuta antes de persistir la reserva.
     * <p>
     * Establece el estado por defecto a CONFIRMADA si no se especifica.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            // Asignar estado por defecto si no se especifica
            this.estado = EnumEstado.CONFIRMADA;
        }
    }

    @Column(name = "confirmado")
    private Boolean confirmado;
}
