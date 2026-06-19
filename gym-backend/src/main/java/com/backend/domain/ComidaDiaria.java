package com.backend.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comidas_diarias")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComidaDiaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String momento; // "Desayuno", "Almuerzo", "Cena"
    private String nombreAlimento; // Lo que viene de FatSecret

    private Double calorias;
    private Double proteina; // Lo que viene de FatSecret
    private Double carbohidratos; // Lo que viene de FatSecret
    private Double grasas; // Lo que viene de FatSecret
    private String imagenUrl; // URL de la imagen del alimento, si quieres mostrarla en el frontend
    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fecha;
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Nutricion plan; // Para saber a qué plan pertenece este plato
}