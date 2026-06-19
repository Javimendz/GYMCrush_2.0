
package com.backend.domain.enums;

public enum EnumNivelActividad {
    SEDENTARIO(1.2), LIGERO(1.375), MODERADO(1.55), INTENSO(1.725), ATLETA(1.9);
    private final double factor;
    EnumNivelActividad(double f) { this.factor = f; }
    public double getFactor() { return factor; }

}