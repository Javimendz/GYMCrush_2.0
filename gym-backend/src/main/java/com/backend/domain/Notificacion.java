package com.backend.domain;

import java.time.LocalDateTime;

import com.backend.domain.enums.TipoNotificacion;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Data
@Builder
@NoArgsConstructor // Requerido por JPA
@AllArgsConstructor
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String titulo;
    private String mensaje;
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @Builder.Default
    private boolean leido = false;
    
    @ManyToOne // Una notificación pertenece a un Ticket
    @JoinColumn(name = "ticket_id") // Nombre en la tabla de la BD
    private Ticket ticket;
    
    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo; // Confirmacion, Recordatorio, Cancelacion
}