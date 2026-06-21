package com.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalles_plan")
@Data
@Builder
@NoArgsConstructor  // Genera el constructor vacío DetallePlan() que te está pidiendo el IDE
@AllArgsConstructor
public class DetallePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private PlanEntrenamiento plan;

    @ManyToOne
    @JoinColumn(name = "entrenamiento_id")
    private Entrenamiento entrenamiento;

    private Integer diaSemana; // 1 = Lunes, 2 = Martes...
    private Integer orden; // 1º Ejercicio, 2º Ejercicio...
    
    private Integer series;
    private Integer repeticiones;
}