package com.backend.domain;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa la asignación de un entrenamiento a un usuario.
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
    private Integer orden = 1; 

    @Column(nullable = false)
    @Builder.Default
    private Boolean completado = false; 

    // Esta es la única lista que debe manejar los ejercicios y sus datos dinámicos
    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SerieRutina> detalleSeries = new java.util.ArrayList<>();

    // RELACIONES
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenamiento_id", nullable = false)
    @ToString.Exclude
    private Entrenamiento entrenamiento;

    @PrePersist
    protected void onCreate() {
        if (this.fechaAsignacion == null) {
            this.fechaAsignacion = LocalDate.now();
        }
    }
}