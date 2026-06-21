package com.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "serie_rutinas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SerieRutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numeroSerie; // Serie 1, Serie 2, Serie 3...

    @Column(nullable = false)
    private Double peso; // Los Kg introducidos

    @Column(nullable = false)
    private Integer repeticiones; // Las REPS introducidas

    @Column(nullable = false)
    @Builder.Default
    private Boolean completada = false; // Checkbox individual por cada serie

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rutina_id", nullable = false)
    @ToString.Exclude
    private Rutina rutina;
}