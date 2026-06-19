package com.backend.domain;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Entidad que representa una actividad deportiva del gimnasio.
 * <p>
 * Almacena información sobre disciplinas deportivas ofrecidas,
 * incluyendo duración, precio, ubicación y horarios asociados.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "actividades")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   

    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion;

    @Column(name = "duracion", nullable = false)
    private Integer duracion;

    @Column(name = "sala", nullable = false, length = 50)
    private String sala;

    @Column(name = "precio", nullable = false)
    private Integer precio;

    @OneToMany(mappedBy = "actividad", fetch = FetchType.EAGER, cascade = CascadeType.ALL, 
           orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties("actividad")
    private List<Horario> horarios;


    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    @JsonIgnoreProperties("actividades")
    private Categoria categoria;
}