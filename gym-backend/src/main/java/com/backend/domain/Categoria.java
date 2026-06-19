package com.backend.domain;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa la categoría deportiva.
 * <p>
 * Sirve para clasificar las actividades del gimnasio y facilitar
 * la búsqueda y filtrado en la aplicación móvil/web.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 */
@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre; // Ej: "Fuerza", "Cardio", "Agua"

    @Column(length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Actividad> actividades = new ArrayList<>();
}