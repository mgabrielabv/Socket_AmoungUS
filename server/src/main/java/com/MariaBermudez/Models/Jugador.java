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
        this.x = 400;
        this.y = 300;
        this.estado = "quieto";
    }
}