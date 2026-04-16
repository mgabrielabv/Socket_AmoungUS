package com.MariaBermudez.DataBase;

import java.sql.*;
import java.time.LocalDateTime;

public class PartidaDAO {

    // crea una partida nueva y devuelve su id
    public int crearPartida() {
        String sql = "INSERT INTO partidas (fecha, cantidad_jugadores) VALUES (?, 0) RETURNING id";
        try {
            Connection conn = ConexionBD.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, LocalDateTime.now().toString().substring(0, 19));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Error al crear partida: " + e.getMessage());
        }
        return -1;
    }

    // registra que un jugador participo en una partida
    public void agregarJugador(int idPartida, String nombreJugador) {
        String sql = "INSERT INTO jugadores_partida (id_partida, nombre_jugador) VALUES (?, ?)";
        try {
            Connection conn = ConexionBD.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idPartida);
            stmt.setString(2, nombreJugador);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al agregar jugador a partida: " + e.getMessage());
        }
    }
}
