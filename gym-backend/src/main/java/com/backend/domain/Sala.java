package com.backend.domain;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un espacio físico dentro del gimnasio.
 * <p>
 * Almacena información sobre las salas, su capacidad máxima
 * y el equipamiento disponible para controlar el aforo.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 */
@Entity
@Table(name = "salas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre; // Ej: "Sala Zen", "Pista Ciclo Indoor"

    @Column(name = "capacidad_max", nullable = false)
    private Integer capacidadMax; // El aforo límite físico de la sala

    @Column(length = 255)
    private String descripcion;

    @Column(length = 100)
    private String ubicacion; // Ej: "Planta Baja", "Edificio Norte"

    @Column(columnDefinition = "TEXT")
    private String equipamiento; // Ej: "20 Bicicletas, proyector, sonido"

    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true; // Para deshabilitar la sala si está en obras

    // Una sala tiene asignados muchos horarios de clases a lo largo de la semana
    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Horario> horarios = new ArrayList<>();

    
}