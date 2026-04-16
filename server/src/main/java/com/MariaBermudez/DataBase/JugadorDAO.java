package com.MariaBermudez.DataBase;

import java.sql.*;

// cd server
// mvn clean compile exec:java "-Dexec.mainClass=com.MariaBermudez.DataBase.JugadorDAO"

public class JugadorDAO {

    // insertar jugador nuevo
    public boolean registrarJugador(String nombre, String color, String contrasena) {
        String sql = "INSERT INTO jugadores (nombre, color, contrasena) VALUES (?, ?, ?)";
        try {


            Connection conn = ConexionBD.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, color);
            stmt.setString(3, contrasena);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar jugador: " + e.getMessage());
            return false;
        }
    }

    // verificar usuario y contrasena
    public Jugador iniciarSesion(String nombre, String contrasena) {
        String sql = "SELECT * FROM jugadores WHERE nombre = ? AND contrasena = ?";
        try {
            Connection conn = ConexionBD.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, contrasena);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("id"));
                j.setNombre(rs.getString("nombre"));
                j.setColor(rs.getString("color"));
                j.setContrasena(rs.getString("contrasena"));
                return j;
            }
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesion: " + e.getMessage());
        }
        return null;
    }


}
