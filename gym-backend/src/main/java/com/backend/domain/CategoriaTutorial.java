package com.backend.domain;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

/**
 * Entidad que representa una categoría de tutoriales educativos.
 * <p>
 * Organiza los tutoriales del gimnasio en categorías temáticas
 * para facilitar la navegación y búsqueda de contenido.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "categoria_tutorial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaTutorial {
    /**
     * Identificador único de la categoría.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nombre de la categoría.
     * <p>
     * Obligatorio y único, identifica la categoría temática.
     * </p>
     */
    @Column(nullable = false, unique = true)
    private String nombre;
    
    /**
     * Descripción detallada de la categoría.
     * <p>
     * Opcional, proporciona contexto sobre el tipo de tutoriales.
     * </p>
     */
    private String descripcion;

    /**
     * Lista de tutoriales asociados a esta categoría.
     * <p>
     * Relación uno-a-muchos con cascada completa para
     * gestionar el ciclo de vida de los tutoriales.
     * </p>
     */
    @OneToMany(mappedBy = "categoria", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Tutorial> tutoriales;
}
