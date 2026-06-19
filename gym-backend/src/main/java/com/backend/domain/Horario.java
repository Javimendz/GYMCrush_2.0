//Paquete
package com.backend.domain;

//Imports
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*; //Importo todo el paquete
import lombok.*;

/**
 * Entidad que representa un horario de clase del gimnasio.
 * <p>
 * Almacena información sobre programación de actividades,
 * incluyendo días, horas, aforo y sala asignada.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity // Indica que esta clase es una entidad JPA
@Table(name = "horarios") // Nombre de la tabla en la base de datos
@Data // Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor // Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor // Lombok para generar un constructor sin argumentos
@Builder // Lombok para generar un constructor con argumentos usando el patrón Builder
public class Horario {
    /**
     * Identificador único del horario.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Día de la semana en que se imparte la clase.
     * <p>
     * Obligatorio y con longitud máxima de 20 caracteres.
     * </p>
     */
    @Column(name = "dia_semana", nullable = false, length = 20)
    private String diaSemana;

    /**
     * Hora de inicio de la clase.
     * <p>
     * Obligatorio, especifica cuándo comienza la sesión.
     * </p>
     */
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    /**
     * Hora de finalización de la clase.
     * <p>
     * Obligatorio, especifica cuándo termina la sesión.
     * </p>
     */
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /**
     * Aforo máximo permitido para la clase.
     * <p>
     * Obligatorio, número máximo de participantes.
     * </p>
     */
    @Column(name = "aforo_max", nullable = false)
private Integer aforoMax;

    /**
     * Sala o ubicación específica donde se realiza la clase.
     * <p>
     * Obligatorio y con longitud máxima de 20 caracteres.
     * </p>
     */

    /**
     * Actividad que se imparte en este horario.
     * <p>
     * Relación muchos-a-uno con carga eager para obtener
     * información completa de la actividad.
     * </p>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "actividad_id", referencedColumnName = "id")
    private Actividad actividad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entrenador_id")
    private Usuario entrenador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sala_id")
    private Sala sala;

    

   @Builder.Default 
    @OneToMany(mappedBy = "horario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    public void addReserva(Reserva reserva) {
    if (this.reservas.size() >= this.aforoMax) {
        throw new IllegalStateException("Capacidad máxima alcanzada para esta clase");
    }
    this.reservas.add(reserva);
    reserva.setHorario(this);
}
}
