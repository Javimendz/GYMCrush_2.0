package com.example.skynet.data.remote.dto;

public class TicketRequestDto {
    private String asunto;
    private String mensaje;

    public TicketRequestDto() {}

    public TicketRequestDto(String asunto, String mensaje) {
        this.asunto = asunto;
        this.mensaje = mensaje;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
