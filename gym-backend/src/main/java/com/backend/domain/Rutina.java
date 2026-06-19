package com.backend.domain;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa la asignación de un entrenamiento a un usuario.
 * <p>
 * Actúa como tabla intermedia con datos extra entre Usuario y Entrenamiento,
 * definiendo qué día toca, el orden de ejecución y si se ha completado.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 */
@Entity
@Table(name = "rutinas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(nullable = false)
    @Builder.Default
    private Integer orden = 1; // Para saber qué ejercicio va primero, segundo...

    @Column(nullable = false)
    @Builder.Default
    private Boolean completado = false; // El usuario lo marca con un check en la app

    @Column(nullable = false)
    @Builder.Default
    private Integer series = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer repeticiones = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double peso = 0.0;

    // Relación hacia el Usuario (De quién es la rutina)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    // Relación hacia el Entrenamiento (Qué entrenamiento es)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenamiento_id", nullable = false)
    @ToString.Exclude
    private Entrenamiento entrenamiento;

    /**
     * Se ejecuta automáticamente antes de guardar por primera vez.
     */
    @PrePersist
    protected void onCreate() {
        if (this.fechaAsignacion == null) {
            this.fechaAsignacion = LocalDate.now();
        }
    }
}