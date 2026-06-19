package com.backend.event;


import com.backend.domain.enums.TipoNotificacion;

public record ReservaEvent(
    Long reservaId,
    String mensaje, 
    String titulo, 
    TipoNotificacion tipo
) {}