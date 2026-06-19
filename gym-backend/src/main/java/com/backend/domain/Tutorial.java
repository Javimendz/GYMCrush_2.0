package com.backend.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un tutorial educativo del gimnasio.
 * <p>
 * Almacena información sobre videos instructivos,
 * incluyendo título, descripción y categoría temática.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "tutoriales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tutorial {
    /**
     * Identificador único del tutorial.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título del tutorial.
     * <p>
     * Obligatorio, describe brevemente el contenido.
     * </p>
     */
    @Column(nullable = false)
    private String titulo;

    /**
     * Descripción detallada del tutorial.
     * <p>
     * Opcional, permite texto largo para explicar el contenido.
     * </p>
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "musculo_objetivo")
    private String musculoObjetivo;

    @Column(name = "equipamiento")
    private String equipamiento;

    /**
     * URL del video del tutorial.
     * <p>
     * Opcional, enlace al video instructivo.
     * </p>
     */
    private String urlVideo;

    /**
     * Duración del tutorial en minutos.
     * <p>
     * Opcional, indica el tiempo total del video.
     * </p>
     */
    private Integer duracionMin;

    /**
     * Categoría temática a la que pertenece el tutorial.
     * <p>
     * Relación muchos-a-uno con carga eager para obtener
     * información completa de la categoría.
     * </p>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private CategoriaTutorial categoria;

    @OneToMany(mappedBy = "tutorial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude // evitar bs infinitos
    private List<Visualizacion> visualizaciones = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenamiento_id") 
    private Entrenamiento entrenamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true) // null = Ejercicio Global (Biblioteca)
    private Usuario usuario;

    @Column(name = "es_global", nullable = false)
    @Builder.Default
private Boolean esGlobal = false;}
