package com.MariaBermudez.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public Jugador obtenerJugador(String nombre) {
        String sql = "SELECT * FROM jugadores WHERE nombre = ?";
        try {
            Connection conn = ConexionBD.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
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
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    // devuelve todos los jugadores de la bd
    public List<Jugador> listarJugadores() {
        List<Jugador> lista = new ArrayList<>();
        String sql = "SELECT * FROM jugadores";
        try {
            Connection conn = ConexionBD.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("id"));
                j.setNombre(rs.getString("nombre"));
                j.setColor(rs.getString("color"));
                j.setContrasena(rs.getString("contrasena"));
                lista.add(j);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminarJugador(String nombre) {
        String sql = "DELETE FROM jugadores WHERE nombre = ?";
        try {
            Connection conn = ConexionBD.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    // para probar que funciona
    public static void main(String[] args) {
        JugadorDAO dao = new JugadorDAO();

        System.out.println("--- Registro ---");
        System.out.println(dao.registrarJugador("Carlos", "rojo", "1234"));
        System.out.println(dao.registrarJugador("Maria", "azul", "abcd"));
        System.out.println(dao.registrarJugador("Carlos", "verde", "5678")); // deberia ser false

        System.out.println("--- Login ---");
        Jugador j = dao.iniciarSesion("Carlos", "1234");
        if (j != null) {
            System.out.println("Login OK: " + j.getNombre() + " color: " + j.getColor());
        } else {
            System.out.println("Usuario o contrasena incorrectos");
        }

        System.out.println("--- Jugadores en BD ---");
        for (Jugador jugador : dao.listarJugadores()) {
            System.out.println(jugador.getNombre() + " - " + jugador.getColor());
        }
    }
}

// RESULTADO ESPERADO:
// true / true — Carlos y Maria se registraron
// false + mensaje de duplicado — Carlos no se pudo registrar de nuevo (la restriccion UNIQUE funciona)
// Login OK con el color correcto
// Los dos jugadores aparecen en la lista