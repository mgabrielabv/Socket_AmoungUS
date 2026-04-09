package com.MariaBermudez.DataBase;

public class Jugador {

    private int id;
    private String nombre;
    private String color;
    private String contrasena;

    public Jugador() {}

    public Jugador(String nombre, String color, String contrasena) {
        this.nombre = nombre;
        this.color = color;
        this.contrasena = contrasena;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
