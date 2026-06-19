//Paquete
package com.backend.domain;

import java.util.List;

import jakarta.persistence.*;//Importar todo el paquete
import lombok.*;

/**
 * Entidad que representa una sesión de entrenamiento personalizado.
 * <p>
 * Almacena información sobre rutinas de ejercicio,
 * incluyendo intensidad, duración y fechas programadas.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "entrenamientos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Entrenamiento {

    /**
     * Identificador único de la sesión de entrenamiento.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha programada para el entrenamiento.
     * <p>
     * Opcional, indica cuándo se realizará la sesión.
     * </p>
     */

    @Column(nullable = false, length = 100)
    private String nombre; // Ej: "Pecho y Tríceps"

    @Column(nullable = true)
    private String urlImagen;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Descripción detallada de la sesión de entrenamiento.
     * <p>
     * Opcional, puede incluir información sobre el enfoque
     * o los objetivos específicos de esta sesión.
     * </p>
     */
    private Integer cantidadEjercicios;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaTutorial categoria;
    /**
     * Duración total de la sesión de entrenamiento.
     * <p>
     * Opcional, representa el tiempo total en minutos.
     * </p>
     */
    @Column(name = "duracion", nullable = true)
    private Integer duracion;

    /**
     * Nivel de intensidad del entrenamiento.
     * <p>
     * Obligatorio, clasifica la dificultad (ej: baja, media, alta).
     * </p>
     */
    @Column(name = "intensidad", length = 150)
    private String intensidad;

    @OneToMany(mappedBy = "entrenamiento", cascade = CascadeType.ALL)
    private List<Tutorial> tutoriales;

    @OneToMany(mappedBy = "entrenamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rutina> rutinas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true) // nullable = true es clave aquí
    private Usuario usuario;

    @Column(name = "es_global")
    private boolean esGlobal;
}
