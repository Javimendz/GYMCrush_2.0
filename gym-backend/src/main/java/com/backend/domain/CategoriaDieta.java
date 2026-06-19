package com.backend.domain;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa una categoría o familia de planes dietéticos.
 * <p>
 * Sirve para agrupar las dietas según su objetivo principal
 * (ej: Volumen, Definición, Mantenimiento, Keto, Vegana).
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 */
@Entity
@Table(name = "categoria_dieta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDieta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre; 

    @Column(length = 255)
    private String descripcion;

    // Una categoría tiene muchas dietas
    @OneToMany(mappedBy = "categoriaDieta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Dieta> dietas = new ArrayList<>();
}