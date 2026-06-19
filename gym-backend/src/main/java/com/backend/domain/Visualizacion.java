package com.backend.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa la visualización de un tutorial por parte de un usuario.
 * Actúa como tabla intermedia con atributos adicionales (progreso, estado).
 */
@Entity
@Table(name = "visualizaciones", 
       uniqueConstraints = {
            //Evita que un mismo usuario tenga varias filas para el mismo tutorial
           @UniqueConstraint(columnNames = {"usuario_id", "tutorial_id"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visualizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_visualizacion", nullable = false, updatable = false)
    private LocalDateTime fechaVisualizacion;

    // Fecha de la última vez que interactuó con el vídeo
    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completado = false;

    @Column(name = "progreso_segundos", nullable = false)
    @Builder.Default
    private Integer progresoSegundos = 0;

    // Propiedad extra útil para analíticas: ¿Cuántas veces ha entrado a este vídeo?
    @Column(name = "contador_reproducciones")
    @Builder.Default
    private Integer contadorReproducciones = 1;

    // Relación hacia el Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    // Relación hacia el Tutorial
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorial_id", nullable = false)
    @ToString.Exclude
    private Tutorial tutorial;

    /**
     * Lógica de auditoría automática
     */
    @PrePersist
    protected void onCreate() {
        if (this.fechaVisualizacion == null) {
            this.fechaVisualizacion = LocalDateTime.now();
        }
        if (this.ultimaActualizacion == null) {
            this.ultimaActualizacion = LocalDateTime.now();
        }
        if (this.contadorReproducciones == null) this.contadorReproducciones = 1;
        if (this.completado == null) this.completado = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.ultimaActualizacion = LocalDateTime.now();
    }

    /**
     * Método de conveniencia para calcular el porcentaje de progreso.
     * Ayuda mucho en Android para pintar las barras de progreso.
     */
    public double getPorcentajeCompletado() {
        if (tutorial == null || tutorial.getDuracionMin() <= 0) {
            return 0.0;
        }
        
        double duracionSegundos = tutorial.getDuracionMin() * 60.0;
        
        return Math.min(100.0, (progresoSegundos / duracionSegundos) * 100.0);
    }
}