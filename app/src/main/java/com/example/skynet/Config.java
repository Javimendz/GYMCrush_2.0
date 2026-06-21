package com.example.skynet;

public class Config {
    // Si usas EMULADOR: 10.0.2.2
    // Si usas DISPOSITIVO FÍSICO: Pon la IP de tu PC (ej: 192.168.1.XX)
    //public static final String IP_SERVIDOR = "192.168.1.96";
    public static final String IP_SERVIDOR = "10.0.2.2";
    public static final String PUERTO = "8080";

    // --- ENTORNO DE PRODUCCIÓN (RENDER) ---
    //public static final String BASE_URL = "https://gymcrush-tfg.onrender.com/";
    //public static final String WS_URL = "wss://gymcrush-tfg.onrender.com/ws/websocket";

    // --- ENTORNO LOCAL ---
    public static final String BASE_URL = "http://" + IP_SERVIDOR + ":" + PUERTO + "/";
     public static final String WS_URL = "ws://" + IP_SERVIDOR + ":" + PUERTO + "/ws/websocket";
}
