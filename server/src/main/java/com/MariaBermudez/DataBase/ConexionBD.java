package com.MariaBermudez.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static Connection conexion = null;
    private static final String URL = "jdbc:postgresql://localhost:5433/amongus";
    private static final String USER = "admin";
    private static final String PASS = "admin123";

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conexion;
    }
}