package com.example.skynet.data.model;

public class CartItem {
    private Suplemento suplemento;
    private int cantidad;

    public CartItem(Suplemento suplemento, int cantidad) {
        this.suplemento = suplemento;
        this.cantidad = cantidad;
    }

    public Suplemento getSuplemento() { return suplemento; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getSubtotal() { return suplemento.getPrecio() * cantidad; }
}
