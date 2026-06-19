package com.backend.event;

import com.backend.domain.enums.TipoNotificacion;

/**
 * Evento que se dispara cuando se crea un nuevo tutorial.
 * Transporta los datos necesarios para notificar a los usuarios.
 */
public record TutorialEvent(
    Long tutorialId,
    String titulo,
    String mensaje,
    TipoNotificacion tipo
) {}