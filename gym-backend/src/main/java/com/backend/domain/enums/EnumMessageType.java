package com.backend.domain.enums;




import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumMessageType {
    CHAT, 
    JOIN, 
    LEAVE;

    /**
     * Este método se encarga de convertir el String que llega desde Android
     * al valor correcto del Enum en Java.
     */
    @JsonCreator
    public static EnumMessageType fromString(String value) {
        if (value == null) return null;
        
        String cleanValue = value.trim().toUpperCase();

        // Si Android envía "TEXTO", lo tratamos internamente como "CHAT"
        if ("TEXTO".equals(cleanValue)) {
            return CHAT;
        }

        try {
            return EnumMessageType.valueOf(cleanValue);
        } catch (IllegalArgumentException e) {
            // Si llega algo totalmente desconocido, devolvemos CHAT por defecto
            // o podrías lanzar una excepción personalizada.
            return CHAT; 
        }
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}