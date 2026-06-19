package com.backend.domain;

//Imports
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa los datos de salud y progreso físico de un usuario.
 * <p>
 * Almacena métricas corporales, IMC calculado y seguimiento
 * del progreso físico con timestamps automáticos.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "salud")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Salud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Peso del usuario en kilogramos.
     * <p>
     * Obligatorio, para cálculo de IMC y seguimiento.
     * </p>
     */
    @Column(name = "peso", nullable = false)
    private Double peso;

    /**
     * Estatura del usuario en metros.
     * <p>
     * Obligatorio, para cálculo de IMC.
     * </p>
     */
    @Column(name = "estatura", nullable = false)
    private Double estatura;

    /**
     * Nivel de actividad física del usuario.
     * <p>
     * Obligatorio, clasifica el estado físico (ej: sedentario, activo, atleta).
     * </p>
     */
    @Column(name = "nivel_actividad", length = 50)
    private String nivelActividad;

    /**
     * Comentarios o notas adicionales sobre la medición.
     * <p>
     * Opcional, permite texto largo para observaciones.
     * </p>
     */
    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    /**
     * Fecha y hora en que se realizó la medición.
     * <p>
     * Obligatorio, no modificable después de creación.
     * </p>
     */
    @Column(name = "fecha_medicion", nullable = false, updatable = false)
    private LocalDateTime fechaMedicion;

    /**
     * Índice de Masa Corporal calculado.
     * <p>
     * Calculado automáticamente basado en peso y estatura.
     * </p>
     */
    private Double imc;

    /**
     * Usuario al que pertenece este registro de salud.
     * <p>
     * Relación muchos-a-uno con carga eager para obtener
     * información del usuario automáticamente.
     * </p>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    /**
     * Método callback que se ejecuta antes de persistir el registro.
     * <p>
     * Establece la fecha de medición actual si no se especifica
     * y calcula el IMC automáticamente.
     * </p>
     */
    @PrePersist
    protected void antesDeGuardar() {
        // Gestionar la fecha de medición
        if (this.fechaMedicion == null) {
            this.fechaMedicion = LocalDateTime.now();
        }

        // Calcular el IMC automáticamente
        if (this.peso != null && this.estatura != null && this.estatura > 0) {
            // Fórmula: peso / (estatura * estatura)
            double calculo = this.peso / (Math.pow(this.estatura, 2));
            this.imc = Math.round(calculo * 100.0) / 100.0; // Redondeo a 2 decimales
        }
    }
}
