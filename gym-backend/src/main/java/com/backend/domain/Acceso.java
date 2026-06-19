package com.backend.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un registro de acceso al gimnasio.
 * <p>
 * Almacena el historial de entradas y salidas de usuarios,
 * incluyendo control mediante tokens QR y registro de timestamps.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name="accesos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acceso {
    /**
     * Identificador único del registro de acceso.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que realiza el acceso.
     * <p>
     * Relación muchos-a-uno con la entidad Usuario.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name="usuario_id")
    private Usuario usuario;
    
    /**
     * Fecha y hora de entrada al gimnasio.
     * <p>
     * Registrada automáticamente cuando el usuario escanea su QR.
     * </p>
     */
    private LocalDateTime fechaHoraEntrada;
    
    /**
     * Fecha y hora de salida del gimnasio.
     * <p>
     * Registrada cuando el usuario sale, puede ser null si aún está dentro.
     * </p>
     */
    private LocalDateTime fechaHoraSalida;
    
    /**
     * Tipo de registro de acceso.
     * <p>
     * Puede ser "ENTRADA" o "SALIDA" para diferenciar
     * las operaciones de control de aforo.
     * </p>
     */
    private String tipo;

}
