package com.example.skynet.ui.social;

public class MensajeChat {
    private String id;
    private String usuario;
    private Long usuarioId;
    private String texto;
    private String hora;
    private boolean esMio;

    public MensajeChat() {}

    public MensajeChat(String id, String usuario, Long usuarioId, String texto, String hora) {
        this.id = id;
        this.usuario = usuario;
        this.usuarioId = usuarioId;
        this.texto = texto;
        this.hora = hora;
    }

    public MensajeChat(String usuario, String texto, String hora) {
        this(null, usuario, null, texto, hora);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public boolean isEsMio() { return esMio; }
    public void setEsMio(boolean esMio) { this.esMio = esMio; }
}
