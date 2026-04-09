package com.MariaBermudez.Models;

import java.io.Serializable;

public class Jugador implements Serializable {
    public String id;
    public int x, y;
    public String color;
    public String estado;

    public Jugador(String id, String color) {
        this.id = id;
        this.color = color;
        this.x = 100;
        this.y = 100;
        this.estado = "quieto";
    }
}